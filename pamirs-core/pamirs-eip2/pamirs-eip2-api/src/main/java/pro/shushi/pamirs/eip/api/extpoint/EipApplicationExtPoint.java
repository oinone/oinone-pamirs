package pro.shushi.pamirs.eip.api.extpoint;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.eip.api.model.EipAuthentication;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.base.extpoint.DefaultReadWriteExtPoint;

import java.util.List;
import java.util.Optional;

@Ext(EipApplication.class)
public class EipApplicationExtPoint extends DefaultReadWriteExtPoint<EipApplication> {

    @Override
    @ExtPoint.Implement
    public Pagination<EipApplication> queryPageAfter(Pagination<EipApplication> page) {
        List<EipApplication> data = page.getContent();
        if (CollectionUtils.isNotEmpty(data)) {
            for (EipApplication item : data) {
                unsetPrivateKey(item);
            }
        }
        return page;
    }

    @Override
    @ExtPoint.Implement
    public List<EipApplication> queryListAfter(List<EipApplication> data) {
        if (CollectionUtils.isNotEmpty(data)) {
            for (EipApplication item : data) {
                unsetPrivateKey(item);
            }
        }
        return data;
    }

    @Override
    @ExtPoint.Implement
    public EipApplication queryOneAfter(EipApplication data) {
        unsetPrivateKey(data);
        return data;
    }

    private void unsetPrivateKey(EipApplication eipApplication) {
        Optional.ofNullable(eipApplication)
                .map(EipApplication::getAuthentication)
                .ifPresent(EipAuthentication::unsetPrivateKey);
    }
}
