package pro.shushi.pamirs.resource.api.spi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.cdn.client.FileClient;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFile;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;
import pro.shushi.pamirs.resource.api.enmu.IconLibTypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceIcon;
import pro.shushi.pamirs.resource.api.model.ResourceIconGroup;
import pro.shushi.pamirs.resource.api.model.ResourceIconLib;
import pro.shushi.pamirs.resource.api.pojo.Glyphs;
import pro.shushi.pamirs.resource.api.pojo.IconContext;
import pro.shushi.pamirs.resource.api.pojo.JsonRootBean;
import pro.shushi.pamirs.resource.api.tmodel.ResourceIconUpload;
import pro.shushi.pamirs.resource.api.util.IconUnZipUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class IconCommonLogicService {

    public static final String ICONFONT = "ICONFONT";

    /**
     * 处理JSON文件，生成二级目录，得到上传链接
     *
     * @param result
     * @param iconContext 上下文
     */
    public void processJsonAndRetrieveUrls(IconUnZipUtils.Result result, IconContext iconContext) {
        FileClient fileClient = FileClientFactory.getClient();
        if (fileClient == null) {
            throw PamirsException.construct(ExpEnumerate.NOTFOUND_FILE_CLIEND_CONFIG).errThrow();
        }

        for (Map.Entry<String, byte[]> jsonNameMap : result.getJsonNameMap().entrySet()) {
            byte[] value = jsonNameMap.getValue();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonRootBean jsonRootBean = objectMapper.readValue(value, JsonRootBean.class);
                iconContext.setParse(jsonRootBean);
                String id = iconContext.getParse().getId();
                iconContext.setSecondaryDirectory(IconCommonLogicService.ICONFONT + CharacterConstants.SEPARATOR_SLASH + id);
            } catch (IOException e) {
                throw PamirsException.construct(ExpEnumerate.JSON_INVALID, e).errThrow();
            }
        }

        for (Map.Entry<String, byte[]> cssNameMap : result.getCssNameMap().entrySet()) {
            String cssName = cssNameMap.getKey();
            CdnFile file = fileClient.upload(iconContext.getSecondaryDirectory() + cssName, new ByteArrayInputStream(cssNameMap.getValue()));
            iconContext.getCssUrls().add(file.getUrl());
        }

        for (Map.Entry<String, byte[]> jsNameMap : result.getJsNameMap().entrySet()) {
            String jsName = jsNameMap.getKey();
            CdnFile file = fileClient.upload(iconContext.getSecondaryDirectory() + jsName, new ByteArrayInputStream(jsNameMap.getValue()));
            iconContext.getJsUrls().add(file.getUrl());
        }

        for (Map.Entry<String, byte[]> fontNameMap : result.getFontNameMap().entrySet()) {
            String fontName = fontNameMap.getKey();
            CdnFile file = fileClient.upload(iconContext.getSecondaryDirectory() + fontName, new ByteArrayInputStream(fontNameMap.getValue()));
            iconContext.getFontUrls().add(file.getUrl());
        }

    }

    public void processJson(IconUnZipUtils.Result result, IconContext iconContext) {
        for (Map.Entry<String, byte[]> jsonNameMap : result.getJsonNameMap().entrySet()) {
            byte[] value = jsonNameMap.getValue();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonRootBean jsonRootBean = objectMapper.readValue(value, JsonRootBean.class);
                iconContext.setParse(jsonRootBean);
                String id = iconContext.getParse().getId();
                iconContext.setSecondaryDirectory(IconCommonLogicService.ICONFONT + CharacterConstants.SEPARATOR_SLASH + id);
            } catch (IOException e) {
                throw PamirsException.construct(ExpEnumerate.JSON_INVALID, e).errThrow();
            }
        }
    }

    public void processingGrouping(IconContext iconContext, ResourceIconGroup iconGroup) {
        ResourceIconGroup resourceIconGroup = iconGroup.queryOneByWrapper(Pops.<ResourceIconGroup>lambdaQuery()
                .from(ResourceIconGroup.MODEL_MODEL)
                .eq(ResourceIconGroup::getName, iconContext.getParse().getName()));
        if (resourceIconGroup != null) {
            Long i = resourceIconGroup.getBatchCode();
            i++;
            while (true) {
                ResourceIconGroup queryGroupName = iconGroup.queryOneByWrapper(Pops.<ResourceIconGroup>lambdaQuery()
                        .from(ResourceIconGroup.MODEL_MODEL)
                        .eq(ResourceIconGroup::getName, iconContext.getParse().getName() + "_" + i));
                if (queryGroupName != null) {
                    i++;
                } else {
                    break;
                }
            }
            iconGroup.setName(iconContext.getParse().getName() + "_" + i);
            resourceIconGroup.setBatchCode(i);
            resourceIconGroup.createOrUpdate();
        } else {
            iconGroup.setName(iconContext.getParse().getName());
        }
    }

    public void updateIcon(IconContext iconContext, ResourceIcon resourceIcon, Glyphs remove, List<String> fullFontClassList, List<ResourceIcon> resourceIconUpdate) {
        checkoutIcon(remove);
        boolean modify = false;
        if (!resourceIcon.getName().equals(remove.getName())) {
            resourceIcon.setName(remove.getName());
            modify = true;
        }
        if (!resourceIcon.getUnicode().equals(remove.getUnicode())) {
            resourceIcon.setUnicode(remove.getUnicode());
            modify = true;
        }
        if (!resourceIcon.getFontClass().equals(remove.getFont_class())) {
            resourceIcon.setFontClass(remove.getFont_class());
            modify = true;
        }
        if (!resourceIcon.getFullFontClass().equals(iconContext.getParse().getCss_prefix_text() + remove.getFont_class())) {
            resourceIcon.setFullFontClass(iconContext.getParse().getCss_prefix_text() + remove.getFont_class());
            fullFontClassList.add(resourceIcon.getFullFontClass());
            modify = true;
        }
        if (modify) {
            resourceIconUpdate.add(resourceIcon);
        }
    }

    public ResourceIconLib getIconLibAndProcessingFile(IconUnZipUtils.Result result, IconContext iconContext, BiConsumer<IconUnZipUtils.Result, IconContext> processFile) {
        iconContext.setCssUrls(new ArrayList<>());
        iconContext.setJsUrls(new ArrayList<>());
        iconContext.setFontUrls(new ArrayList<>());
        //创建资源对象
        iconContext.setResourceIconUpload(new ResourceIconUpload());
        //处理文件
        if (processFile != null) {
            processFile.accept(result, iconContext);
        }

        //判断图标库是否存在，如果存在，就走差量计算写数据库，如果不存在，就执行写数据库
        ResourceIconLib iconLib = new ResourceIconLib()
                .queryOneByWrapper(Pops.<ResourceIconLib>lambdaQuery()
                        .from(ResourceIconLib.MODEL_MODEL)
                        .eq(ResourceIconLib::getOutId, iconContext.getParse().getId())
                        .eq(ResourceIconLib::getType, IconLibTypeEnum.ICONFONT.getValue()));
        if (StringUtils.isBlank(iconContext.getParse().getName())
                || StringUtils.isBlank(iconContext.getParse().getId())
                || StringUtils.isBlank(iconContext.getParse().getCss_prefix_text())) {
            throw PamirsException.construct(ExpEnumerate.JSON_INVALID).errThrow();
        }
        return iconLib;
    }

    public void uploadLib(IconContext iconContext, ResourceIconLib iconLib) {
        iconLib.setName(iconContext.getParse().getName());
        iconLib.setType(IconLibTypeEnum.ICONFONT);
        iconLib.setFontClassPrefix(iconContext.getParse().getCss_prefix_text());
        iconLib.setCssUrls(iconContext.getCssUrls());
        iconLib.setJsUrls(iconContext.getJsUrls());
        iconLib.setFontUrls(iconContext.getFontUrls());
        iconLib.setDescription(iconContext.getParse().getDescription());
        iconLib.createOrUpdate();
    }

    public void uploadLibInit(IconContext iconContext, ResourceIconLib iconLib) {
        iconLib.setName(iconContext.getParse().getName());
        iconLib.setType(IconLibTypeEnum.ICONFONT);
        iconLib.setFontClassPrefix(iconContext.getParse().getCss_prefix_text());
        iconLib.setDescription(iconContext.getParse().getDescription());
        iconLib.createOrUpdate();
    }

    public void existLib(IconContext iconContext) {
        ResourceIconLib lib = new ResourceIconLib()
                .queryOneByWrapper(Pops.<ResourceIconLib>lambdaQuery()
                        .from(ResourceIconLib.MODEL_MODEL)
                        .eq(ResourceIconLib::getFontClassPrefix, iconContext.getParse().getCss_prefix_text()));
        if (lib != null) {
            throw PamirsException.construct(ExpEnumerate.FONT_CLASS_PREFIX_REPEAT, lib.getFontClassPrefix(), lib.getName()).errThrow();
        }
    }

    /**
     * 上传文件
     */
    public void uploadFiles(IconContext iconContext) {
        iconContext.getResourceIconUpload().setCssUrls(iconContext.getCssUrls());
        iconContext.getResourceIconUpload().setJsUrls(iconContext.getJsUrls());
        iconContext.getResourceIconUpload().setFontUrls(iconContext.getFontUrls());
    }

    public void checkoutIcon(Glyphs glyph) {
        if (StringUtils.isBlank(glyph.getIcon_id())
                || StringUtils.isBlank(glyph.getFont_class())
                || StringUtils.isBlank(glyph.getName())
                || StringUtils.isBlank(glyph.getUnicode())) {
            throw PamirsException.construct(ExpEnumerate.JSON_INVALID).errThrow();
        }
    }

    public void checkIconWithFontClass(List<String> fullfontclassList) {
        if (CollectionUtils.isEmpty(fullfontclassList)) {
            return;
        }
        List<ResourceIcon> iconList = new ResourceIcon().queryList(Pops.<ResourceIcon>lambdaQuery()
                .from(ResourceIcon.MODEL_MODEL)
                .in(ResourceIcon::getFullFontClass, fullfontclassList));
        for (ResourceIcon icon : iconList) {
            icon.fieldQuery(ResourceIcon::getLib);
            throw PamirsException.construct(ExpEnumerate.FONT_CLASS_REPEAT, icon.getLib().getFontClassPrefix(), icon.getLib().getName(), icon.getFontClass())
                    .errThrow();
        }
    }

    public String getFileDownloadUrl(IconContext iconContext, String fileName) {
        try {
            File file = new File(fileName);
            String prefixRegex = "^[a-zA-Z]:\\\\";
            // 检查并替换
            if (fileName.matches(prefixRegex + ".*")) {
                fileName = fileName.replaceFirst(prefixRegex, "");
            }
            InputStream inputStream = Files.newInputStream(file.toPath());
            return FileClientFactory.getClient().uploadByFileName(iconContext.getSecondaryDirectory() + fileName, inputStream);
        } catch (IOException e) {
            throw PamirsException.construct(ExpEnumerate.UPLOAD_FAILURE, e).errThrow();
        }
    }

    public String getUniqueKey(String iconId, String type, String libId) {
        return iconId + type + libId;
    }
}
