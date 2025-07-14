package gift.member.dto;

public record AdminMemberGetResponseDto(
    Long memberId,
    String email,
    String password,
    String name,
    String role
) {

}
