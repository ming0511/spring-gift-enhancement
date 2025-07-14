package gift.product.entity;

public class Product {

    private Long productId;
    private final String name;
    private final Double price;
    private final String imageUrl;
    private final Boolean mdConfirmed;

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