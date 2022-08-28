package hellof.service;

import hellof.domain.Member;
import hellof.repository.MemberRepositoryV2;
import hellof.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션  - 트랜잭션 매니저
 *
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {


    //private final DataSource dataSource;


    //트랜잭션 매니저
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;



    public void accountTransfer(String fromId, String toId, int amount) throws SQLException {

        //트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try{
            //비즈니스로직
            bizLogic(fromId, toId, amount);
            transactionManager.commit(status); //성공시 커밋
        }catch (SQLException e) {
            transactionManager.rollback(status); // 예외시 롤백
            throw new IllegalStateException(e);
        }finally {
            //트랜잭션 매니저가 알아서 닫아준다.
        }
    }

    private void bizLogic(String fromId, String toId, int amount) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - amount);

        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + amount);
    }

    private void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalArgumentException("이체중 예외 발생");
        }
    }

}

