package hellof.exception.basic;

import lombok.extern.slf4j.Slf4j;

import java.net.ConnectException;
import java.sql.SQLException;

/**
 *  컨트룰러와 서비스 단에서는 해당 익셉션을 처리할수 없으며 throw로 던져줘야한다.
 *  또한 서비스단에서 SQLException을 던지면 그 JDBC 기술에 의존하게 되는 관계가 생기며  JDBC기술이 바뀌면 해당 서비스단 코드들도 다바꿔줘야 하는 문제 발생
 *  따라서 런타입 익셉션을 사용하자
 */

@Slf4j
public class CheckedAppTest {


    static class Controller{
        public void callService() throws SQLException, ConnectException {
            Service service = new Service();
            service.bizLogic();
        }
    }


    static class Service{
         NetWorkClient netWorkClient = new NetWorkClient();
         Repository repository = new Repository();

         public void bizLogic() throws ConnectException, SQLException {
             netWorkClient.call();
             repository. call();
         }

    }

    static class NetWorkClient{
        public void call() throws ConnectException {
            throw new ConnectException("연결 실패");
        }
    }

    static class Repository{

        public void call() throws SQLException {
             throw new SQLException("ex");
        }
    }

}
