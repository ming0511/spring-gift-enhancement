package gift.wish.controller;

import gift.wish.annotation.LoginMember;
import gift.wish.dto.WishCreateCommand;
import gift.wish.dto.WishCreateRequestDto;
import gift.wish.dto.WishCreateResponseDto;
import gift.wish.dto.WishPageResponseDto;
import gift.wish.service.WishService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping
    public ResponseEntity<WishCreateResponseDto> addWish(@LoginMember Long memberId,
        @Valid @RequestBody WishCreateRequestDto requestDto) {

        WishCreateCommand dto = new WishCreateCommand(requestDto.productId());

        return new ResponseEntity<>(wishService.addWish(memberId, dto), HttpStatus.CREATED);
    }

    // /api/wishes?page=0&size=10&sort=createdAt,desc
    @GetMapping
    public ResponseEntity<WishPageResponseDto> getWishes(@LoginMember Long memberId,
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return new ResponseEntity<>(wishService.getWishes(memberId, pageable), HttpStatus.OK);
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> deleteWish(@LoginMember Long memberId, @PathVariable Long wishId) {
        wishService.deleteWish(memberId, wishId);
        return ResponseEntity.noContent().build();
    }
}

// TODO: 수량 변경 필요?