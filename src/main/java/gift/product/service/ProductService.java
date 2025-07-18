package gift.product.service;

import gift.product.dto.ProductCreateCommand;
import gift.product.dto.ProductCreateResponseDto;
import gift.product.dto.ProductGetResponseDto;
import gift.product.dto.ProductUpdateRequestDto;
import java.util.List;

public interface ProductService {

    ProductCreateResponseDto saveProduct(ProductCreateCommand dto);

    List<ProductGetResponseDto> findAllProducts();

    ProductGetResponseDto findProductById(Long productId);

    void updateProduct(Long productId, ProductUpdateRequestDto productUpdateRequestDto);

    void deleteProduct(Long productId);
}
