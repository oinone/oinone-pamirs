package pro.shushi.pamirs.boot.web.init;

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
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.framework.common.utils.xstream.TreeNodeXStream;
import pro.shushi.pamirs.framework.common.utils.xstream.XMLNodeContent;
import pro.shushi.pamirs.framework.configure.MetaConfiguration;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.util.AppClassLoader;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @jakarta.annotation.Resource
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
        TreeNodeXStream xStream = new TreeNodeXStream();
        for (Resource resource : resources) {
            try (InputStream is = resource.getInputStream()) {
                String template = IOUtils.toString(is);
                if (StringUtils.isBlank(template)) {
                    continue;
                }
                // FIXME: zbh 20240301 兼容旧版mask
                template = FIX_PATTERN.matcher(template).replaceAll(TemplateNodeConstants.NODE_MASK);
                TreeNode<XMLNodeContent> root = xStream.fromXML(template);
                XMLNodeContent rootNodeContent = root.getValue();
                if (rootNodeContent != null) {
                    maskFileList.add(resource.getFilename());

                    // 处理布局
                    String maskName = Optional.ofNullable(rootNodeContent.getAttribute(LambdaUtil.fetchFieldName(UIMask::getName)))
                            .orElse(StringUtils.substringBefore(resource.getFilename(), CharacterConstants.SEPARATOR_DOT));
                    boolean newMask = false;
                    MaskDefinition maskDefinition = metaData.getDataItem(MaskDefinition.MODEL_MODEL, maskName);
                    if (null == maskDefinition) {
                        newMask = true;
                        maskDefinition = new MaskDefinition();
                    }
                    maskDefinition.setTemplate(template);
                    maskDefinition.setName(maskName);
                    maskDefinition.setType(Optional.ofNullable(rootNodeContent.getAttribute(LambdaUtil.fetchFieldName(UIMask::getType))).map(ViewTypeEnum::valueOf).orElse(null));
                    maskDefinition.setBizType(ViewBizTypeEnum.OPERATIONS_MANAGEMENT);
                    maskDefinition.setTitle(Optional.ofNullable(rootNodeContent.getAttribute(LambdaUtil.fetchFieldName(UIMask::getTitle))).orElse(maskName));
                    maskDefinition.setPriority(Optional.ofNullable(rootNodeContent.getAttribute(LambdaUtil.fetchFieldName(UIMask::getPriority))).map(Integer::valueOf).orElse(ViewConstants.manualPriority));
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
            }
        }
    }
}
