package pro.shushi.pamirs.meta.dsl.constants;

public interface DSLDefineConstants {

    /**
     * 已完成执行[中断过]slot列表索引key
     */
    public String EXECUTED_SLOTS_DESCRIPTION = "_executed_slots_description_";

    /**
     * 执行成功(至少已经完成交接[异步])的返回结果，可能只是一个Future
     */
    public String SUCCESS_SLOTS_RESULT_MAP = "_success_slots_result_map_";

    /**
     * 执行成功(至少已经完成交接[异步])的返回结果，可能只是一个Future
     */
    public String SUCCESS_EXE_RESULT_MAP = "_success_exe_result_map_";

    /**
     * 存在执行失败的slot
     */
    public String SOME_SLOTS_EXECUTE_FAIL = "_some_slots_execute_fail";

    /**
     * 保存执行失败的slot的异常原因
     */
    String SOME_SLOTS_EXECUTE_FAIL_EXCEPTION = "_some_slots_execute_fail_exception";
    /**
     * 保存执行失败的slot
     */
    String SOME_SLOTS_EXECUTE_FAIL_SLOT = "_some_slots_execute_fail_slot";

    public String CURRENT_STATE_NAME = "_current_state_name";

    public String CURRENT_ITERATOR_INDEX = "$current_iterator_index";

    String DSL_RESULT_NAME = "_dsl_success_exe_result";

    String NAME_RESULT_SCHEMA = "resultSchema";

    String NAME_CONDITION = "condition";
}
