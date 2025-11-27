package com.athenhub.stockservice.stock.presentation;

import com.athenhub.commonmvc.security.AuthenticatedUser;
import com.athenhub.stockservice.stock.application.dto.RegisterResponse;
import com.athenhub.stockservice.stock.application.dto.StockInitializeCommand;
import com.athenhub.stockservice.stock.application.service.StockRegistrationFacade;
import com.athenhub.stockservice.stock.domain.dto.AccessContext;
import com.athenhub.stockservice.stock.presentation.dto.RegisterStockRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 재고 등록 요청을 처리하는 컨트롤러이다.
 *
 * <p>클라이언트로부터 재고 초기화 요청을 전달받아 {@link StockRegistrationFacade}에 위임하고, 그 결과를 응답으로 반환한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@RequestMapping("/api/v1/stocks")
@RestController
@RequiredArgsConstructor
public class StockRegisterController {

  private final StockRegistrationFacade stockRegistrationFacade;

  /**
   * 재고를 등록한다.
   *
   * <p>인증된 사용자 정보와 요청 바디를 기반으로 {@link AccessContext}와 {@link StockInitializeCommand}를 생성한 뒤, 재고 등록
   * 처리를 수행한다.
   *
   * @param authenticatedUser 인증된 사용자 정보
   * @param request 재고 등록 요청 DTO
   * @return 재고가 등록된 상품 ID 응답
   * @author 김지원
   * @since 1.0.0
   */
  @PostMapping
  public RegisterResponse register(
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
      @RequestBody @Valid RegisterStockRequest request) {

    AccessContext context =
        new AccessContext(authenticatedUser.id(), request.hubId(), request.vendorId());

    StockInitializeCommand command = toStockInitializeCommand(request);

    return stockRegistrationFacade.register(context, command);
  }

  /**
   * {@link RegisterStockRequest}를 {@link StockInitializeCommand}로 변환한다.
   *
   * <p>요청에 포함된 상품 ID와 옵션(Variant) 목록을 도메인에서 사용할 수 있는 커맨드 객체로 매핑한다.
   *
   * @param request 재고 등록 요청 DTO
   * @return 재고 초기화 커맨드
   * @author 김지원
   * @since 1.0.0
   */
  private static StockInitializeCommand toStockInitializeCommand(RegisterStockRequest request) {

    return new StockInitializeCommand(
        request.productId(),
        request.productVariants().stream()
            .map(it -> new StockInitializeCommand.ProductVariant(it.id(), it.quantity()))
            .toList());
  }
}
