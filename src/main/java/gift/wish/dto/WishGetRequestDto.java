package gift.wish.dto;

public record WishGetRequestDto(
    Integer page,
    Integer size,
    String sort
) {

}
