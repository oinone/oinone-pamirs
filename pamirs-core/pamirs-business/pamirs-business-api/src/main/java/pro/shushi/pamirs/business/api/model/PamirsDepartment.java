package pro.shushi.pamirs.business.api.model;

import pro.shushi.pamirs.boot.web.constants.BusinessModelConstants;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.behavior.ITreeCodeModel;
import pro.shushi.pamirs.core.common.cache.UniqueKeyGenerator;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

@Model.model(PamirsDepartment.MODEL_MODEL)
@Model.Advanced(name = "PamirsDepartment", unique = {"code", "treeCode"}, index = {"name", "parentCode"})
@Model(displayName = "部门", labelFields = "name")
@Model.Code(sequence = "SEQ", prefix = "D", size = 8)
public class PamirsDepartment extends BizCodeModel implements IDataStatus, ITreeCodeModel {

    private static final long serialVersionUID = -265364687793309168L;

    public static final String MODEL_MODEL = BusinessModelConstants.DEPARTMENT;

    public static final UniqueKeyGenerator<PamirsDepartment, String> UNIQUE_KEY_GENERATOR = PamirsDepartment::getCode;

    @Base
    @Field.String(size = 64)
    @Field(displayName = "编码", unique = true, required = true, priority = 90)
    private String code;

    @Field.String(size = 32)
    @Field(displayName = "部门类型")
    private String departmentType;

    @Field.String(size = 255)
    @Field(displayName = "部门名称", required = true, translate = true)
    private String name;

    @Field.Enum
    @Field(displayName = "数据状态", required = true, defaultValue = "ENABLED")
    private DataStatusEnum dataStatus;

    @Field.many2one
    @Field.Relation(relationFields = {"parentCode"}, referenceFields = {"code"})
    @Field(displayName = "上级部门")
    private PamirsDepartment parent;

    @Field.one2many
    @Field.Relation(relationFields = {"code"}, referenceFields = {"parentCode"})
    @Field(displayName = "下级部门")
    private List<PamirsDepartment> childList;

    @Field.String
    @Field(displayName = "上级部门编码", invisible = true)
    private String parentCode;

    @Field.String(size = 256)
    @Field(displayName = "树编码", required = true, invisible = true)
    private String treeCode;

    @Field.String
    @Field(displayName = "备注")
    private String description;

    @Field.many2one
    @Field.Relation(relationFields = {"companyCode"}, referenceFields = {"code"})
    @Field(displayName = "所属公司")
    private PamirsCompany company;

    @Field.String
    @Field(displayName = "所属公司编码", invisible = true)
    private String companyCode;

    @Field.one2many
    @Field.Relation(relationFields = {"code"}, referenceFields = {"departmentCode"})
    @Field(displayName = "岗位列表")
    private List<PamirsPosition> positionList;

    @Field.many2many(through = DepartmentRelEmployee.MODEL_MODEL, relationFields = {"departmentType", "departmentCode"}, referenceFields = {"employeeType", "employeeCode"})
    @Field.Relation(relationFields = {"departmentType", "code"}, referenceFields = {"employeeType", "code"})
    @Field(displayName = "员工列表")
    private List<PamirsEmployee> employeeList;

    @Field.Integer
    @Field(displayName = "优先级", defaultValue = MetaDefaultConstants.PRIORITY_VALUE_STRING)
    private Long priority;

    @Field.many2one
    @Field.Relation(store = false)
    @Field(displayName = "直属主管", store = NullableBoolEnum.FALSE)
    private PamirsEmployee immediateSupervisor;

    @Field.Boolean
    @Field(displayName = "是否为部门主管", store = NullableBoolEnum.FALSE)
    private Boolean supervisor;
}
