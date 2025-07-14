package gift.wish.service;

import gift.exception.wish.InvalidPageException;
import gift.exception.wish.WishlistAccessDeniedException;
import gift.wish.dto.WishCreateRequestDto;
import gift.wish.dto.WishCreateResponseDto;
import gift.wish.dto.WishGetRequestDto;
import gift.wish.dto.WishGetResponseDto;
import gift.wish.dto.WishPageResponseDto;
import gift.wish.entity.Page;
import gift.wish.entity.Wish;
import gift.wish.repository.WishRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class WishServiceImpl implements WishService {

    private final WishRepository wishRepository;

    public WishServiceImpl(WishRepository wishRepository) {
        this.wishRepository = wishRepository;
    }

    @Override
    public WishCreateResponseDto addWish(Long memberId, WishCreateRequestDto wishCreateRequestDto) {
        // TODO: 이미 추가한 상품인지 확인하기(WishRepository.existsByMemberAndProduct) 실패 시 예외 처리(이미 존재하는 위시) -> 이후 수량 관련해서 추가.
        Boolean exists = wishRepository.existsByMemberAndProduct(memberId,
            wishCreateRequestDto.productId());
        if (exists) {
            throw new IllegalStateException("이미 위시리스트에 추가하셨습니다.");
        }

        Wish wish = new Wish(memberId, wishCreateRequestDto.productId());
        Long wishId = wishRepository.addWish(wish);

        Wish saveWish = wishRepository.findByWishId(wishId);

        return new WishCreateResponseDto(saveWish.getWishId(), saveWish.getMemberId(),
            saveWish.getProductId(),
            saveWish.getCreateDate());
    }

    // TODO: 상품 이름도 같이 반환할 수 있는 방법이 뭐가 있을까?
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

        List<Wish> wishes = wishRepository.getWishes(memberId, pageInfo);

        Long total = wishRepository.countWishesByMemberId(memberId);

        List<WishGetResponseDto> content = wishes.stream()
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
        Wish wish = wishRepository.findByWishId(wishId);

        if (!memberId.equals(wish.getMemberId())) {
            throw new WishlistAccessDeniedException("다른 사용자의 위시리스트에 접근할 수 없습니다.");
        }

        wishRepository.deleteWish(wishId);
    }

}
