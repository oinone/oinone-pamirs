package pro.shushi.pamirs.boot.web.init;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.constants.ViewConstants;
import pro.shushi.pamirs.boot.base.enmu.ViewBizTypeEnum;
import pro.shushi.pamirs.boot.base.model.MaskDefinition;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.boot.base.ux.model.UIMask;
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
import pro.shushi.pamirs.meta.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 母版初始化加载器
 *
 * @author Adamancy Zhang at 14:34 on 2024-02-29
 */
@Slf4j
@Component
public class ViewMaskInitLoader {

    private static final Pattern FIX_PATTERN = Pattern.compile("host");

    @javax.annotation.Resource
    private MetaConfiguration metaConfiguration;

    public void init(String module, MetaData metaData) {
        PathMatchingResourcePatternResolver finder = new PathMatchingResourcePatternResolver(AppClassLoader.getClassLoader(ViewMaskInitLoader.class));
        try {
            Resource[] resources = finder.getResources(ViewInitHelper.getDefaultMaskLoadPath(module));
            int total = resources.length;
            String customViewsPackage = metaConfiguration.getViewsPackage();
            Resource[] customResources = null;
            if (StringUtils.isNotBlank(customViewsPackage) && !ViewInitHelper.isDefaultViewsPackage(customViewsPackage)) {
                customResources = finder.getResources(ViewInitHelper.getMaskLoadPath(customViewsPackage, module));
                total += customResources.length;
            }
            List<String> maskFileList = new ArrayList<>(total);

            resolveMask(metaData, maskFileList, resources);
            if (customResources != null) {
                resolveMask(metaData, maskFileList, customResources);
            }

            log.info(MessageFormat.format("[View Mask Definition Loader] scanning views files....total: {0}, masks:{1}.", total, maskFileList));
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private void resolveMask(MetaData metaData, List<String> maskFileList, Resource[] resources) {
        for (Resource resource : resources) {
            try (InputStream is = resource.getInputStream()) {
                String template = IOUtils.toString(is);
                if (StringUtils.isBlank(template)) {
                    continue;
                }
                // FIXME: zbh 20240301 兼容旧版mask
                template = FIX_PATTERN.matcher(template).replaceAll(TemplateNodeConstants.NODE_MASK);
                Object obj = ViewXmlUtils.fromXML(template);
                if (obj instanceof UIMask) {
                    maskFileList.add(resource.getFilename());

                    UIMask uiMask = (UIMask) obj;

                    // 处理布局
                    String maskName = Optional.ofNullable(uiMask.getName())
                            .orElse(StringUtils.substringBefore(resource.getFilename(), CharacterConstants.SEPARATOR_DOT));
                    boolean newMask = false;
                    MaskDefinition maskDefinition = metaData.getDataItem(MaskDefinition.MODEL_MODEL, maskName);
                    if (null == maskDefinition) {
                        newMask = true;
                        maskDefinition = new MaskDefinition();
                    }
                    maskDefinition.setTemplate(template);
                    maskDefinition.setName(maskName);
                    maskDefinition.setType(uiMask.getType());
                    maskDefinition.setTitle(Optional.ofNullable(uiMask.getTitle()).orElse(maskName));
                    maskDefinition.setBizType(ViewBizTypeEnum.OPERATIONS_MANAGEMENT);
                    maskDefinition.setPriority(Optional.ofNullable(uiMask.getPriority()).orElse(ViewConstants.manualPriority));
                    maskDefinition.setShow(ActiveEnum.ACTIVE);
                    maskDefinition.setActive(ActiveEnum.ACTIVE);
                    maskDefinition.setSystemSource(SystemSourceEnum.MANUAL);

                    if (newMask) {
                        metaData.addData(maskDefinition);
                    } else {
                        maskDefinition.disableMetaCompleted();
                    }
                }
            } catch (Exception e) {
                log.error("[View Mask Definition Loader] " + resource.getFilename() + " is not valid view mask definition file!!!", e);
                throw PamirsException.construct(BootUxdExpEnumerate.BASE_VIEW_REGISTER_ERROR, e).errThrow();
            }
        }
    }

    public static void main(String[] args) {
        String template = "<mask>\n" +
                "    <header>\n" +
                "        <widget widget=\"designer-app-switcher\"/>\n" +
                "        <block>\n" +
                "            <widget widget=\"notification\"/>\n" +
                "            <widget widget=\"divider\"/>\n" +
                "            <widget widget=\"language\"/>\n" +
                "            <widget widget=\"divider\"/>\n" +
                "            <widget widget=\"user\"/>\n" +
                "        </block>\n" +
                "    </header>\n" +
                "    <container>\n" +
                "        <sidebar>\n" +
                "            <widget widget=\"designer-nav-menu\" height=\"100%\"/>\n" +
                "        </sidebar>\n" +
                "        <content>\n" +
                "            <block height=\"100%\" width=\"100%\">\n" +
                "                <widget width=\"100%\" widget=\"main-view\"/>\n" +
                "            </block>\n" +
                "        </content>\n" +
                "    </container>\n" +
                "</mask>";
        Object obj = xstream.fromXML(template, new UIMask());
        System.out.println(1);
    }

    private static XStream xstream;

    static {
        Set<Class<?>> annotationClassSet = ClassUtils.getClasses(UIView.class.getPackage().getName());
        if (!CollectionUtils.isEmpty(annotationClassSet)) {
            Class<?>[] annotationClasses = annotationClassSet.toArray(new Class[0]);
            xstream = new XStream();
            XStream.setupDefaultSecurity(xstream);
            xstream.allowTypes(annotationClasses);
            xstream.processAnnotations(annotationClasses);
            xstream.aliasSystemAttribute(null, "class");
        }
    }
}
