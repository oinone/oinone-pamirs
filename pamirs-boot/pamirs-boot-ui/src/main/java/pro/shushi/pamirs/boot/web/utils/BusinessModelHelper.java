package pro.shushi.pamirs.boot.web.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.web.constants.BusinessModelConstants;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.List;

/**
 * 业务模型常量（同内置业务模型编码）
 *
 * @author Adamancy Zhang at 12:39 on 2025-10-20
 */
public class BusinessModelHelper {

    private static final List<String> BUSINESS_MODELS = Lists.newArrayList(
            BusinessModelConstants.RESOURCE_ADDRESS,
            BusinessModelConstants.COMPANY,
            BusinessModelConstants.DEPARTMENT,
            BusinessModelConstants.EMPLOYEE,
            BusinessModelConstants.ROLE
    );

    public static boolean isBusinessModel(String reference) {
        return BUSINESS_MODELS.contains(reference);
    }

    public static String isInheritedBusinessModel(String reference) {
        if (isBusinessModel(reference)) {
            return reference;
        }
        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(reference);
        if (modelConfig == null) {
            return null;
        }
        List<String> superModels = modelConfig.getSuperModels();
        if (CollectionUtils.isNotEmpty(superModels)) {
            for (String superModel : superModels) {
                if (BaseModel.MODEL_MODEL.equals(superModel)) {
                    continue;
                }
                String result = isInheritedBusinessModel(superModel);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}
