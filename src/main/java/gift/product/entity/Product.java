package gift.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean mdConfirmed;

    protected Product() {

    }

    public Product(String name, Double price, String imageUrl, Boolean mdConfirmed) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.mdConfirmed = mdConfirmed;
    }

    public Product(Long productId, String name, Double price, String imageUrl,
        Boolean mdConfirmed) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.mdConfirmed = mdConfirmed;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Boolean getMdConfirmed() {
        return mdConfirmed;
    }
}