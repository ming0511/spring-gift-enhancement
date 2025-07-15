package gift.member.service;

import gift.exception.member.EmailAlreadyExistsException;
import gift.exception.member.MemberNotFoundException;
import gift.member.dto.AdminMemberCreateRequestDto;
import gift.member.dto.AdminMemberGetResponseDto;
import gift.member.dto.AdminMemberUpdateRequestDto;
import gift.member.dto.RegisterRequestDto;
import gift.member.dto.TokenResponseDto;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.member.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository members;

    public MemberServiceImpl(MemberRepository members) {
        this.members = members;
    }

    @Override
    public TokenResponseDto registerMember(RegisterRequestDto registerRequestDto) {
        if (members.existsByEmail(registerRequestDto.email())) {
            throw new EmailAlreadyExistsException("이미 사용 중인 이메일입니다.");
        }

        Member member = new Member(registerRequestDto.email(), registerRequestDto.password(),
            registerRequestDto.name(), registerRequestDto.role());

        Member savedMember = members.save(member);

        String token = new JwtTokenProvider().generateToken(savedMember.getMemberId(),
            savedMember.getName(),
            savedMember.getRole());

        return new TokenResponseDto(token);
    }

    @Override
    public void findMemberByEmail(RegisterRequestDto registerRequestDto) {

        try {
            members.findByEmail(registerRequestDto.email());
        } catch (EmptyResultDataAccessException e) {
            throw new MemberNotFoundException(
                "이메일이 존재하지 않습니다. email =" + registerRequestDto.email());
        }
    }

    @Override
    public void saveMember(AdminMemberCreateRequestDto adminMemberCreateRequestDto) {

        Member member = new Member(adminMemberCreateRequestDto.email(),
            adminMemberCreateRequestDto.password(), adminMemberCreateRequestDto.name(),
            adminMemberCreateRequestDto.role());

        members.save(member);
    }

    @Override
    public List<AdminMemberGetResponseDto> findAllMembers() {
        List<Member> memberList = members.findAll();

        return memberList.stream()
            .map(member -> new AdminMemberGetResponseDto(
                member.getMemberId(),
                member.getEmail(),
                member.getPassword(),
                member.getName(),
                member.getRole()
            ))
            .collect(Collectors.toList());
    }

    @Override
    public AdminMemberGetResponseDto findMemberById(Long memberId) {
        Member member = members.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        return new AdminMemberGetResponseDto(member.getMemberId(), member.getEmail(),
            member.getPassword(), member.getName(), member.getRole());
    }

    @Override
    public void updateMember(Long memberId,
        AdminMemberUpdateRequestDto adminMemberUpdateRequestDto) {

        Member member = new Member(memberId,
            adminMemberUpdateRequestDto.email(), adminMemberUpdateRequestDto.password(),
            adminMemberUpdateRequestDto.name(), adminMemberUpdateRequestDto.role());

        update(memberId, member);
    }

    @Override
    public void deleteMember(Long memberId) {
        members.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        members.deleteById(memberId);
    }

    @Transactional
    public void update(Long id, Member member) {
        Member foundMember = members.findById(id)
            .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        foundMember.setEmail(member.getEmail());
        foundMember.setPassword(member.getPassword());
        foundMember.setName(member.getName());
        foundMember.setRole(member.getRole());
    }
}
