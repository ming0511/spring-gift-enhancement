package gift.product.service;

import gift.product.dto.ProductCreateCommand;
import gift.product.dto.ProductCreateResponseDto;
import gift.product.dto.ProductGetResponseDto;
import gift.product.dto.ProductUpdateCommand;
import java.util.List;

public interface ProductService {

    ProductCreateResponseDto saveProduct(ProductCreateCommand dto);

    List<ProductGetResponseDto> findAllProducts();

    ProductGetResponseDto findProductById(Long productId);

    void updateProduct(Long productId, ProductUpdateCommand dto);

    void deleteProduct(Long productId);
}
