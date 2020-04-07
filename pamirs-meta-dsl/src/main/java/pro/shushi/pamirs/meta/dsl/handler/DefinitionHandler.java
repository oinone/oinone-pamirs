package pro.shushi.pamirs.meta.dsl.handler;

import org.springframework.core.io.Resource;

public interface DefinitionHandler<T> {

    void deployProcess(Resource resource, Class<T> t);

}
