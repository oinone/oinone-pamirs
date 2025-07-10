package pro.shushi.pamirs.boot.web.init;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.constants.ViewConstants;
import pro.shushi.pamirs.boot.base.enmu.ViewBizTypeEnum;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.boot.web.utils.ViewXmlUtils;
import pro.shushi.pamirs.framework.configure.MetaConfiguration;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
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
 * 视图加载管理器
 * <p>
 * 2021/5/26 12:07 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
public class ViewTemplateInitLoader {

    @javax.annotation.Resource
    private MetaConfiguration metaConfiguration;

    public void init(String module, MetaData metaData) {
        PathMatchingResourcePatternResolver finder = new PathMatchingResourcePatternResolver(AppClassLoader.getClassLoader(ViewTemplateInitLoader.class));
        try {
            Resource[] resources = finder.getResources(ViewInitHelper.getDefaultTemplateLoadPath(module));
            int total = resources.length;
            String customViewsPackage = metaConfiguration.getViewsPackage();
            Resource[] customResources = null;
            if (StringUtils.isNotBlank(customViewsPackage) && !ViewInitHelper.isDefaultViewsPackage(customViewsPackage)) {
                customResources = finder.getResources(ViewInitHelper.getTemplateLoadPath(customViewsPackage, module));
                total += customResources.length;
            }
            List<String> viewFileList = new ArrayList<>(total);

            resolveTemplate(metaData, viewFileList, resources);
            if (customResources != null) {
                resolveTemplate(metaData, viewFileList, customResources);
            }

            log.info(MessageFormat.format("[View Template Definition Loader] scanning views files....total: {0}, views:{1}.", total, viewFileList));
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private void resolveTemplate(MetaData metaData, List<String> viewFileList, Resource[] resources) {
        for (Resource resource : resources) {
            try (InputStream is = resource.getInputStream()) {
                String template = IOUtils.toString(is);
                if (StringUtils.isBlank(template)) {
                    continue;
                }
                Object obj = ViewXmlUtils.fromXML(template);
                if (obj instanceof UIView) {
                    viewFileList.add(resource.getFilename());

                    UIView uiView = (UIView) obj;

                    if (StringUtils.isBlank(uiView.getModel())) {
                        throw PamirsException.construct(BootUxdExpEnumerate.BASE_VIEW_MODEL_IS_NULL_ERROR).errThrow();
                    }

                    // 处理视图
                    String viewName = Optional.ofNullable(uiView.getName())
                            .orElse(StringUtils.substringBefore(resource.getFilename(), CharacterConstants.SEPARATOR_DOT));
                    boolean newView = false;
                    View view = metaData.getDataItem(View.MODEL_MODEL, View.sign(uiView.getModel(), viewName));
                    int priority = Optional.ofNullable(uiView.getPriority()).orElse(ViewConstants.manualPriority);
                    if (null == view) {
                        newView = true;
                        view = new View();
                    } else if (!view.isMetaCompleted() && null != view.getPriority() && view.getPriority() < priority) {
                        continue;
                    }
                    view.setTemplate(template);
                    view.setModel(uiView.getModel());
                    view.setName(viewName);
                    view.setTitle(uiView.getTitle());
                    view.setType(uiView.getType());
                    view.setBizType(ViewBizTypeEnum.OPERATIONS_MANAGEMENT);
                    view.setSummary(uiView.getSummary());
                    view.setBaseLayoutName(uiView.getLayout());
                    view.setPriority(priority);
                    view.setShow(ActiveEnum.ACTIVE);
                    view.setActive(ActiveEnum.ACTIVE);
                    view.setSystemSource(SystemSourceEnum.MANUAL);

                    if (newView) {
                        metaData.addData(view);
                    } else {
                        view.disableMetaCompleted();
                    }

                    // 处理非引用的动作，为非引用的动作创建元数据
                    ViewInitHelper.autoCreateMetaData(metaData, uiView);

                    // 校正菜单窗口动作视图类型
                    ViewInitHelper.fixViewActionViewType(metaData, view);
                }
            } catch (Exception e) {
                log.error("[View Template Definition Loader] " + resource.getFilename() + " is not valid view template definition file!!!", e);
            }
        }
    }

}

