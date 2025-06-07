package pro.shushi.pamirs.resource.api.spi.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;
import pro.shushi.pamirs.resource.api.enmu.IconLibTypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceIcon;
import pro.shushi.pamirs.resource.api.model.ResourceIconGroup;
import pro.shushi.pamirs.resource.api.model.ResourceIconLib;
import pro.shushi.pamirs.resource.api.pojo.Glyphs;
import pro.shushi.pamirs.resource.api.pojo.IconContext;
import pro.shushi.pamirs.resource.api.spi.api.ResourceSystemInitializationIcon;
import pro.shushi.pamirs.resource.api.util.IconUnZipUtils;

import java.util.*;

@SPI.Service(SystemInitializationIconImpl.ICONFONT)
@Component
@Slf4j
public class SystemInitializationIconImpl implements ResourceSystemInitializationIcon {

    public static final String ICONFONT = "ICONFONT";
    IconCommonLogicService iconCommonLogicService = new IconCommonLogicService();

    /**
     * 系统图标处理方式，写入数据库，传入oss
     *
     * @param resource 资源
     */
    @Override
    public void writeData(Resource resource) {
        if (!resource.exists() || !Objects.requireNonNull(resource.getFilename()).endsWith(".zip")) {
            log.error("文件非法");
            return;
        }
        try {
            //解压文件
            IconUnZipUtils.Result result = IconUnZipUtils.unzipFromStream(resource.getInputStream());
            //操作文件
            manipulatingFile(result);
            log.info("{}:文件上传成功", resource.getFilename());
        } catch (Exception e) {
            throw PamirsException.construct(ExpEnumerate.DECOMPRESSION_FAILURE, e).errThrow();
        }
    }

    /**
     * 操作文件
     */
    private void manipulatingFile(IconUnZipUtils.Result result) {
        IconContext iconContext = new IconContext();
        ResourceIconLib iconLib = iconCommonLogicService.getIconLibAndProcessingFile(result, iconContext, (res, context) -> iconCommonLogicService.processJson(res, context));
        if (iconLib != null) {
            //判断图标库前缀是否存在
            if (!iconLib.getFontClassPrefix().equals(iconContext.getParse().getCss_prefix_text())) {
                iconCommonLogicService.existLib(iconContext);
            }
            //执行差量计算,写数据库
            differenceQuantity(iconContext, iconLib);
        } else {
            iconCommonLogicService.existLib(iconContext);
            //写入数据库
            parseAndStoreData(iconContext);
        }

        //上传数据
        iconCommonLogicService.uploadFiles(iconContext);
    }

    /**
     * 图标库存在时，执行差量计算
     *
     * @param iconContext 上下文
     * @param iconLib     图标库
     */
    private void differenceQuantity(IconContext iconContext, ResourceIconLib iconLib) {

        //改变分组名称之后再次上传此文件，分组名称保持不变
        iconCommonLogicService.uploadLibInit(iconContext, iconLib);
        ResourceIconGroup iconGroup = new ResourceIconGroup().queryOneByWrapper(Pops.<ResourceIconGroup>lambdaQuery()
                .from(ResourceIconGroup.MODEL_MODEL)
                .eq(ResourceIconGroup::getId, iconLib.getGroupId()));
        //存入json的值
        Map<String, Glyphs> difVal = new HashMap<>();
        for (Glyphs glyph : iconContext.getParse().getGlyphs()) {
            difVal.put(iconCommonLogicService.getUniqueKey(glyph.getIcon_id(), "ICONFONT", iconContext.getParse().getId()), glyph);
        }
        List<ResourceIcon> resourceIcons = new ResourceIcon().queryList(Pops.<ResourceIcon>lambdaQuery()
                .from(ResourceIcon.MODEL_MODEL)
                .eq(ResourceIcon::getGroupId, iconGroup.getId()));
        List<ResourceIcon> resourceIconRemove = new ArrayList<>();
        List<ResourceIcon> resourceIconUpdate = new ArrayList<>();
        List<String> fullFontClassList = new ArrayList<>();
        for (ResourceIcon resourceIcon : resourceIcons) {
            Glyphs remove = difVal.remove(iconCommonLogicService.getUniqueKey(resourceIcon.getOutId(), resourceIcon.getType().getValue(), iconLib.getOutId()));
            //删除失败，说明数据库中多余
            if (remove == null) {
                resourceIconRemove.add(resourceIcon);
            } else {
                iconCommonLogicService.updateIcon(iconContext, resourceIcon, remove, fullFontClassList, resourceIconUpdate);
            }
        }
        new ResourceIcon().deleteByPks(resourceIconRemove);
        new ResourceIcon().updateBatch(resourceIconUpdate);
        iconCommonLogicService.checkIconWithFontClass(fullFontClassList);
        //剩余的图标就是需要新增的图标
        List<Glyphs> glyphs = new ArrayList<>(difVal.values());
        writeIcon(iconContext, glyphs, iconLib, iconGroup);
    }

    /**
     * 图标库不存在时 存数据库
     *
     * @param iconContext 上下文
     */
    private void parseAndStoreData(IconContext iconContext) {
        ResourceIconLib iconLib = new ResourceIconLib();
        ResourceIconGroup iconGroup = new ResourceIconGroup();
        iconCommonLogicService.processingGrouping(iconContext, iconGroup);
        iconGroup.setBatchCode(0L);
        iconGroup.setSys(Boolean.TRUE);
        iconGroup.create();

        iconLib.setOutId(iconContext.getParse().getId());
        iconLib.setGroupId(iconGroup.getId());
        iconCommonLogicService.uploadLibInit(iconContext, iconLib);
        List<Glyphs> glyphs = iconContext.getParse().getGlyphs();

        writeIcon(iconContext, glyphs, iconLib, iconGroup);
    }

    private void writeIcon(IconContext iconContext, List<Glyphs> glyphs, ResourceIconLib iconLib, ResourceIconGroup iconGroup) {
        List<ResourceIcon> iconList = new ArrayList<>();
        List<String> fullFontClassList = new ArrayList<>();
        for (Glyphs glyph : glyphs) {
            ResourceIcon resourceIcon = new ResourceIcon();
            iconCommonLogicService.checkoutIcon(glyph);
            resourceIcon.setOutId(glyph.getIcon_id());
            resourceIcon.setLibId(iconLib.getId());
            resourceIcon.setType(IconLibTypeEnum.ICONFONT);
            resourceIcon.setDisplayName(glyph.getName());
            resourceIcon.setName(glyph.getName());
            resourceIcon.setFontClass(glyph.getFont_class());
            resourceIcon.setUnicode(glyph.getUnicode());
            resourceIcon.setFullFontClass(iconContext.getParse().getCss_prefix_text() + glyph.getFont_class());
            resourceIcon.setShow(Boolean.TRUE);
            resourceIcon.setSys(Boolean.TRUE);
            resourceIcon.setGroupId(iconGroup.getId());
            iconList.add(resourceIcon);
            fullFontClassList.add(resourceIcon.getFullFontClass());
        }
        iconCommonLogicService.checkIconWithFontClass(fullFontClassList);
        new ResourceIcon().createOrUpdateBatch(iconList);
    }
}
