package gift.wish.controller;

import gift.wish.annotation.LoginMember;
import gift.wish.dto.WishCreateRequestDto;
import gift.wish.dto.WishCreateResponseDto;
import gift.wish.dto.WishGetRequestDto;
import gift.wish.dto.WishPageResponseDto;
import gift.wish.service.WishService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        @Valid @RequestBody WishCreateRequestDto wishCreateRequestDto) {

        return new ResponseEntity<>(wishService.addWish(memberId, wishCreateRequestDto),
            HttpStatus.CREATED);
    }

    // /api/wishes?page=0&size=10&sort=createdDate,desc
    @GetMapping
    public ResponseEntity<WishPageResponseDto> getWishes(@LoginMember Long memberId,
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "10") Integer size,
        @RequestParam(defaultValue = "createdDate,desc") String sort) {

        WishGetRequestDto wishGetRequestDto = new WishGetRequestDto(page, size, sort);

        return new ResponseEntity<>(wishService.getWishes(memberId, wishGetRequestDto),
            HttpStatus.OK);
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> deleteWish(@LoginMember Long memberId, @PathVariable Long wishId) {
        wishService.deleteWish(memberId, wishId);
        return ResponseEntity.noContent().build();
    }
}

// TODO: 수량 변경 필요?