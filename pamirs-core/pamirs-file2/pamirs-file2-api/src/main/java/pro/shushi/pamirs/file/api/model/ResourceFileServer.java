package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

@Base
@Model.model(ResourceFileServer.MODEL_MODEL)
@Model(displayName = "文件服务器")
@Model.Advanced(unique = {"name,type"})
public class ResourceFileServer extends IdModel {

    public static final String MODEL_MODEL = "file.ResourceFileServer";

    @Field.String
    @Field(displayName = "名称", required = true)
    private String name;

    @Field.String
    @Field(displayName = "类型", required = true)
    private String type;

    @Field.String
    @Field(displayName = "存储桶", required = true)
    private String bucket;

    @Field.String(size = 256)
    @Field(displayName = "上传URL/endpoint", required = true)
    private String uploadUrl;

    @Field.String(size = 256)
    @Field(displayName = "下载URL", required = true)
    private String downloadUrl;

    @Field.String
    @Field(displayName = "accessKeyId")
    private String accessKeyId;

    @Field.String(size = 256)
    @Field( displayName = "accessKeySecret")
    private String accessKeySecret;

    @Field.String
    @Field(displayName = "文件主目录")
    private String mainDir;

    @Field.String
    @Field(displayName = "配置数据")
    private String configJson;

    @Field.Integer
    @Field(displayName = "有效时间，单位ms")
    private Long validTime;

    @Field.Integer
    @Field(displayName = "超时时间，单位ms")
    private Long timeout;

    @Field.String(size = 256)
    @Field(displayName = "回调地址")
    private String callbackUrl;

    @Field.Boolean
    @Field(displayName = "是否有效")
    private Boolean active;

}
