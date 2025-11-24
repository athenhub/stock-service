package com.athenhub.stockservice.product.infrastructure;

import com.athenhub.stockservice.product.domain.dto.AccessContext;
import com.athenhub.stockservice.product.domain.service.BelongsToValidator;
import com.athenhub.stockservice.product.infrastructure.client.MemberClient;
import com.athenhub.stockservice.product.infrastructure.dto.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberContextBelongsToValidator implements BelongsToValidator {

    private final MemberClient memberClient;

    @Override
    public boolean belongsTo(AccessContext context) {
        MemberInfo myInfo = memberClient.getMyInfo();
        return belongsToOrganization(context, myInfo);
    }

    private boolean belongsToOrganization(AccessContext accessContext, MemberInfo myInfo) {
        return belongsToSameHub(accessContext, myInfo) || belongsToSameVendor(accessContext, myInfo);
    }

    private boolean belongsToSameVendor(AccessContext accessContext, MemberInfo myInfo) {
        return myInfo.organizationType().isVendor()
                && accessContext.vendorId().equals(myInfo.organizationId());
    }

    private boolean belongsToSameHub(AccessContext accessContext, MemberInfo myInfo) {
        return myInfo.organizationType().isHub()
                && accessContext.hubId().equals(myInfo.organizationId());
    }
}
