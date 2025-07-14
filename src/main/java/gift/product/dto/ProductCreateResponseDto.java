package gift.product.dto;

public record ProductCreateResponseDto(Long productId,
                                       String name,
                                       Double price,
                                       String imageUrl,
                                       Boolean mdConfirmed) {

}