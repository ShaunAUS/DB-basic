package hellof.service;

import hellof.connection.ConnectionConst;
import hellof.domain.Member;
import hellof.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 트랜잭션 테스트 -트랜잭션 템플릿 사용
 */
@RequiredArgsConstructor
class MemberServiceV3_2Test {

    public static final String MEMBER_A ="memberA";
    public static final String MEMBER_B ="memberB";
    public static final String MEMBER_EX ="EX";

    private MemberRepositoryV3 memberReposiotry;
    private MemberServiceV3_2 memberSerivce;

    @BeforeEach
    void befores() {
        //커넥션을 만들어서 (=dataSource)  트랜잭션 매니저에게 전달
        DriverManagerDataSource dataSource = new DriverManagerDataSource(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD);
        memberReposiotry = new MemberRepositoryV3(dataSource); //DB 커넥션
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource); //트랜잭션 매니저
        memberSerivce = new MemberServiceV3_2(transactionManager,memberReposiotry);
    }

    @AfterEach
    void afterEach() throws SQLException {
        memberReposiotry.delte(MEMBER_A);
        memberReposiotry.delte(MEMBER_B);
        memberReposiotry.delte(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {

        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_B, 10000);
        memberReposiotry.save(memberA);
        memberReposiotry.save(memberEx);

        //when
        assertThatThrownBy(() -> memberSerivce.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalArgumentException.class)//예외 타입이 일치하는지 확인
                .hasMessage("잔액이 부족합니다.");


        //then
        Member findMemberA = memberReposiotry.findById(memberA.getMemberId());
        Member findMemberB = memberReposiotry.findById(memberEx.getMemberId());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외 발생")
    void accountTransferEx() throws SQLException {

        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_EX, 10000);
        memberReposiotry.save(memberA);
        memberReposiotry.save(memberB);

        //when

        memberSerivce.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        //then
        Member findMemberA = memberReposiotry.findById(memberA.getMemberId());
        Member findMemberB = memberReposiotry.findById(memberB.getMemberId());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }
}