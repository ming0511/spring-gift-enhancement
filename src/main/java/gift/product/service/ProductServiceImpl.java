package gift.product.service;

import gift.exception.product.ProductNotFoundException;
import gift.product.dto.ProductCreateRequestDto;
import gift.product.dto.ProductCreateResponseDto;
import gift.product.dto.ProductGetResponseDto;
import gift.product.dto.ProductUpdateRequestDto;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository products;

    public ProductServiceImpl(ProductRepository products) {
        this.products = products;
    }

    @Override
    public ProductCreateResponseDto saveProduct(ProductCreateRequestDto productCreateRequestDto) {

        Boolean mdConfirmed =
            productCreateRequestDto.name().contains("카카오") ? productCreateRequestDto.mdConfirmed()
                : false;

        Product product = new Product(productCreateRequestDto.name(),
            productCreateRequestDto.price(), productCreateRequestDto.imageUrl(),
            mdConfirmed);

        product.validate();

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
        Product product = products.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));

        return new ProductGetResponseDto(product.getProductId(), product.getName(),
            product.getPrice(), product.getImageUrl(), product.getMdConfirmed());
    }

    @Override
    public void updateProduct(Long productId, ProductUpdateRequestDto productUpdateRequestDto) {
        Boolean mdConfirmed =
            productUpdateRequestDto.name().contains("카카오") ? productUpdateRequestDto.mdConfirmed()
                : false;

        Product product = new Product(productId, productUpdateRequestDto.name(),
            productUpdateRequestDto.price(), productUpdateRequestDto.imageUrl(),
            mdConfirmed);

        product.validate();

        update(productId, product);
    }

    @Override
    public void deleteProduct(Long productId) {
        products.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));

        products.deleteById(productId);
    }

    @Transactional
    public void update(Long id, Product product) {
        Product foundProduct = products.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));

        foundProduct.rename(product.getName());
        foundProduct.updatePrice(product.getPrice());
        foundProduct.updateImageUrl(product.getImageUrl());
        foundProduct.updateMdConfirmed(product.getMdConfirmed());
    }
}
