package gift.option.service;

import gift.exception.option.DulicateOptionNameException;
import gift.exception.product.ProductNotFoundException;
import gift.option.dto.OptionCreateCommand;
import gift.option.entity.Option;
import gift.option.repository.OptionRepository;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public OptionServiceImpl(OptionRepository optionRepository,
        ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Option addProductOption(Long productId, OptionCreateCommand dto) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));

        Boolean optionExists = product.getOptions().stream()
            .anyMatch(option -> option.getName().equals(dto.name()));

        if (optionExists) {
            throw new DulicateOptionNameException("동일한 이름의 옵션이 이미 존재합니다: " + dto.name());
        }

        Option option = new Option(dto.name(), dto.quantity(), product);

        return optionRepository.save(option);
    }
}
