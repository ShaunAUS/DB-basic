package hellof.repository;

import hellof.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 트랜잭션 매니저
 * DataSourceUtils.getConnection()  - 트랜잭션 동기화 매니저에 커넥션 있으면 그거가져오고 없으면 새로생성(dataSource를 통한 커넥션획득 -> 트랜잭션 시작)
 * DataSourceUtils.releaseConnection()   - 트랜잭션 커넥션은 유지 , 일반은 닫는다.
 */

@Slf4j
public class MemberRepositoryV3 {

    //DB 커넥션
    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }


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

    public void close(Connection con , Statement statement , ResultSet resultSet) {

        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);

        //DataSoruceUtils 리소스 닫기 (트랜잭션 동기화 리소스 닫기)
        //트랜잭션을 위한 커넥션은 닫지않고 유지시켜줌 // 일반 커넥션은 걍 닫음
        DataSourceUtils.releaseConnection(con, dataSource);

        //JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() throws SQLException {


        //Connection connection = dataSource.getConnection();


        // 트랜잭션 동기화 하려면 DataSourceUtils 사용
        // 트랜잭션 동기화 매니저에 미리 만들어둔 트랜잭션 커넥션을 가져와 사용한다.
       Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("getConnection = {} , class = {}",con,con.getClass());
        return con;
    }
}
