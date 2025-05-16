package pro.shushi.pamirs.resource.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.resource.api.model.ResourceIconGroup;

import java.util.List;

@Model(displayName = "上传文件传输模型")
@Model.model(ResourceIconUpload.MODEL_MODEL)
public class ResourceIconUpload extends TransientModel {

    public static final String MODEL_MODEL = "resource.ResourceIconUpload";

    //请求
    @Field.String
    @Field(displayName = "上传URL")
    private List<String> urls;

    @Field.Boolean
    @Field(displayName = "是否系统图标")
    private Boolean sys;

    //响应
    @Field.String
    @Field(displayName = "cssUrls")
    private List<String> cssUrls;

    @Field.String
    @Field(displayName = "jsUrls")
    private List<String> jsUrls;

    @Field.String
    @Field(displayName = "fontUrls")
    private List<String> fontUrls;

    @Field.many2many
    @Field(displayName = "分组")
    private List<ResourceIconGroup> iconGroupList;

    @Field.Integer
    @Field(displayName = "图标库id")
    private Long libId;
}
