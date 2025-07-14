package gift.member.dto;

public record AdminMemberUpdateRequestDto(
    String email,
    String password,
    String name,
    String role
) {

}
