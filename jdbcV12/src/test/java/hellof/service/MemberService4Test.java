package hellof.service;

import hellof.connection.ConnectionConst;
import hellof.domain.Member;
import hellof.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 트랜잭션 - DataSource , TransactionManager 자동 등록 = SpringBoot
 *
 */
@Slf4j
@SpringBootTest
class MemberService4Test {


    public static final String MEMBER_A ="memberA";
    public static final String MEMBER_B ="memberB";
    public static final String MEMBER_EX ="EX";
    @Autowired
    private MemberRepositoryV3 memberReposiotry;
    @Autowired
    private MemberServiceV3_3 memberSerivce;


    //@Transactional 을 쓰려면 관된것들 Bean 등록 해야함 - > @SpringBootTest
    @TestConfiguration
    static class testConfig{

        //SpringBoot - DataSource 자동으로 생성 -> bean 등록
        private final DataSource dataSource;

        public testConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        //커넥션
/*        @Bean
        DataSource dataSource(){
            return new DriverManagerDataSource(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD);
        }
        */
        //트랜잭션 매니저
        @Bean
        PlatformTransactionManager transactionManager(DataSource dataSource){
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        MemberRepositoryV3 memberRepository(DataSource dataSource){
            return new MemberRepositoryV3(dataSource);
        }
        @Bean
        MemberServiceV3_3 memberService(MemberRepositoryV3 memberRepository){
            return new MemberServiceV3_3(memberRepository);
        }

    }

    //@Trasactional 은 비즈니스 로직을 상속받아 알아서 트랜잭션+ 비즈니스 로직으로 프록시 생성 해줌
    @Test
    void AopCheck(){
        log.info("memberService class = {}" , memberSerivce.getClass());
        log.info("memberRepositoryu class = {}" , memberReposiotry.getClass());
        Assertions.assertThat(AopUtils.isAopProxy(memberSerivce)).isTrue();
        Assertions.assertThat(AopUtils.isAopProxy(memberReposiotry)).isFalse();

    }



   /* @BeforeEach
    void befores() {
        //커넥션을 만들어서 (=dataSource)  트랜잭션 매니저에게 전달
        DriverManagerDataSource dataSource = new DriverManagerDataSource(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD);
        memberReposiotry = new MemberRepositoryV3(dataSource); //DB 커넥션
        memberSerivce = new MemberServiceV3_3(memberReposiotry);  // @Transactional가 트랜잭션 프록시 생성해서 트랜잭션 매니저도 만들어줌
    }*/

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