package gift.product.repository;

import gift.product.entity.Product;
import java.util.List;

public interface ProductRepository {

    Long saveProduct(Product product);

    List<Product> findAllProducts();

    Product findProductById(Long productId);

    void updateProduct(Product product);

    void deleteProduct(Long productId);
}
