package pro.shushi.pamirs.user.api.constants;

/**
 * 常量
 *
 * @author Adamancy Zhang at 09:51 on 2024-01-05
 */
public interface UserConstants {

    Long ANONYMOUS_USER_ID = 10000L;
    String ANONYMOUS_USER_CODE = "10000";
    String ANONYMOUS_USER_LOGIN = "anonymous";
    String ANONYMOUS_USER_PASSWORD = "anonymous";
    String ANONYMOUS_USER_NICKNAME = "匿名";

    Long ADMIN_USER_ID = 10001L;
    String ADMIN_USER_CODE = "10001";
    String ADMIN_USER_LOGIN = "admin";
    String ADMIN_USER_PASSWORD = "admin";
    String ADMIN_USER_NICKNAME = "超级管理员";

    String SINGLE_USER_CACHE_MODE = "single";
    String MULTIPLE_USER_CACHE_MODE = "multiple";

    String WORKFLOW_TASK_HANDOVER_NAMESPACE = "workflow.WorkflowTaskHandoverService";
    String WORKFLOW_UPDATE_HANDOVER_FROM_USER_NAME_FUN = "updateFromUserName";
    String WORKFLOW_UPDATE_HANDOVER_TO_EMPLOYEE_NAME_FUN = "updateToEmployeeName";

    String WORKFLOW_TASK_DELEGATION_NAMESPACE = "workflow.WorkflowTaskDelegationService";
    String WORKFLOW_UPDATE_DELEGATION_FROM_USER_NAME_FUN = "updateFromUserName";
    String WORKFLOW_UPDATE_DELEGATION_EMPLOYEE_NAME_FUN = "updateToEmployeeName";
}
