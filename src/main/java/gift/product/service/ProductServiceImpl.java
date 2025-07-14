package gift.product.service;

import gift.exception.product.UnapprovedProductException;
import gift.product.dto.ProductCreateRequestDto;
import gift.product.dto.ProductCreateResponseDto;
import gift.product.dto.ProductGetResponseDto;
import gift.product.dto.ProductUpdateRequestDto;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductCreateResponseDto saveProduct(ProductCreateRequestDto productCreateRequestDto) {

        Boolean mdConfirmed =
            productCreateRequestDto.name().contains("카카오") ? productCreateRequestDto.mdConfirmed()
                : false;

        if (productCreateRequestDto.name().contains("카카오")
            && !mdConfirmed) {
            throw new UnapprovedProductException("협의되지 않은 '카카오'가 포함된 상품명은 사용할 수 없습니다.");
        }

        Product product = new Product(productCreateRequestDto.name(),
            productCreateRequestDto.price(), productCreateRequestDto.imageUrl(),
            mdConfirmed);

        Long productId = productRepository.saveProduct(product);

        Product savedProduct = productRepository.findProductById(productId);

        return new ProductCreateResponseDto(savedProduct.getProductId(), savedProduct.getName(),
            savedProduct.getPrice(), savedProduct.getImageUrl(), savedProduct.getMdConfirmed());
    }

    @Override
    public List<ProductGetResponseDto> findAllProducts() {
        List<Product> products = productRepository.findAllProducts();

        return products.stream()
            .map(product -> new ProductGetResponseDto(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getMdConfirmed()
            ))
            .collect(Collectors.toList());
    }

    @Override
    public ProductGetResponseDto findProductById(Long productId) {
        Product product = productRepository.findProductById(productId);

        return new ProductGetResponseDto(product.getProductId(), product.getName(),
            product.getPrice(), product.getImageUrl(), product.getMdConfirmed());
    }

    @Override
    public void updateProduct(Long productId, ProductUpdateRequestDto productUpdateRequestDto) {
        findProductById(productId);

        Boolean mdConfirmed =
            productUpdateRequestDto.name().contains("카카오") ? productUpdateRequestDto.mdConfirmed()
                : false;

        if (productUpdateRequestDto.name().contains("카카오")
            && !mdConfirmed) {
            throw new UnapprovedProductException("협의되지 않은 '카카오'가 포함된 상품명은 사용할 수 없습니다.");
        }

        Product product = new Product(productId, productUpdateRequestDto.name(),
            productUpdateRequestDto.price(), productUpdateRequestDto.imageUrl(),
            mdConfirmed);

        productRepository.updateProduct(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        findProductById(productId);

        productRepository.deleteProduct(productId);
    }
}
