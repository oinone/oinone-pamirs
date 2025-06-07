package pro.shushi.pamirs.resource.api.constants;

public interface ContextVariableConstants {

    String serverContextSymbol = "$";

    String clientContextSymbol = "#";

    String activeModel = "activeModel";

    String activeId = "activeId";

    String activeIds = "activeIds";

    String activeAction = "activeAction";

    String activeViewType = "activeViewType";

    String activeViewId = "activeViewId";

    String currentModuleId = "moduleId";

    String currentUser = serverContextSymbol + "currentUser";

    String currentCorp = serverContextSymbol + "currentCorp";

    String ENABLE_SUDO = "ENABLE_SUDO";

    String ENABLE_INCR = "ENABLE_INCR";

    String DISABLE_COMPUTE = "DISABLE_COMPUTE";

    String DISABLE_RELATION = "DISABLE_RELATION";

    String ENABLE_DATA_DIFF = "ENABLE_DATA_DIFF";

    String ENABLE_LOAD = "ENABLE_LOAD";

    String IGNORE_PARENT = "IGNORE_PARENT";

    String IGNORE_CHILDREN = "IGNORE_CHILDREN";

    String ENABLE_SYSTEM_DIFF = "ENABLE_SYSTEM_DIFF";

    String OP_TYPE = "OP_TYPE";

    String EVENT_TYPE = "EVENT_TYPE";

    String B2CNO_COMPUTE = "B2CNO_COMPUTE";

}
