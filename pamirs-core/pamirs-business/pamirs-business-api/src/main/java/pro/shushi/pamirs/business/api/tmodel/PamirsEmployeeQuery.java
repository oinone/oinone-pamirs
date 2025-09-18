package pro.shushi.pamirs.business.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

/**
 * 员工查询条件模型
 *
 * @author Gesi at 20:26 on 2025/9/18
 */
@Model(displayName = "员工查询条件模型")
@Model.model(PamirsEmployeeQuery.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
public class PamirsEmployeeQuery extends TransientModel {

    public static final String MODEL_MODEL = "business.PamirsEmployeeQuery";

    @Field(displayName = "domain rsql")
    private String domainRsql;

    @Field(displayName = "员工编码")
    private List<String> empCodes;

    @Field(displayName = "部门编码")
    private List<String> deptCodes;

    @Field(displayName = "角色编码")
    private List<String> roleCodes;

    @Field(displayName = "当前用户所绑定员工")
    private Boolean userEmployee;

    @Field(displayName = "当前用户所属部门中的员工")
    private Boolean userDept;

    @Field(displayName = "当前用户所属部门及下属部门中的员工")
    private Boolean userDeptAndChildren;

}
