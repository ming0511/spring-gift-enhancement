package gift.option.service;

import gift.exception.option.DulicateOptionNameException;
import gift.exception.option.OptionNotFoundException;
import gift.exception.product.ProductNotFoundException;
import gift.option.dto.OptionCreateCommand;
import gift.option.dto.OptionUpdateCommand;
import gift.option.entity.Option;
import gift.option.repository.OptionRepository;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public void updateProductOption(Long productId, OptionUpdateCommand dto) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));

        Option option = optionRepository.findById(dto.optionId())
            .orElseThrow(() -> new OptionNotFoundException("해당 옵션을 찾을 수 없습니다."));

        if (!option.getProductId().equals(product.getProductId())) {
            throw new OptionNotFoundException("해당 옵션은 지정된 상품에 속해 있지 않습니다.");
        }

        if (!option.getName().equals(dto.name())) {
            Boolean optionExists = product.getOptions().stream()
                .anyMatch(option1 -> option1.getName().equals(dto.name()));

            if (optionExists) {
                throw new DulicateOptionNameException("동일한 이름의 옵션이 이미 존재합니다: " + dto.name());
            }
        }

        option.changeName(dto.name());
        option.changeQuantity(dto.quantity());
    }

    @Override
    @Transactional
    public void deleteProductOption(Long productId, Long optionId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));

        Option option = optionRepository.findById(optionId)
            .orElseThrow(() -> new OptionNotFoundException("해당 옵션을 찾을 수 없습니다."));

        if (!option.getProductId().equals(product.getProductId())) {
            throw new OptionNotFoundException("해당 옵션은 지정된 상품에 속해 있지 않습니다.");
        }

        product.removeOption(option);
    }
}
