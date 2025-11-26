package pro.shushi.pamirs.draft.model;

import pro.shushi.pamirs.core.common.serialize.ToStringSerializeProcessor;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.List;

/**
 * 草稿上下文
 *
 * @author Gesi at 10:19 on 2025/9/1
 */
@Base
@Model.model(Draft.MODEL_MODEL)
@Model(displayName = "草稿")
@Model.Advanced(unique = {"code"})
public class Draft extends IdModel {

    private static final long serialVersionUID = 103136745530161641L;

    public static final String MODEL_MODEL = "draft.Draft";

    @Field.String(size = 64)
    @Field(displayName = "草稿编码", required = true)
    private String code;

    @Field.String
    @Field(displayName = "模型编码", required = true)
    private String model;

    @Field.Text
    @Field(displayName = "资源路径")
    private String path;

    @Field.Text
    @Field(displayName = "草稿数据主键", multi = true)
    private List<String> dataPks;

    @Field.String(size = 64)
    @Field(displayName = "用户ID")
    private String userId;

    @Field.Integer
    @Field(displayName = "失效时间")
    private Long invalidDate;

    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "草稿数据", serialize = ToStringSerializeProcessor.TYPE)
    private Object draftData;
}
