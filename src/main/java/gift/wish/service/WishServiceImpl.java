package gift.wish.service;

import gift.exception.member.MemberNotFoundException;
import gift.exception.product.ProductNotFoundException;
import gift.exception.wish.InvalidPageException;
import gift.exception.wish.WishNotFoundException;
import gift.exception.wish.WishlistAccessDeniedException;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.wish.dto.WishCreateRequestDto;
import gift.wish.dto.WishCreateResponseDto;
import gift.wish.dto.WishGetRequestDto;
import gift.wish.dto.WishGetResponseDto;
import gift.wish.dto.WishPageResponseDto;
import gift.wish.entity.Page;
import gift.wish.entity.Wish;
import gift.wish.repository.WishRepository;
import gift.delete.repository.WishRepositoryInterface;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class WishServiceImpl implements WishService {

    private final WishRepositoryInterface wishRepository;

    private final WishRepository wishes;
    private final MemberRepository members;
    private final ProductRepository products;

    public WishServiceImpl(WishRepositoryInterface wishRepository, WishRepository wishes,
        MemberRepository members, ProductRepository products) {
        this.wishRepository = wishRepository;
        this.wishes = wishes;
        this.members = members;
        this.products = products;
    }

    @Override
    public WishCreateResponseDto addWish(Long memberId, WishCreateRequestDto wishCreateRequestDto) {
        // TODO: 이미 추가한 상품인지 확인하기(WishRepository.existsByMemberAndProduct) 실패 시 예외 처리(이미 존재하는 위시) -> 이후 수량 관련해서 추가.
        Long productId = wishCreateRequestDto.productId();

        Boolean exists = wishes.existsByMember_MemberIdAndProduct_ProductId(memberId, productId);
        if (exists) {
            throw new IllegalStateException("이미 위시리스트에 추가하셨습니다.");
        }

        Member member = members.findById(memberId).orElseThrow(
            () -> new MemberNotFoundException("회원이 존재하지 않습니다. memberId =" + memberId)
        );
        Product product = products.findById(productId).orElseThrow(
            () -> new ProductNotFoundException("상품이 존재하지 않습니다. productId =" + productId)
        );

        Wish wish = new Wish(member, product);
        Wish savedWish = wishes.save(wish);

        return new WishCreateResponseDto(savedWish.getWishId(), savedWish.getMemberId(),
            savedWish.getProductId(),
            savedWish.getCreateDate());
    }

    // TODO: JPA 정렬 방법?
    @Override
    public WishPageResponseDto getWishes(Long memberId, WishGetRequestDto wishGetRequestDto) {
        Integer page = wishGetRequestDto.page();
        Integer size = wishGetRequestDto.size();
        String sort = wishGetRequestDto.sort();

        if (size <= 0) {
            throw new InvalidPageException("허용되지 않은 페이지 크기입니다.");
        }

        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        String sortOrder = (sortParts.length > 1) ? sortParts[1] : "ASC";
        sortOrder = sortOrder.toUpperCase();

        if (!sortField.equals("createdDate")) {
            throw new InvalidPageException("허용되지 않은 정렬 필드입니다.");
        }
        if (!sortOrder.equals("ASC") && !sortOrder.equals("DESC")) {
            throw new InvalidPageException("허용되지 않은 정렬 방향입니다.");
        }

        Integer offset = page * size;

        Page pageInfo = new Page(size, offset, sortField, sortOrder);

        List<Wish> wishList = wishRepository.getWishes(memberId, pageInfo);

        Long total = wishes.countByMember_MemberId(memberId);

        List<WishGetResponseDto> content = wishList.stream()
            .map(wish -> new WishGetResponseDto(
                wish.getWishId(),
                wish.getProductId(),
                wish.getCreateDate()
            ))
            .collect(Collectors.toList());

        Integer totalPages = (int) Math.ceil((double) total / size);

        return new WishPageResponseDto(content, page, size, total, totalPages);
    }

    @Override
    public void deleteWish(Long memberId, Long wishId) {
        Wish wish = wishes.findById(wishId).orElseThrow(
            () -> new WishNotFoundException("위시 상품이 존재하지 않습니다. wishId = " + wishId)
        );

        if (!memberId.equals(wish.getMemberId())) {
            throw new WishlistAccessDeniedException("다른 사용자의 위시리스트에 접근할 수 없습니다.");
        }

        wishes.deleteById(wishId);
    }

}
