package pro.shushi.pamirs.business.core.service.entity.relation;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate;
import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.business.api.model.PamirsPartnerRelation;
import pro.shushi.pamirs.core.common.CopyHelper;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.core.common.standard.service.impl.AbstractStandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.enmu.AuditStatusEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adamancy Zhang at 12:13 on 2021-08-31
 */
public abstract class AbstractPamirsPartnerRelationService<T extends PamirsPartnerRelation> extends AbstractStandardModelService<T> {

    protected abstract String getRelationType();

    protected abstract PamirsPartner getOriginPartner(T data);

    protected abstract PamirsPartner getTargetPartner(T data);

    @Override
    protected T verificationAndSet(T data, boolean isUpdate) {
        if (!isUpdate) {
            if (null == data.getDataStatus()) {
                data.setDataStatus(DataStatusEnum.ENABLED);
            }
            if (null == data.getAuditStatus()) {
                data.setAuditStatus(AuditStatusEnum.SUCCESS);
            }
        }
        if (StringUtils.isBlank(data.getRelationType())) {
            data.setRelationType(getRelationType());
        }
        if (StringUtils.isBlank(data.getModel())) {
            data.setModel(Models.api().getModel(data));
        }
        PamirsPartner originPartner = getOriginPartner(data);
        if (originPartner == null) {
            throw PamirsException.construct(BusinessExpEnumerate.ORIGIN_PARTNER_IS_REQUIRED).errThrow();
        }
        PamirsPartner targetPartner = getTargetPartner(data);
        if (targetPartner == null) {
            throw PamirsException.construct(BusinessExpEnumerate.TARGET_PARTNER_IS_REQUIRED).errThrow();
        }
        data.setOriginPartner(originPartner)
                .setTargetPartner(targetPartner);
        return data;
    }

    @Override
    protected void afterProperties(T data, boolean isUpdate) {
        if (isUpdate) {
            CopyHelper.simpleReplace(data, new PamirsPartnerRelation()).updateByCode();
        } else {
            CopyHelper.simpleReplace(data, new PamirsPartnerRelation()).create();
        }
    }

    @Override
    protected void deleteAfterProperties(List<T> list) {
        List<String> partnerRelationCodeList = new ArrayList<>();
        for (T item : list) {
            String code = item.getCode();
            if (StringUtils.isBlank(code)) {
                throw new IllegalArgumentException("Invalid partner code.");
            }
            partnerRelationCodeList.add(code);
        }
        Models.origin().deleteByWrapper(Pops.<PamirsPartnerRelation>lambdaQuery()
                .from(PamirsPartnerRelation.MODEL_MODEL)
                .in(PamirsPartnerRelation::getCode, partnerRelationCodeList));
    }
}
