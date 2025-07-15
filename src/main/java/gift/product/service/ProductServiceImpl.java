package gift.product.service;

import gift.exception.product.ProductNotFoundException;
import gift.exception.product.UnapprovedProductException;
import gift.product.dto.ProductCreateRequestDto;
import gift.product.dto.ProductCreateResponseDto;
import gift.product.dto.ProductGetResponseDto;
import gift.product.dto.ProductUpdateRequestDto;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.delete.repository.ProductRepositoryInterface;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepositoryInterface productRepository;

    private final ProductRepository products;

    public ProductServiceImpl(ProductRepositoryInterface productRepository,
        ProductRepository products) {
        this.productRepository = productRepository;
        this.products = products;
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

        Product savedProduct = products.save(product);

        return new ProductCreateResponseDto(savedProduct.getProductId(), savedProduct.getName(),
            savedProduct.getPrice(), savedProduct.getImageUrl(), savedProduct.getMdConfirmed());
    }

    @Override
    public List<ProductGetResponseDto> findAllProducts() {
        List<Product> productList = products.findAll();

        return productList.stream()
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
        Optional<Product> product = products.findById(productId);

        if (!product.isPresent()) {
            throw new ProductNotFoundException("상품이 존재하지 않습니다. productId =" + productId);
        }

        return new ProductGetResponseDto(product.get().getProductId(), product.get().getName(),
            product.get().getPrice(), product.get().getImageUrl(), product.get().getMdConfirmed());
    }

    @Override
    public void updateProduct(Long productId, ProductUpdateRequestDto productUpdateRequestDto) {
        Optional<Product> foundProduct = products.findById(productId);

        if (!foundProduct.isPresent()) {
            throw new ProductNotFoundException("상품이 존재하지 않습니다. productId =" + productId);
        }

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

        // TODO: JPA로 수정.
        productRepository.updateProduct(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        Optional<Product> foundProduct = products.findById(productId);

        if (!foundProduct.isPresent()) {
            throw new ProductNotFoundException("상품이 존재하지 않습니다. productId =" + productId);
        }

        products.deleteById(productId);
    }
}
