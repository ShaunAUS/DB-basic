package hellof.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.h2.engine.User;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hellof.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {


    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }

    /**
     * 커넥션 풀
     */
    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {

        //커넥션풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);

        useDataSource(dataSource);
        Thread.sleep(1000);
    }

    //Repository 는 dataSource 만 알면되서 독립성이 높아진다.
    // DriverManager와 가장 큰 차이점은 설정과/사용의 분리
    void dataSourceDriverManager() throws SQLException {

        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {

        //풀에 커넥션이 없경우 시간이 좀걸리지만 내부적으로 커넥션을 알아서 얻어온다.
        //풀이 커넥션 10개가 넘으면 wating 으로 넘어감
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }
}
