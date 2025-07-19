package gift.option.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import gift.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "option")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "fk_option_product_id_ref_product_id"))
    @JsonBackReference
    private Product product;

    protected Option() {
    }

    public Option(String name, Integer quantity) {
        this(null, name, quantity, null);
    }

    public Option(String name, Integer quantity, Product product) {
        this(null, name, quantity, product);
    }

    public Option(Long optionId, String name, Integer quantity, Product product) {
        this.optionId = optionId;
        this.name = name;
        this.quantity = quantity;
        this.product = product;
    }

    public Long getOptionId() {
        return optionId;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    public Long getProductId() {
        return product.getProductId();
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void changeProduct(Product product) {
        this.product = product;
    }
}
