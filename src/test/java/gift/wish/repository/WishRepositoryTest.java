package gift.wish.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class WishRepositoryTest {

    @Autowired
    private WishRepository wishes;

    @Test
    void saveWish() {
//        Wish expected = new Wish("");
//        Wish actual = wishes.save(expected);
//        assertAll(
//            () -> assertThat(actual.getMemberId()).isNotNull(),
//            () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()),
//            () -> assertThat(actual.getPassword()).isEqualTo(expected.getPassword())
//        );
    }

    @Test
    void findAllWishes() {

    }

    @Test
    void findWishById() {

    }

    @Test
    void deleteWishById() {

    }

    @Test
    void existsByMemberIdAndProductId() {

    }

    @Test
    void countByMemberId() {

    }
}
