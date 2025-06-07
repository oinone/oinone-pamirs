package pro.shushi.pamirs.resource.core.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceRegion;
import pro.shushi.pamirs.resource.api.service.ResourceRegionService;
import pro.shushi.pamirs.resource.api.tmodel.ResourceRegionProxyModel;
import pro.shushi.pamirs.resource.api.util.RemoteResourceHelper;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.*;

@Component
@Model.model(ResourceRegion.MODEL_MODEL)
public class ResourceRegionAction {

    @Autowired
    private ResourceRegionService ariesResourceRegionService;

    /**
     * 前端缓存
     *
     * @param region
     * @return
     */
    @Action(contextType = ActionContextTypeEnum.CONTEXT_FREE, displayName = "刷新地址数据")
    @Function(openLevel = {LOCAL, REMOTE, API})
    public ResourceRegion syncRegion(ResourceRegion region) {
        return ariesResourceRegionService.updateRegionFile(region);
    }

    @Function(openLevel = {API, REMOTE}, summary = "获取地址库文件地址")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public ResourceRegionProxyModel fetchRegionDownloadUrl(ResourceRegionProxyModel region) {
//        if (StringUtils.isBlank(RemoteResourceHelper.DOWNLOAD_URL)) {
//            ariesResourceRegionService.updateRegionFile(region);
//        }
        return new ResourceRegionProxyModel().setDownloadUrl(RemoteResourceHelper.DOWNLOAD_URL);
    }
}
