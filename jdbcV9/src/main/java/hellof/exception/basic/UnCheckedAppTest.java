package hellof.exception.basic;

import lombok.extern.slf4j.Slf4j;

import java.net.ConnectException;
import java.sql.SQLException;


/**
 * 체크예외를 런타임예외로 변경해서 던져준다. 그러면 throw가 붙지 않아 코드도 깔끔해지고 의존관계도 해결됀다
 * 런타임시에는 문서화를 잘해주자
 * 하지만 런타임예외를 사용할시에 따로 처리(변환) 코드 작성필요
 *
 *
 * 스택트레이스 넣어주는거 잊지말자 ! 익셉션이 왜일어났는지 알수없음 안넣어주면
 * 예외를 전환할때는 꼭 기존예외를 넣어주자
 */

@Slf4j
public class UnCheckedAppTest {


    static class Controller{
        public void callService() throws SQLException, ConnectException {
            Service service = new Service();
            service.bizLogic();
        }
    }


    static class NetWorkClient{
        public void call()  {
            throw new RuntimeConnectException("연결 실패");
        }
    }

    static class Service{
        NetWorkClient netWorkClient = new NetWorkClient();

         Repository repository = new Repository();

         public void bizLogic()  {
             netWorkClient.call();
             repository. call();
         }

    }

    static class Repository{

        public void call()  {
            try {
                runSql();

                // SQLException을 런타임 익셉션으로 변환
            } catch (SQLException e) {

                //e를 꼭넣어줘야함 그래야지 익셉션이 터진 이유를 알수 있다.
                throw new RuntimeSQLException(e);
            }
        }

        public void runSql() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException{
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException{
        public RuntimeSQLException(Throwable cause) {  //이전 익셉션도 매개변수로 같이 담을수 있다.
            super(cause);
        }
    }

}
