package gift.member.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.member.builder.MemberBuilder;
import gift.member.entity.Member;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository members;

    @Test
    void saveMember() {
        Member expected = MemberBuilder.aMember().build();
        Member actual = members.save(expected);
        assertAll(
            () -> assertThat(actual.getMemberId()).isNotNull(),
            () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()),
            () -> assertThat(actual.getPassword()).isEqualTo(expected.getPassword()),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getRole()).isEqualTo(expected.getRole())
        );
    }

    @Test
    void findMemberByEmail() {
        String expected = "one@email.com";
        members.save(MemberBuilder.aMember().withEmail(expected).build());

        String actual = members.findByEmail(expected).get().getEmail();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAllMembers() {
        members.save(MemberBuilder.aMember().withEmail("one@email.com").build());
        members.save(MemberBuilder.aMember().withEmail("two@email.com").build());
        members.save(MemberBuilder.aMember().withEmail("three@email.com").build());

        List<Member> memberList = members.findAll();

        assertThat(memberList).hasSize(3);
    }

    @Test
    void findMemberById() {
        Member expected = MemberBuilder.aMember().build();
        Member savedMember = members.save(expected);

        Member actual = members.findById(savedMember.getMemberId()).get();
        assertAll(
            () -> assertThat(actual.getMemberId()).isNotNull(),
            () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()),
            () -> assertThat(actual.getPassword()).isEqualTo(expected.getPassword()),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getRole()).isEqualTo(expected.getRole())
        );
    }

    @Test
    void updateMember() {
    }

    @Test
    void deleteMember() {
        Member expected = MemberBuilder.aMember().build();
        Member savedMember = members.save(expected);

        members.delete(savedMember);

        assertThat(members.findById(savedMember.getMemberId())).isEmpty();
    }

    @Test
    void existsByEmail() {
        Member expected = MemberBuilder.aMember().build();
        Member savedMember = members.save(expected);

        assertThat(members.existsByEmail(savedMember.getEmail())).isTrue();
    }
}
