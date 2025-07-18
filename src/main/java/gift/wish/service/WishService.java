package gift.wish.service;

import gift.wish.dto.WishCreateCommand;
import gift.wish.dto.WishCreateResponseDto;
import gift.wish.dto.WishGetRequestDto;
import gift.wish.dto.WishPageResponseDto;

public interface WishService {

    WishCreateResponseDto addWish(Long memberId, WishCreateCommand dto);

    WishPageResponseDto getWishes(Long memberId, WishGetRequestDto wishGetRequestDto);

    void deleteWish(Long memberId, Long wishId);

}
