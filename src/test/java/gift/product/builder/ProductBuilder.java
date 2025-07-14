package gift.product.builder;

import gift.product.entity.Product;

public class ProductBuilder {

    private String name = "기본 상품";
    private Double price = 4500.0;
    private String imageUrl = "http://default.img";
    private Boolean mdConfirmed = false;

    private ProductBuilder() {

    }

    public static ProductBuilder aProduct() {
        return new ProductBuilder();
    }

    public ProductBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder withPrice(Double price) {
        this.price = price;
        return this;
    }

    public ProductBuilder withImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public ProductBuilder withMdConfirmed(Boolean mdConfirmed) {
        this.mdConfirmed = mdConfirmed;
        return this;
    }

    public Product build() {
        return new Product(name, price, imageUrl, mdConfirmed);
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