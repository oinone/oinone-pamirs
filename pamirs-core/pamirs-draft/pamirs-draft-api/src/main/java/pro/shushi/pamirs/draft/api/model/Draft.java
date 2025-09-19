package pro.shushi.pamirs.draft.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * 草稿上下文
 *
 * @author Gesi at 10:19 on 2025/9/1
 */
@Model.model(Draft.MODEL_MODEL)
@Model(displayName = "草稿")
@Model.Advanced(unique = {"code"})
@Model.Persistence(logicDelete = false)
public class Draft extends IdModel {

    public static final String MODEL_MODEL = "draft.Draft";

    @Field.String(size = 64)
    @Field(displayName = "草稿编码", required = true)
    private String code;

    @Field(displayName = "草稿所属模型", required = true)
    private String model;

    @Field(displayName = "草稿页面path")
    @Field.String(size = 1024)
    private String path;

    @Field(displayName = "草稿所属用户id")
    private Long userId;

    @Field(displayName = "草稿数据主键")
    private String dataPks;

    @Field(displayName = "草稿数据", summary = "模型内容序列化")
    @Field.Advanced(columnDefinition = "LONGTEXT")
    private String draftData;

}
