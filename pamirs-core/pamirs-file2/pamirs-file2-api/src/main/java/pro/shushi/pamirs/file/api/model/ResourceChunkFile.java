package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * 分片文件
 *
 * @author Adamancy Zhang at 09:49 on 2024-05-31
 */
@Base
@Model.model(ResourceChunkFile.MODEL_MODEL)
@Model(displayName = "分片文件")
public class ResourceChunkFile extends TransientModel {

    private static final long serialVersionUID = 1834987970958944979L;

    public static final String MODEL_MODEL = "file.ResourceChunkFile";

    @Field.Integer
    @Field(displayName = "分片序号")
    private Integer partNumber;

    @Field.Integer
    @Field(displayName = "文件大小")
    private Long fileSize;
}
