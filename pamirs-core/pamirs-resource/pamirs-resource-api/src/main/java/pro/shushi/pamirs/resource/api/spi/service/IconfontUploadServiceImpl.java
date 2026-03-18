package pro.shushi.pamirs.resource.api.spi.service;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.URLHelper;
import pro.shushi.pamirs.framework.connectors.cdn.client.FileClient;
import pro.shushi.pamirs.framework.connectors.cdn.client.LocalFileClient;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
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
import pro.shushi.pamirs.resource.api.spi.api.ResourceIconUploadApi;
import pro.shushi.pamirs.resource.api.tmodel.ResourceIconUpload;
import pro.shushi.pamirs.resource.api.util.IconUnZipUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

@SPI.Service(IconfontUploadServiceImpl.ICONFONT)
@Component
@Slf4j
public class IconfontUploadServiceImpl implements ResourceIconUploadApi {

    public static final String ICONFONT = "ICONFONT";
    IconCommonLogicService iconCommonLogicService = new IconCommonLogicService();

    @Override
    public List<ResourceIconUpload> uploadUrl(ResourceIconUpload requestFile) {
        List<String> downloadUrls = requestFile.getUrls();
        List<ResourceIconUpload> resourceIconUploadList = new ArrayList<>();
        for (String downloadUrl : downloadUrls) {
            try {
                FileClient client = FileClientFactory.getClient();
                if (!(client instanceof LocalFileClient)) {
                    URL url = new URL(Objects.requireNonNull(URLHelper.decode(downloadUrl)));
                    downloadUrl = URLHelper.repairRelativePath(url.getPath());
                    if (!downloadUrl.endsWith(".zip")) {
                        throw PamirsException.construct(ExpEnumerate.NON_ZIP_FILE).errThrow();
                    }
                }

                // 获取压缩包文件流
                InputStream downloadStream = FileClientFactory.getClient().getDownloadStream(downloadUrl);
                if (downloadStream == null) {
                    throw PamirsException.construct(ExpEnumerate.FILE_STREAM_FAILURE).errThrow();
                }
                IconUnZipUtils.Result result = IconUnZipUtils.unzipFromStream(downloadStream);
                if (MapUtils.isEmpty(result.getCssNameMap()) || MapUtils.isEmpty(result.getJsonNameMap()) ||
                        MapUtils.isEmpty(result.getJsNameMap())) {
                    throw PamirsException.construct(ExpEnumerate.NON_ZIP_FILE).errThrow();
                }

                ResourceIconUpload resourceIconUpload = manipulatingFiles(result, requestFile);
                resourceIconUploadList.add(resourceIconUpload);
                log.info("{}: File upload success", downloadUrl);
            } catch (Exception e) {
                throw PamirsException.construct(ExpEnumerate.DECOMPRESSION_FAILURE, e).errThrow();
            }
        }
        return resourceIconUploadList;
    }


    /**
     * 操作文件
     */
    private ResourceIconUpload manipulatingFiles(IconUnZipUtils.Result result, ResourceIconUpload requestFile) {
        IconContext iconContext = new IconContext();
        ResourceIconLib iconLib = iconCommonLogicService.getIconLibAndProcessingFile(result, iconContext, (res, context) -> iconCommonLogicService.processJsonAndRetrieveUrls(res, context));
        if (iconLib != null) {
            //判断图标库前缀是否存在
            if (!iconLib.getFontClassPrefix().equals(iconContext.getParse().getCss_prefix_text())) {
                iconCommonLogicService.existLib(iconContext);
            }
            iconContext.getResourceIconUpload().setLibId(iconLib.getId());
            //执行差量计算,写数据库
            differenceQuantity(iconContext, requestFile, iconLib);
        } else {
            //判断图标库前缀是否存在
            iconCommonLogicService.existLib(iconContext);
            //写入数据库
            parseAndStoreData(iconContext, requestFile);
        }

        //上传数据
        iconCommonLogicService.uploadFiles(iconContext);

        return iconContext.getResourceIconUpload();
    }

