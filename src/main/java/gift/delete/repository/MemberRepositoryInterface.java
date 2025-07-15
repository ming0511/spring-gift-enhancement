package gift.delete.repository;

import gift.member.entity.Member;
import java.util.List;

public interface MemberRepositoryInterface {

    Long saveMember(Member member);

    Member findMemberByEmail(String email);

    List<Member> findAllMembers();

    Member findMemberById(Long memberId);

    void updateMember(Member member);

    void deleteMember(Long memberId);

    Boolean existsByEmail(String email);
}
