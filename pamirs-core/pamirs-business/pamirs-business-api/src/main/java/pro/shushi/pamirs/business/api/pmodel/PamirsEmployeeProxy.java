package pro.shushi.pamirs.business.api.pmodel;

import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * <p>
 * This class was created by Yexiu on 2024/6/12 at 18:23.
 * </p>
 *
 * @author Yexiu
 * @since 2024/6/12
 */
@Model.model(PamirsEmployeeProxy.MODEL_MODEL)
@Model(displayName = "员工导出代理")
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class PamirsEmployeeProxy extends PamirsEmployee {

    private static final long serialVersionUID = -6582160484690807999L;

    public static final String MODEL_MODEL = "business.PamirsEmployeeProxy";

    @Field.String
    @Field(displayName = "部门名称列表")
    private String departmentNameList;

    @Field.String
    @Field(displayName = "部门编码列表")
    private String departmentCodeList;

    @Field.String
    @Field(displayName = "角色编码列表")
    private String roleCodes;
}
