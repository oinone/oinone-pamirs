package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * @author yeshenyue on 2024/9/9 09:27.
 */
@Base
@Model.model(EipParamMappingItem.MODEL_MODEL)
@Model(displayName = "映射参数定义", labelFields = "from")
public class EipParamMappingItem extends TransientModel {

    private static final long serialVersionUID = -3939048671338603747L;
    public static final String MODEL_MODEL = "pamirs.eip.EipParamMappingItem";

    @Field.String
    @Field(displayName = "参数名", required = true)
    private String key;

    @Field.String
    @Field(displayName = "映射字段值", required = true)
    private String to;

    @Field.String
    @Field(displayName = "取值表达式", required = true)
    private String valueExpr;

    /**
     * children接收前端递归数据，实际存储时打平
     */
    private List<EipParamMappingItem> children;
}