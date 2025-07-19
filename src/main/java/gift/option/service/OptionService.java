package gift.option.service;

import gift.option.dto.OptionCreateCommand;
import gift.option.entity.Option;

public interface OptionService {

    Option addProductOption(Long productId, OptionCreateCommand dto);
}
