package gift.product.dto;

public record ProductCreateCommand(
    String name,
    Double price,
    String imageUrl,
    Boolean mdConfirmed) {

}