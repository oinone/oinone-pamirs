package pro.shushi.pamirs.business.core.service.entity;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.business.api.enumeration.BusinessPartnerTypeEnum;
import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.core.common.standard.service.impl.AbstractStandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link PamirsPartner}抽象服务
 *
 * @author Adamancy Zhang at 10:14 on 2021-08-31
 */
public abstract class AbstractPamirsPartnerService<T extends PamirsPartner> extends AbstractStandardModelService<T> {

    protected abstract BusinessPartnerTypeEnum getPartnerType();

    @Override
    protected T verificationAndSet(T data, boolean isUpdate) {
        if (data.getPartnerType() == null) {
            data.setPartnerType(getPartnerType());
        }
        return data;
    }

    @Override
    protected List<T> deleteBeforeVerification(List<T> list) {
        List<String> partnerCodeList = new ArrayList<>();
        for (T item : list) {
            String code = item.getCode();
            if (StringUtils.isBlank(code)) {
                throw new IllegalArgumentException("Invalid partner code.");
            }
            partnerCodeList.add(code);
        }
        Models.origin().deleteByWrapper(Pops.<PamirsPartner>lambdaQuery()
                .from(PamirsPartner.MODEL_MODEL)
                .in(PamirsPartner::getCode, partnerCodeList));
        return list;
    }
}
