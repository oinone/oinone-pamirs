package pro.shushi.pamirs.meta.dsl.init;

import pro.shushi.pamirs.meta.dsl.definition.node.Logic;
import pro.shushi.pamirs.meta.dsl.definition.node.process.Definition;
import pro.shushi.pamirs.meta.dsl.handler.DefinitionHandler;
import pro.shushi.pamirs.meta.dsl.handler.LogicTransformHander;
import pro.shushi.pamirs.meta.dsl.handler.ProcessDefinitionHandler;
import pro.shushi.pamirs.meta.dsl.handler.TransformHandler;

import java.util.HashMap;
import java.util.Map;

public class InitFramework {

    private static Map<String, DefinitionHandler> definitionHandlerMap= new HashMap();

    private static Map<String, TransformHandler> transformHandlerMap= new HashMap();

    static {
        InitFramework.definitionHandlerMap.put(Definition.class.getName(), ProcessDefinitionHandler.get());

        InitFramework.transformHandlerMap.put(Logic.class.getName(), LogicTransformHander.get());
    }

    public static TransformHandler getTransformer(String name){
        return transformHandlerMap.get(name);
    }

}
