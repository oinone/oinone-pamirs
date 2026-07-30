package pro.shushi.pamirs.business.api.enumeration;

/**
 * 员工组织维度筛选 queryData 键（与模型字段名隔离，避免 queryPage 路由误判）
 */
public final class EmployeeScopeConstants {

    private EmployeeScopeConstants() {
    }

    /** 按部门编码筛选（匹配主部门和部门列表） */
    public static final String DEPARTMENT_CODE = "scopeDepartmentCode";

    /** 按角色编码筛选 */
    public static final String ROLE_CODE = "scopeRoleCode";

    /** 按岗位编码筛选 */
    public static final String POSITION_CODE = "scopePositionCode";
}
