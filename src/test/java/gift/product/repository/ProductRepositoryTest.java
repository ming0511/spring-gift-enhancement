package gift.product.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.product.builder.ProductBuilder;
import gift.product.entity.Product;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository products;

    @Test
    void saveProduct() {
        Product expected = ProductBuilder.aProduct().build();
        Product actual = products.save(expected);

        assertAll(
            () -> assertThat(actual.getProductId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
            () -> assertThat(actual.getImageUrl()).isEqualTo(expected.getImageUrl()),
            () -> assertThat(actual.getMdConfirmed()).isEqualTo(expected.getMdConfirmed())
        );
    }

    @Test
    void findAllProducts() {
        products.save(ProductBuilder.aProduct().withName("one").build());
        products.save(ProductBuilder.aProduct().withName("two").build());
        products.save(ProductBuilder.aProduct().withName("three").build());

        List<Product> productList = products.findAll();

        assertThat(productList).hasSize(3);
    }

    @Test
    void findProductById() {
        Product expected = ProductBuilder.aProduct().build();
        Product savedProduct = products.save(expected);

        Product actual = products.findById(savedProduct.getProductId()).get();
        assertAll(
            () -> assertThat(actual.getProductId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
            () -> assertThat(actual.getImageUrl()).isEqualTo(expected.getImageUrl()),
            () -> assertThat(actual.getMdConfirmed()).isEqualTo(expected.getMdConfirmed())
        );
    }

    @Test
    void updateProduct() {

    }

    @Test
    void deleteProduct() {
        Product expected = ProductBuilder.aProduct().build();
        Product savedProduct = products.save(expected);

        products.delete(savedProduct);

        assertThat(products.findById(savedProduct.getProductId())).isEmpty();
    }
}
