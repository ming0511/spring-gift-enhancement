package gift.wish.entity;

import gift.member.entity.Member;
import gift.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishes")
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }

    protected Wish() {

    }

    public Wish(Member member, Product product) {
        this.member = member;
        this.product = product;
    }

    public Wish(Member member, Product product, LocalDateTime createDate) {
        this.member = member;
        this.product = product;
        this.createDate = createDate;
    }

    public Wish(Long wishId, Product product, LocalDateTime createDate) {
        this.wishId = wishId;
        this.product = product;
        this.createDate = createDate;
    }

    public Wish(Long wishId, Member member, Product product) {
        this.wishId = wishId;
        this.member = member;
        this.product = product;
    }

    public Wish(Long wishId, Member member, Product product, LocalDateTime createDate) {
        this.wishId = wishId;
        this.member = member;
        this.product = product;
        this.createDate = createDate;
    }

    public Long getWishId() {
        return wishId;
    }

    public Member getMember() {
        return member;
    }

    public Product getProduct() {
        return product;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public Long getMemberId() {
        return member.getMemberId();
    }

    public Long getProductId() {
        return product.getProductId();
    }
}
