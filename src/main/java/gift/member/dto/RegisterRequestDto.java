package gift.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDto(
    @NotBlank(message = "Email must not be blank.")
    String email,

    @NotBlank(message = "Password must not be blank.")
    String password,

    @NotNull(message = "Member Name must not be null.")
    String name,

    @NotNull(message = "Role must not be null.")
    String role
) {

}
