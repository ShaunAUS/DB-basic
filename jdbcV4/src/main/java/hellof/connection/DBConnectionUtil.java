package hellof.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hellof.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection() {
        try {

            //드라이버 매니저가 드라이버 찾아줌
            //그래서 DB를 바꿔도  connection 안바꿔도된다.
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection = {} , class = {}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
