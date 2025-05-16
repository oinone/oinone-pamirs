package pro.shushi.pamirs.eip.api.extpoint;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.eip.api.model.EipAuthentication;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.base.extpoint.DefaultReadWriteExtPoint;

import java.util.List;

@Ext(EipAuthentication.class)
public class EipAuthenticationExtPoint extends DefaultReadWriteExtPoint<EipAuthentication> {

    @Override
    @ExtPoint.Implement
    public Pagination<EipAuthentication> queryPageAfter(Pagination<EipAuthentication> page) {
        List<EipAuthentication> data = page.getContent();
        if (CollectionUtils.isNotEmpty(data)) {
            for (EipAuthentication item : data)
                if (item != null)
                    unsetKey(item);
        }
        return page;
    }

    @Override
    @ExtPoint.Implement
    public List<EipAuthentication> queryListAfter(List<EipAuthentication> data) {
        if (CollectionUtils.isNotEmpty(data)) {
            for (EipAuthentication item : data)
                unsetKey(item);
        }
        return data;
    }

    @Override
    @ExtPoint.Implement
    public EipAuthentication queryOneAfter(EipAuthentication data) {
        unsetKey(data);
        return data;
    }

    private void unsetKey(EipAuthentication data) {
        data.unsetPrivateKey();
    }
}
