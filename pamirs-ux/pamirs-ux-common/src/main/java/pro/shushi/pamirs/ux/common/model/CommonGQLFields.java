package pro.shushi.pamirs.ux.common.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * JSON 格式 GQL 字段
 *
 * @author Adamancy Zhang at 21:24 on 2025-11-18
 */
@Model.model(CommonGQLFields.MODEL_MODEL)
@Model(displayName = "GQL字段")
public class CommonGQLFields extends TransientModel {

    private static final long serialVersionUID = -6661900209821499994L;

    public static final String MODEL_MODEL = "core.common.CommonGQLFields";

    @Field.String
    @Field(displayName = "普通字段")
    private List<String> fields;

    @Field(displayName = "字段名", summary = "当且仅当关联关系字段有值")
    private String field;

    @Field.one2many
    @Field(displayName = "关联关系字段")
    private List<CommonGQLFields> relationFields;
}
