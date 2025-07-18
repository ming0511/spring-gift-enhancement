package gift.delete.repository;

import gift.wish.entity.Page;
import gift.wish.entity.Wish;
import java.util.List;

public interface WishRepositoryInterface {

    Long addWish(Wish wish);

    List<Wish> getWishes(Long memberId, Page page);

    void deleteWish(Long wishId);

    Wish findByWishId(Long wishId);

    Long countWishesByMemberId(Long memberId);

    Boolean existsByMemberAndProduct(Long memberId, Long productId);
}
