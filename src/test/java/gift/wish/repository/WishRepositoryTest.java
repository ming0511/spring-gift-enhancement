package gift.wish.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.member.builder.MemberBuilder;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.product.builder.ProductBuilder;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.wish.entity.Wish;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class WishRepositoryTest {

    @Autowired
    private WishRepository wishes;

    @Autowired
    private MemberRepository members;

    @Autowired
    private ProductRepository products;

    @Test
    void saveWish() {
        Member member = members.save(MemberBuilder.aMember().build());
        Product product = products.save(ProductBuilder.aProduct().build());

        Wish expected = new Wish(member, product);
        Wish actual = wishes.save(expected);
        assertAll(
            () -> assertThat(actual.getWishId()).isNotNull(),
            () -> assertThat(actual.getMemberId()).isEqualTo(member.getMemberId()),
            () -> assertThat(actual.getProductId()).isEqualTo(product.getProductId())
        );
    }

    @Test
    void findAllWishes() {
        Member member = members.save(MemberBuilder.aMember().build());
        Product product = products.save(ProductBuilder.aProduct().withName("3").build());
        Wish expected = new Wish(member, product);
        wishes.save(expected);

        Member member1 = members.save(MemberBuilder.aMember().withEmail("two").build());
        Product product1 = products.save(ProductBuilder.aProduct().withName("2").build());
        Wish expected1 = new Wish(member1, product1);
        wishes.save(expected1);

        Member member2 = members.save(MemberBuilder.aMember().withEmail("three").build());
        Product product2 = products.save(ProductBuilder.aProduct().withName("3").build());
        Wish expected2 = new Wish(member2, product2);
        wishes.save(expected2);

        List<Wish> wishList = wishes.findAll();

        assertThat(wishList).hasSize(3);
    }

    @Test
    void findWishById() {
        Member member = members.save(MemberBuilder.aMember().build());
        Product product = products.save(ProductBuilder.aProduct().build());
        Wish expected = new Wish(member, product);
        wishes.save(expected);

        Wish actual = wishes.findById(expected.getWishId()).get();
        assertAll(
            () -> assertThat(actual.getWishId()).isNotNull(),
            () -> assertThat(actual.getMemberId()).isEqualTo(member.getMemberId()),
            () -> assertThat(actual.getProductId()).isEqualTo(product.getProductId())
        );
    }

    @Test
    void deleteWishById() {
        Member member = members.save(MemberBuilder.aMember().build());
        Product product = products.save(ProductBuilder.aProduct().build());
        Wish expected = new Wish(member, product);
        wishes.save(expected);

        wishes.deleteById(expected.getWishId());

        assertThat(wishes.findById(expected.getWishId())).isEmpty();
    }

    @Test
    void existsByMemberIdAndProductId() {
        Member member = members.save(MemberBuilder.aMember().build());
        Product product = products.save(ProductBuilder.aProduct().build());
        Wish expected = new Wish(member, product);
        wishes.save(expected);

        Boolean actual = wishes.existsByMember_MemberIdAndProduct_ProductId(member.getMemberId(),
            product.getProductId());

        assertThat(actual).isTrue();
    }

    @Test
    void countByMemberId() {
        Member member = members.save(MemberBuilder.aMember().build());
        Product product = products.save(ProductBuilder.aProduct().withName("3").build());
        Wish expected = new Wish(member, product);
        wishes.save(expected);

        Product product1 = products.save(ProductBuilder.aProduct().withName("2").build());
        Wish expected1 = new Wish(member, product1);
        wishes.save(expected1);

        Member member2 = members.save(MemberBuilder.aMember().withEmail("three").build());
        Product product2 = products.save(ProductBuilder.aProduct().withName("3").build());
        Wish expected2 = new Wish(member2, product2);
        wishes.save(expected2);

        Long actual = wishes.countByMember_MemberId(member.getMemberId());

        assertThat(actual).isEqualTo(2L);
    }
}
