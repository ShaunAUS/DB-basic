package hellof.repository.ex;

/**
 * 우리가 직접만든 예외이기때문에 JDBC 나 특정기술에  의존하지 않는다
 */
public class MyDuplicateException extends MyDbException{

    public MyDuplicateException() {
    }

    public MyDuplicateException(String message) {
        super(message);
    }

    public MyDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDuplicateException(Throwable cause) {
        super(cause);
    }

}
