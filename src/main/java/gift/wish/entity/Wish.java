package gift.wish.entity;

import gift.member.entity.Member;
import gift.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishes")
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @ManyToOne(optional = false)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column(name = "createDate", nullable = false)
    private LocalDateTime createDate;

    protected Wish() {

    }

    public Wish(Member member, Product product) {
        this.member = member;
        this.product = product;
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
