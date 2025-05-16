package pro.shushi.pamirs.business.api.model;

import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.enumeration.PositionType;
import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.cache.UniqueKeyGenerator;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

import java.util.List;

@Model.model(PamirsPosition.MODEL_MODEL)
@Model.Advanced(name = "PamirsPosition", unique = {"code"}, index = {"name"})
@Model(displayName = "岗位", labelFields = "name")
@Model.Code(sequence = "SEQ", prefix = "P", size = 8)
public class PamirsPosition extends BizCodeModel implements IDataStatus {

    private static final long serialVersionUID = -4289587846886442731L;

    public static final String MODEL_MODEL = "business.PamirsPosition";

    public static final UniqueKeyGenerator<PamirsPosition, String> UNIQUE_KEY_GENERATOR = PamirsPosition::getCode;

    @Field.Enum
    @Field(displayName = "岗位类型", required = true)
    private PositionType positionType;

    @Field.String(size = 255)
    @Field(displayName = "岗位名称", required = true)
    private String name;

    @Field.Enum
    @Field(displayName = "数据状态", required = true, defaultValue = "ENABLED")
    private DataStatusEnum dataStatus;

    @Field.many2one
    @Field.Relation(relationFields = {"departmentCode"}, referenceFields = {"code"})
    @Field(displayName = "所属部门")
    private PamirsDepartment department;

    @Field.String
    @Field(displayName = "所属部门编码", invisible = true)
    private String departmentCode;

    @Field.many2one
    @Field.Relation(relationFields = {"companyCode"}, referenceFields = {"code"})
    @Field(displayName = "所属公司")
    private PamirsCompany company;

    @Field.String
    @Field(displayName = "所属公司编码", invisible = true)
    private String companyCode;

    @Field.many2one
    @Field.Relation(relationFields = {"parentCode"}, referenceFields = {"code"})
    @Field(displayName = "直属岗位")
    private PamirsPosition parent;

    @Field.String
    @Field(displayName = "直属岗位编码", invisible = true)
    private String parentCode;

    @Field.many2many(through = PositionRelEmployee.MODEL_MODEL, relationFields = {"positionId"}, referenceFields = {"employeeId"})
    @Field.Relation(relationFields = {"id"}, referenceFields = {"id"})
    @Field(displayName = "员工列表")
    private List<PamirsEmployee> employeeList;

    @Field.Integer
    @Field(displayName = "员工数")
    private Long employeeCount;

}
