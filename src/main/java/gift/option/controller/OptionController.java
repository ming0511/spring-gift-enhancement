package gift.option.controller;

import gift.option.dto.OptionCreateCommand;
import gift.option.dto.OptionCreateRequestDto;
import gift.option.dto.OptionCreateResponseDto;
import gift.option.dto.OptionUpdateCommand;
import gift.option.dto.OptionUpdateRequestDto;
import gift.option.entity.Option;
import gift.option.service.OptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/options")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping("/{productId}/options")
    public ResponseEntity<OptionCreateResponseDto> addProductOption(
        @PathVariable Long productId,
        @Valid OptionCreateRequestDto requestDto) {

        OptionCreateCommand dto = new OptionCreateCommand(requestDto.name(), requestDto.quantity());

        Option option = optionService.addProductOption(productId, dto);

        OptionCreateResponseDto responseDto = new OptionCreateResponseDto(
            option.getOptionId(),
            option.getName(),
            option.getQuantity()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}/options/{optionId}")
    public ResponseEntity<Void> updateProductOption(
        @PathVariable Long productId,
        @PathVariable Long optionId,
        @Valid OptionUpdateRequestDto requestDto) {

        OptionUpdateCommand dto = new OptionUpdateCommand(optionId, requestDto.name(),
            requestDto.quantity());

        optionService.updateProductOption(productId, dto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}/options/{optionId}")
    public ResponseEntity<Void> deleteProductOption(
        @PathVariable Long productId,
        @PathVariable Long optionId) {

        optionService.deleteProductOption(productId, optionId);

        return ResponseEntity.noContent().build();
    }
}
