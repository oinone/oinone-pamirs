package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

@Base
@Model.model(ResourceFileForm.MODEL_MODEL)
@Model(displayName = "文件表单信息")
public class ResourceFileForm extends TransientModel {

    private static final long serialVersionUID = -6150291327192835335L;

    public static final String MODEL_MODEL = "file.ResourceFileForm";

    @Field.String
    @Field(displayName = "下载URL")
    private String downloadUrl;

    @Field.String
    @Field(displayName = "文件名")
    private String filename;

    @Field.Integer
    @Field(displayName = "文件大小")
    private Long fileSize;

    @Field.String
    @Field(displayName = "接受文件类型")
    private String accept;

    @Field.String
    @Field(displayName = "文件类型(MimeType)")
    private String contentType;

    @Field.String
    @Field(displayName = "指定CDN配置")
    private String cdnKey;

    @Field.many2one
    @Field(displayName = "分片文件信息")
    private List<ResourceChunkFile> chunkFiles;

    @Field.String
    @Field(displayName = "单文件上传")
    private String singleUploadJson;

    @Field.String
    @Field(displayName = "分片上传/断点续传")
    private String multipartUploadJson;
}
