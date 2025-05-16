package pro.shushi.pamirs.trigger.constant;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 工作流默认监听的三个表
 *
 * @author wangxian
 */
public interface WorkFlowConstant {

    //flow_biz_data\\.workflow_workflow_instance,flow_biz_data\\.workflow_workflow_timer_instance,flow_biz_data\\.workflow_workflow_user_task

    List<String> DEFAULT_WORKFLOW_FILTER_TABLES = Lists.newArrayList(
            "workflow_workflow_timer_instance","workflow_workflow_user_task");

}
