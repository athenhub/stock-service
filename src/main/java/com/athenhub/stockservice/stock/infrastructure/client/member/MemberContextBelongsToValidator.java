package com.athenhub.stockservice.stock.infrastructure.client.member;

import com.athenhub.stockservice.stock.domain.dto.AccessContext;
import com.athenhub.stockservice.stock.domain.service.BelongsToValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Member 서비스를 통해 사용자의 소속(허브/업체)을 검증하는 구현체이다.
 *
 * <p>{@link BelongsToValidator} 도메인 인터페이스의 인프라 구현으로, 외부 Member 서비스로부터 현재 사용자의 정보를 조회한 후 재고 요청 컨텍스트와
 * 비교하여 소속 관계를 판단한다.
 *
 * <p>검증 방식:
 *
 * <ul>
 *   <li>사용자가 허브 소속이면 → context.hubId 와 비교
 *   <li>사용자가 업체 소속이면 → context.vendorId 와 비교
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class MemberContextBelongsToValidator implements BelongsToValidator {

  /** 외부 Member 서비스 호출을 위한 Feign Client */
  private final MemberClient memberClient;

  /**
   * 사용자가 요청한 컨텍스트(허브/업체)에 소속되어 있는지 검증한다.
   *
   * @param context 사용자, 허브, 업체 정보를 포함한 접근 컨텍스트
   * @return 소속되어 있으면 true, 아니면 false
   */
  @Override
  public boolean belongsTo(AccessContext context) {
    MemberInfo myInfo = memberClient.getMyInfo();
    return belongsToOrganization(context, myInfo);
  }

  /** 사용자의 조직 정보와 접근 컨텍스트를 비교하여 소속 여부를 판단한다. */
  private boolean belongsToOrganization(AccessContext accessContext, MemberInfo myInfo) {
    return belongsToSameHub(accessContext, myInfo) || belongsToSameVendor(accessContext, myInfo);
  }

  /** 사용자가 업체(Vendor) 소속이며, 동일한 업체인지 확인한다. */
  private boolean belongsToSameVendor(AccessContext accessContext, MemberInfo myInfo) {
    return myInfo.organizationType().isVendor()
        && accessContext.vendorId().equals(myInfo.organizationId());
  }

  /** 사용자가 허브(Hub) 소속이며, 동일한 허브인지 확인한다. */
  private boolean belongsToSameHub(AccessContext accessContext, MemberInfo myInfo) {
    return myInfo.organizationType().isHub()
        && accessContext.hubId().equals(myInfo.organizationId());
  }
}
