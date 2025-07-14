package gift.product.service;

import gift.product.dto.ProductCreateRequestDto;
import gift.product.dto.ProductCreateResponseDto;
import gift.product.dto.ProductGetResponseDto;
import gift.product.dto.ProductUpdateRequestDto;
import java.util.List;

public interface ProductService {

    ProductCreateResponseDto saveProduct(ProductCreateRequestDto productCreateRequestDto);

    List<ProductGetResponseDto> findAllProducts();

    ProductGetResponseDto findProductById(Long productId);

    void updateProduct(Long productId, ProductUpdateRequestDto productUpdateRequestDto);

    void deleteProduct(Long productId);
}
