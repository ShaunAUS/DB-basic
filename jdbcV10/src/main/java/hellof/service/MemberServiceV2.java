package hellof.service;

import hellof.domain.Member;
import hellof.repository.MemberRepositoryV1;
import hellof.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션  - 파라미터 연동, 풀을 고려한 종료
 * -> 가장 큰 문제점은 코드의 복잡성과 트랜잭션처리 코드가 비즈니스 로직보다 더많아짐
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {
    private final MemberRepositoryV2 memberRepository;
    private final DataSource dataSource;

    public void accountTransfer(String fromId, String toId, int amount) throws SQLException {

        Connection con = dataSource.getConnection();

        try{

        // 트랜잭션 시작
        con.setAutoCommit(false);

            //비즈니스로직
            bizLogic(fromId, toId, amount, con);

        // 트랜잭션 종료
        con.commit();

        }catch (SQLException e) {
            con.rollback(); // 예외시 롤백
            throw new IllegalStateException(e);
        }finally {
            if(con != null){

                try{
                con.setAutoCommit(true);  // false 하고 반환하면 그대로 풀에 들어가 다음 커넥션 사용시 autoCommit false로 시작함
                con.close();
                }

                catch (Exception e){
                log.info("error",e);
                }
            }
        }
    }

    private void bizLogic(String fromId, String toId, int amount, Connection con) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - amount);

        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + amount);
    }

    private void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalArgumentException("이체중 예외 발생");
        }
    }

}

