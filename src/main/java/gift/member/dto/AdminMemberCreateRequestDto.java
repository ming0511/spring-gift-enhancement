package gift.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminMemberCreateRequestDto(
    @NotBlank(message = "Email must not be blank.")
    String email,

    @NotBlank(message = "Email must not be blank.")
    String password,

    @NotNull(message = "Member Name must not be null.")
    String name,

    String role
) {

}
