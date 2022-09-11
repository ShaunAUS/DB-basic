package hellof.service;

import hellof.domain.Member;
import hellof.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션  - 트랜잭션 탬플릿
 *
 */
@Slf4j
public class MemberServiceV3_2 {


    //private final DataSource dataSource;


    //트랜잭션 매니저
    //private final PlatformTransactionManager transactionManager;

    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager platformTransactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(platformTransactionManager); // 트랜잭션 템플릿 쓰기위해서는 트랜잭션 매니저 필요함
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int amount) throws SQLException {

        //탬플릿이  성공이면 커밋 실패하면 롤백 알아서 해줌
        txTemplate.executeWithoutResult(status -> {

            try {
                bizLogic(fromId, toId, amount);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });

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

