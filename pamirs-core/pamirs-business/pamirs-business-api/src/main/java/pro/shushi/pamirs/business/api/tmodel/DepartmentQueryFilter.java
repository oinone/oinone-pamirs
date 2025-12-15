package pro.shushi.pamirs.business.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

/**
 * 部门查询条件模型
 *
 * @author Adamancy Zhang at 16:32 on 2025-12-03
 */
@Base
@Model(displayName = "部门查询条件模型")
@Model.model(DepartmentQueryFilter.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
public class DepartmentQueryFilter extends TransientModel {

    private static final long serialVersionUID = -8690145071835461029L;

    public static final String MODEL_MODEL = "business.DepartmentQueryWrapper";

    @Field(displayName = "RSQL过滤条件")
    private String rsql;

    @Field(displayName = "部门编码")
    private List<String> departmentCodes;

    @Field(displayName = "当前用户所在公司的所有部门")
    private Boolean userCompanyDept;

    @Field(displayName = "当前用户所处部门")
    private Boolean userDept;

    @Field(displayName = "当前用户所处部门及下级部门")
    private Boolean userDeptAndChildren;

}
