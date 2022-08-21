package hellof.repository;

import com.zaxxer.hikari.HikariDataSource;
import hellof.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hellof.connection.ConnectionConst.*;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    //각 테스트가 실행되기직전 한번 호출
    @BeforeEach
    void beforeEach(){

        //기본 DriverManager = 항생 새로운 커넥션 획득( 쿼리하나 실행할떄마다 )
        //너무 성능이 안나오니 hikari 커넥션 풀링 사용
        /*DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PASSWORD);
        repository = new MemberRepositoryV1(dataSource);*/


        //커넥션풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        repository = new MemberRepositoryV1(dataSource);


    }

    @Test
    void crud() throws SQLException {

        //save
        Member member = new Member("memberId1", 10000);
        repository.save(member);

        //find
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMeber = {}", findMember);

        // @Data 는 내부적으로 equlas 재정의 되서 객체의 내용비교
        // 그래서 == false 가 나오고 , equlas 는 true 가 나옴
        Assertions.assertThat(findMember).isEqualTo(member);


        //update
        repository.update(member.getMemberId(),20000);
        Member updatedMember = repository.findById(member.getMemberId());
        Assertions.assertThat(updatedMember.getMoney()).isEqualTo(20000);

        //delete
        repository.delte(member.getMemberId());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}