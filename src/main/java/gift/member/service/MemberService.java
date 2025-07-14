package gift.member.service;

import gift.member.dto.AdminMemberCreateRequestDto;
import gift.member.dto.AdminMemberGetResponseDto;
import gift.member.dto.AdminMemberUpdateRequestDto;
import gift.member.dto.RegisterRequestDto;
import gift.member.dto.TokenResponseDto;
import java.util.List;

public interface MemberService {

    TokenResponseDto registerMember(RegisterRequestDto registerRequestDto);

    void findMemberByEmail(RegisterRequestDto registerRequestDto);

    void saveMember(AdminMemberCreateRequestDto adminMemberCreateRequestDto);

    List<AdminMemberGetResponseDto> findAllMembers();

    AdminMemberGetResponseDto findMemberById(Long memberId);

    void updateMember(Long memberId, AdminMemberUpdateRequestDto adminMemberUpdateRequestDto);

    void deleteMember(Long memberId);
}
