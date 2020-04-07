package pro.shushi.pamirs.meta.dsl.handler;

import pro.shushi.pamirs.meta.dsl.model.Process;

import java.util.List;

public interface TransformHandler<T> {

    List<Process> transform(T definition);

}
