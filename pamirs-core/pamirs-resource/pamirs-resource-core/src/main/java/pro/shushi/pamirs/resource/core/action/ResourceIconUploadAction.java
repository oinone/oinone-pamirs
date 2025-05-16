package pro.shushi.pamirs.resource.core.action;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;
import pro.shushi.pamirs.resource.api.enmu.IconLibTypeEnum;
import pro.shushi.pamirs.resource.api.spi.api.ResourceIconUploadApi;
import pro.shushi.pamirs.resource.api.tmodel.ResourceIconUpload;

import java.util.List;

@Component
@Model.model(ResourceIconUpload.MODEL_MODEL)
public class ResourceIconUploadAction {

    @Transactional
    @Action.Advanced(type = FunctionTypeEnum.QUERY)
    @Action(displayName = "上传文件")
    public List<ResourceIconUpload> uploads(ResourceIconUpload uploadUrl) {
        List<ResourceIconUpload> resourceIconUploads = Spider.getExtension(ResourceIconUploadApi.class, IconLibTypeEnum.ICONFONT.getValue()).uploadUrl(uploadUrl);
        if (resourceIconUploads == null) {
            throw PamirsException.construct(ExpEnumerate.UPLOAD_FAILURE).errThrow();
        }
        return resourceIconUploads;
    }
}