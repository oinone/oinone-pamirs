package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * @author Gesi at 15:46 on 2025/9/1
 */
@Model(displayName = "分组已选择字段")
@Model.model(GroupSelectField.MODEL_MODEL)
public class GroupSelectField extends TransientModel {

    public static final String MODEL_MODEL = "base.GroupSelectField";

    @Field(displayName = "分组字段信息")
    private GroupField groupField;

    @Field(displayName = "当前分组需要查询的值", summary = "用string表示")
    private String groupValue;

    @Field(displayName = "下一级分组的所有已选择字段")
    private List<GroupSelectField> childGroupSelectFields;

}
