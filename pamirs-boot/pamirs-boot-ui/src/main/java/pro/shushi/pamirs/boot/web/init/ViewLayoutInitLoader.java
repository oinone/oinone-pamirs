package pro.shushi.pamirs.boot.web.init;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.constants.ViewConstants;
import pro.shushi.pamirs.boot.base.enmu.TemplateLayoutTypeEnum;
import pro.shushi.pamirs.boot.base.enmu.ViewBizTypeEnum;
import pro.shushi.pamirs.boot.base.model.LayoutDefinition;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.web.utils.ViewXmlUtils;
import pro.shushi.pamirs.framework.configure.MetaConfiguration;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.AppClassLoader;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 布局加载管理器
 * <p>
 * 2021/5/26 12:07 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
public class ViewLayoutInitLoader {

    @jakarta.annotation.Resource
    private MetaConfiguration metaConfiguration;

    public void init(String module, MetaData metaData) {
        PathMatchingResourcePatternResolver finder = new PathMatchingResourcePatternResolver(AppClassLoader.getClassLoader(ViewLayoutInitLoader.class));
        try {
            Resource[] resources = finder.getResources(ViewInitHelper.getDefaultLayoutLoadPath(module));
            int total = resources.length;
            String customViewsPackage = metaConfiguration.getViewsPackage();
            Resource[] customResources = null;
            if (StringUtils.isNotBlank(customViewsPackage) && !ViewInitHelper.isDefaultViewsPackage(customViewsPackage)) {
                customResources = finder.getResources(ViewInitHelper.getLayoutLoadPath(customViewsPackage, module));
                total += customResources.length;
            }
            List<String> layoutFileList = new ArrayList<>(total);

            resolveLayout(metaData, layoutFileList, resources);
            if (customResources != null) {
                resolveLayout(metaData, layoutFileList, customResources);
            }

            log.info(MessageFormat.format("[View Layout Definition Loader] scanning views files....total: {0}, layouts:{1}.", total, layoutFileList));
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private void resolveLayout(MetaData metaData, List<String> layoutFileList, Resource[] resources) {
        for (Resource resource : resources) {
            try (InputStream is = resource.getInputStream()) {
                String template = IOUtils.toString(is);
                if (StringUtils.isBlank(template)) {
                    continue;
                }
                Object obj = ViewXmlUtils.fromXML(template);
                if (obj instanceof UIView) {
                    layoutFileList.add(resource.getFilename());

                    UIView uiLayout = (UIView) obj;

                    // 处理布局
                    String layoutName = Optional.ofNullable(uiLayout.getName())
                            .orElse(StringUtils.substringBefore(resource.getFilename(), CharacterConstants.SEPARATOR_DOT));
                    boolean newLayout = false;
                    LayoutDefinition layoutDefinition = metaData.getDataItem(LayoutDefinition.MODEL_MODEL, layoutName);
                    if (null == layoutDefinition) {
                        newLayout = true;
                        layoutDefinition = new LayoutDefinition();
                    }
                    layoutDefinition.setTemplate(template);
                    layoutDefinition.setName(layoutName);
                    layoutDefinition.setType(uiLayout.getType());
                    layoutDefinition.setLayoutType(TemplateLayoutTypeEnum.VIEW);
                    layoutDefinition.setBizType(ViewBizTypeEnum.OPERATIONS_MANAGEMENT);
                    layoutDefinition.setPriority(Optional.ofNullable(uiLayout.getPriority()).orElse(ViewConstants.manualPriority));
                    layoutDefinition.setShow(ActiveEnum.ACTIVE);
                    layoutDefinition.setActive(ActiveEnum.ACTIVE);
                    layoutDefinition.setSystemSource(SystemSourceEnum.MANUAL);

                    if (newLayout) {
                        metaData.addData(layoutDefinition);
                    } else {
                        layoutDefinition.disableMetaCompleted();
                    }

                    // 处理非引用的动作，为非引用的动作创建元数据
                    ViewInitHelper.autoCreateMetaData(metaData, uiLayout);
                }
            } catch (Exception e) {
                log.error("[View Layout Definition Loader] " + resource.getFilename() + " is not valid view layout definition file!!!", e);
            }
        }
    }
}
