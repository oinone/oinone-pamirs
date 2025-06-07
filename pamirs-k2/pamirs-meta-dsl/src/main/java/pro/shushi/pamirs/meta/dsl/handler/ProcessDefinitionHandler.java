package pro.shushi.pamirs.meta.dsl.handler;

import org.springframework.core.io.Resource;
import pro.shushi.pamirs.meta.dsl.definition.node.process.Definition;

public class ProcessDefinitionHandler implements DefinitionHandler<Definition> {

    @Override
    public void deployProcess(Resource resource, Class<Definition> t) {

    }

    public static DefinitionHandler get() {
        return new ProcessDefinitionHandler();
    }

}
