package hellof.connection;

//final  즉 상수로 모아 만든거기때문에 객체 생성 못하게 abstact 추가
public abstract class ConnectionConst {

    public static final String URL = "jdbc:h2:tcp://localhost/~/dbtest";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
