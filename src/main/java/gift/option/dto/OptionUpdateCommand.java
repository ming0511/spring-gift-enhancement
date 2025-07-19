package gift.option.dto;

public record OptionUpdateCommand(
    Long optionId,
    String name,
    Integer quantity
) {

}
