package pro.shushi.pamirs.boot.web.spi.meta;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.utils.ViewActionUtils;
import pro.shushi.pamirs.boot.web.utils.ViewUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.definition.MetaDataPreStoreComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.Map;
import java.util.Optional;

/**
 * 视图动作计算
 * <p>
 * 2020/11/16 6:15 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
@SPI.Service(ViewAction.MODEL_MODEL)
public class ViewActionComputer implements MetaDataPreStoreComputer<ViewAction> {

    @Override
    public Result<Void> compute(Map<String/*module*/, Meta> metaMap, String model, String module, ViewAction data) {
        Result<Void> result = new Result<>();

        String dataModel = Optional.ofNullable(data.getModel()).orElse(data.getResModel());
        String resModel = Optional.ofNullable(data.getResModel()).orElse(data.getModel());

        // 补充模块名称
        if (StringUtils.isBlank(data.getModule())) {
            if (StringUtils.isNotBlank(dataModel)) {
                ModelDefinition resModelDefinition = PamirsSession.getContext().getModelConfig(dataModel).getModelDefinition();
                if (null != resModelDefinition) {
                    data.setModule(resModelDefinition.getModule());
                    data.setModuleName(resModelDefinition.getModuleName());
                }
            }
        }

        // 补充页面标题
        if (StringUtils.isBlank(data.getTitle())) {
            String resViewName = data.getResViewName();
            View view = ViewUtils.fetchCacheView(resModel, resViewName, data.getViewType().value());
            if (null != view && StringUtils.isBlank(view.getTitle())) {
                ModelDefinition resModelDefinition = null;
                String resModule = StringUtils.isBlank(data.getResModule()) ? data.getModule() : data.getResModule();
                if (StringUtils.isNotBlank(resModule)) {
                    Meta resModuleMeta = metaMap.get(resModule);
                    if (null == resModuleMeta) {
                        resModelDefinition = Optional.ofNullable(metaMap.get(module)).map(Meta::getData)
                                .map(v -> v.get(resModule)).map(v -> v.getModel(resModel)).orElse(null);
                    } else {
                        resModelDefinition = resModuleMeta.getModel(resModel);
                    }
                }
                data.setTitle(ViewActionUtils.viewActionTitle(data.getDataType(),
                        Optional.ofNullable(resModelDefinition).map(ModelDefinition::getDisplayName)
                                .orElse(CharacterConstants.SEPARATOR_EMPTY), data.getDisplayName()));
            }
        }
        return result;
    }

    @Override
    public boolean canCompute(String model) {
        return ViewAction.MODEL_MODEL.equals(model);
    }

}