    /**
     * 图标库存在时，执行差量计算
     *
     * @param iconContext 上下文
     * @param requestFile 请求
     * @param iconLib     图标库
     */
    private void differenceQuantity(IconContext iconContext, ResourceIconUpload requestFile, ResourceIconLib iconLib) {
        //改变分组名称之后再次上传此文件，分组名称保持不变
        List<ResourceIconGroup> batchGroup = new ArrayList<>();
        iconCommonLogicService.uploadLib(iconContext, iconLib);
        ResourceIconGroup iconGroup = new ResourceIconGroup().queryOneByWrapper(Pops.<ResourceIconGroup>lambdaQuery()
                .from(ResourceIconGroup.MODEL_MODEL)
                .eq(ResourceIconGroup::getId, iconLib.getGroupId()));
        batchGroup.add(iconGroup);

        //存入json的值
        Map<String, Glyphs> difVal = new HashMap<>();
        for (Glyphs glyph : iconContext.getParse().getGlyphs()) {
            difVal.put(iconCommonLogicService.getUniqueKey(glyph.getIcon_id(), ICONFONT, iconContext.getParse().getId()), glyph);
        }
        List<ResourceIcon> resourceIcons = new ResourceIcon().queryList(Pops.<ResourceIcon>lambdaQuery()
                .from(ResourceIcon.MODEL_MODEL)
                .eq(ResourceIcon::getLibId, iconLib.getId()));
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
                Long groupId = resourceIcon.getGroupId();
                ResourceIconGroup group = new ResourceIconGroup().queryOneByWrapper(Pops.<ResourceIconGroup>lambdaQuery()
                        .from(ResourceIconGroup.MODEL_MODEL)
                        .eq(ResourceIconGroup::getId, groupId)
                        .ne(ResourceIconGroup::getName, iconGroup.getName()));
                if (group != null && !batchGroup.get(batchGroup.size() - 1).getId().equals(group.getId())) {
                    batchGroup.add(group);
                }
            }
        }
        iconCommonLogicService.checkIconWithFontClass(fullFontClassList);
        new ResourceIcon().deleteByPks(resourceIconRemove);
        new ResourceIcon().updateBatch(resourceIconUpdate);
        iconContext.getResourceIconUpload().setIconGroupList(batchGroup);
        //剩余的图标就是新增的图标
        List<Glyphs> values = new ArrayList<>(difVal.values());
        writeIcon(requestFile, iconContext, values, iconGroup, iconLib);
    }

    /**
     * 图标库不存在时 存数据库
     *
     * @param iconContext 上下文
     */
    private void parseAndStoreData(IconContext iconContext, ResourceIconUpload requestFile) {
        ResourceIconLib iconLib = new ResourceIconLib();
        ResourceIconGroup iconGroup = new ResourceIconGroup();
        List<ResourceIconGroup> batchGroup = new ArrayList<>();

        iconCommonLogicService.processingGrouping(iconContext, iconGroup);
        iconGroup.setBatchCode(0L);
        iconGroup.setSys(Boolean.FALSE);
        iconGroup.create();

        iconLib.setOutId(iconContext.getParse().getId());
        iconLib.setGroupId(iconGroup.getId());
        iconCommonLogicService.uploadLib(iconContext, iconLib);
        batchGroup.add(iconGroup);
        iconContext.getResourceIconUpload().setLibId(iconLib.getId());

        iconContext.getResourceIconUpload().setIconGroupList(batchGroup);
        List<Glyphs> values = iconContext.getParse().getGlyphs();

        //添加图标
        writeIcon(requestFile, iconContext, values, iconGroup, iconLib);
    }

    public void writeIcon(ResourceIconUpload requestFile, IconContext iconContext, List<Glyphs> values, ResourceIconGroup iconGroup, ResourceIconLib iconLib) {
        List<ResourceIcon> iconList = new ArrayList<>();
        List<String> fullFontClassList = new ArrayList<>();
        for (Glyphs glyph : values) {
            iconCommonLogicService.checkoutIcon(glyph);
            ResourceIcon resourceIcon = new ResourceIcon();
            resourceIcon.setOutId(glyph.getIcon_id());
            resourceIcon.setType(IconLibTypeEnum.ICONFONT);
            resourceIcon.setDisplayName(glyph.getName());
            resourceIcon.setName(glyph.getName());
            resourceIcon.setFontClass(glyph.getFont_class());
            resourceIcon.setUnicode(glyph.getUnicode());
            resourceIcon.setFullFontClass(iconContext.getParse().getCss_prefix_text() + glyph.getFont_class());
            resourceIcon.setShow(Boolean.TRUE);
            resourceIcon.setSys(requestFile.getSys() != null ? requestFile.getSys() : Boolean.FALSE);
            resourceIcon.setGroupId(iconGroup.getId());
            resourceIcon.setLibId(iconLib.getId());
            iconList.add(resourceIcon);
            fullFontClassList.add(resourceIcon.getFullFontClass());
        }
        iconCommonLogicService.checkIconWithFontClass(fullFontClassList);
        new ResourceIcon().createBatch(iconList);
    }
}