package gift.wish.entity;

import java.time.LocalDateTime;

public class Wish {

    private Long wishId;
    private Long memberId;
    private final Long productId;
    private LocalDateTime createDate;

    public Wish(Long memberId, Long productId) {
        this.memberId = memberId;
        this.productId = productId;
    }

    public Wish(Long wishId, Long productId, LocalDateTime createDate) {
        this.wishId = wishId;
        this.productId = productId;
        this.createDate = createDate;
    }

    public Wish(Long wishId, Long memberId, Long productId) {
        this.wishId = wishId;
        this.memberId = memberId;
        this.productId = productId;
    }

    public Wish(Long wishId, Long memberId, Long productId, LocalDateTime createDate) {
        this.wishId = wishId;
        this.memberId = memberId;
        this.productId = productId;
        this.createDate = createDate;
    }

    public Long getWishId() {
        return wishId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getProductId() {
        return productId;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }
}
