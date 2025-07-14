package gift.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductUpdateRequestDto(
    @NotNull(message = "Product Name must not be null.")
    @Size(max = 15, message = "Product Name must be less than or equal to 15 characters.")
    @Pattern(
        regexp = "^[\\p{L}\\p{N} ()\\[\\]+\\-&/_]*$",
        message = "Product name contains letters, numbers, and the following special characters are allowed: ( ) [ ] + - & / _"
    )
    String name,

    @NotNull(message = "Product Price must not be null.")
    @Min(value = 0, message = "Product Price must be greater than or equal to zero.")
    Double price,

    @NotNull(message = "Product Image URL must not be null.")
    String imageUrl,

    @NotNull(message = "Product Md Confirmed must not be null.")
    Boolean mdConfirmed) {

}