package gift.member.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
    @NotBlank(message = "Email must not be blank.")
    String email,

    @NotBlank(message = "Password must not be blank.")
    String password
) {

}
