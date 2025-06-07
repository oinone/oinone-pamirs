package pro.shushi.pamirs.framework.orm.client.converter.processor;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.client.converter.RemoteClientDataConverter;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.util.FieldUtils;

import javax.annotation.Resource;

/**
 * 前端分页转换服务
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 */
@Component
public class RemoteClientPageProcessor {

    @Resource
    private RemoteClientDataConverter remoteClientDataConverter;

    @SuppressWarnings("unused")
    public void in(String model, Object obj) {

    }

    public void out(String model, Object obj) {
        Object value = FieldUtils.getFieldValue(obj, FieldConstants.CONTENT);
        if (Pagination.MODEL_MODEL.equals(model) && null != value) {
            String contentModel = Models.api().getDataModel(obj);
            value = remoteClientDataConverter.out(contentModel, value);
            FieldUtils.setFieldValue(obj, FieldConstants.CONTENT, value);
        }
    }

}
