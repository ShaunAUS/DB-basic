package hellof.repository;

import hellof.domain.Member;
import hellof.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 예외 누수문제 해결하기
 * 체크예외 런타임 으로 변환
 * MemberRepository Interface 사용
 * throws SQLException 제거
 */

@Slf4j
public class MemberRepositoryV4_1 implements MemberRepository {

    //DB 커넥션
    private final DataSource dataSource;

    public MemberRepositoryV4_1(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Member findById(String memberId)  {
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
            throw new MyDbException(e);
        }finally {
            close(con,pstmt,rs);
        }
    }

    @Override
    public Member save(Member member)  {
        String sql = "insert into member(member_id, money) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt =null;  // 이걸로 DB에 쿼리문을 날린다  + sql injection 공격 방 -> 파라미터 바인딩 방법을 사용해야함


        try {

            //DB 연결
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            //query values binding
            pstmt.setString(1,member.getMemberId());
            pstmt.setInt(2,member.getMoney());

            //실행
            //영향 받은 로우수 만큼 int 반환
            pstmt.executeUpdate();
            return member;

        } catch (SQLException e) {
            throw new MyDbException(e);

        }finally {
            //외부 리소스 Tcp/Ip 사용 닫아줘야함
            close(con,pstmt,null);
        }
    }

    @Override
    public void update(String memberId , int money)  {

        String sql = "update MEMBER set money = ? where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt  = null;


        try {

            //DB 연결
            con = getConnection();

            pstmt = con.prepareStatement(sql);

            //query values binding
            pstmt.setInt(1,money);
            pstmt.setString(2,memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}", resultSize);
            pstmt.executeUpdate();


        } catch (SQLException e) {
            throw new MyDbException(e);
        }finally {
            //외부 리소스 Tcp/Ip 사용 닫아줘야함
            close(con,pstmt,null);
        }

    }


    @Override
    public void delete(String memberId)  {

        String sql = "delete from MEMBER where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;



        try {
            //DB 연결
            con = getConnection();

            pstmt = con.prepareStatement(sql);

            //query values binding
            pstmt.setString(1,memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new MyDbException(e);
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
