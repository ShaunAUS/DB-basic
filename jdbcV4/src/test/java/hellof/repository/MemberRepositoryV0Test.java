package hellof.repository;

import hellof.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    @Test
    void crud() throws SQLException {

        MemberRepositoryV0 repository = new MemberRepositoryV0();

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
    }
}