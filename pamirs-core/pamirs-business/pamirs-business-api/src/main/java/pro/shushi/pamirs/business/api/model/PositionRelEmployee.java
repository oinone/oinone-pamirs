package pro.shushi.pamirs.business.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.BaseRelation;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * @author shier
 * date  2020/6/19 2:25 下午
 */
@Model.Advanced(name = "PositionRelEmployee", relationship = NullableBoolEnum.TRUE, index = {"employeeId", "positionId"})
@Model.model(PositionRelEmployee.MODEL_MODEL)
@Model(displayName = "员工岗位关系表")
public class PositionRelEmployee extends BaseRelation {

    private static final long serialVersionUID = 5716595089649781301L;

    public static final String MODEL_MODEL = "PositionRelEmployee";

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "员工ID")
    private Long employeeId;

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "岗位ID")
    private Long positionId;

    public static PositionRelEmployee newInstance(PamirsEmployee employee, PamirsPosition position) {
        return new PositionRelEmployee()
                .setEmployeeId(employee.getId())
                .setPositionId(position.getId());
    }

}
