package gift.wish.dto;

import jakarta.validation.constraints.NotNull;

public record WishCreateRequestDto(
    @NotNull(message = "Member ID must not be null.")
    Long productId
) {

}
