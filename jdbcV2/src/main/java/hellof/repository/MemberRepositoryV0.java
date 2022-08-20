package hellof.repository;

import hellof.connection.DBConnectionUtil;
import hellof.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC -DrivateManager 사용
 */

@Slf4j
public class MemberRepositoryV0 {


    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt =null;
        ResultSet rs = null;


        try {
            con =getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);

            rs = pstmt.executeQuery();

            //next = 데이터가 있는지 확인, 한번은 호출해줘야함
            if(rs.next()){

                //조회 결과 가져와서 넣어주기
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;

            }else{
                throw new NoSuchElementException("member not found memberId" + memberId);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {
            close(con,pstmt,rs);
        }


    }

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt =null;  // 이걸로 DB에 쿼리문을 날린다  + sql injection 공격 방 -> 파라미터 바인딩 방법을 사용해야함


        //DB 연결
        con = getConnection();

        try {
            pstmt = con.prepareStatement(sql);

            //query values binding
            pstmt.setString(1,member.getMemberId());
            pstmt.setInt(2,member.getMoney());

            //실행
            //영향 받은 로우수 만큼 int 반환
            pstmt.executeUpdate();
            return member;

        } catch (SQLException e) {
            log.error("DB error");
            throw e;
        }finally {
            //외부 리소스 Tcp/Ip 사용 닫아줘야함
            close(con,pstmt,null);
        }
    }

    public void update(String memberId , int money) throws SQLException {

        String sql = "update MEMBER set money = ? where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt  = null;

        //DB 연결
        con = getConnection();

        try {

            pstmt = con.prepareStatement(sql);

            //query values binding
            pstmt.setInt(1,money);
            pstmt.setString(2,memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}", resultSize);
            pstmt.executeUpdate();


        } catch (SQLException e) {
            log.error("DB error");
            throw e;
        }finally {
            //외부 리소스 Tcp/Ip 사용 닫아줘야함
            close(con,pstmt,null);
        }

    }

    public void delte(String memberId) throws SQLException {

        String sql = "delete from MEMBER where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        //DB 연결
        con = getConnection();

        try {

            pstmt = con.prepareStatement(sql);

            //query values binding
            pstmt.setString(1,memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("DB error");
            throw e;
        }finally {
            //외부 리소스 Tcp/Ip 사용 닫아줘야함
            close(con,pstmt,null);
        }

    }

    public void close(Connection con , Statement statement , ResultSet resultSet){

        // 리소스 닫는 와중에도 exception 이 발생하면 나머지가 닫히지 않기 때문에 try-catch 처리해줘야함
        if(resultSet != null){

            try {
                resultSet.close();
            } catch (SQLException e) {
                log.error("error",e);
            }
        }

        if(statement != null){

            try {
                statement.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }

        if(con != null){

            try {
                con.close();
            } catch (SQLException e) {
                log.error("error",e );
            }
        }

    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
