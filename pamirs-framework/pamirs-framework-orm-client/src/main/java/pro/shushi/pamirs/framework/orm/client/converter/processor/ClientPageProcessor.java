package pro.shushi.pamirs.framework.orm.client.converter.processor;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.client.converter.DefaultClientDataConverter;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.util.FieldUtils;

import jakarta.annotation.Resource;

/**
 * 前端分页转换服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class ClientPageProcessor {

    @Resource
    private DefaultClientDataConverter frontEndDataConverter;

    @SuppressWarnings("unused")
    public void in(String model, Object obj) {

    }

    public void out(String model, Object obj) {
        Object value = FieldUtils.getFieldValue(obj, FieldConstants.CONTENT);
        if (Pagination.MODEL_MODEL.equals(model) && null != value) {
            String contentModel = Models.api().getDataModel(obj);
            value = frontEndDataConverter.out(contentModel, value);
            FieldUtils.setFieldValue(obj, FieldConstants.CONTENT, value);
        }
    }

}
