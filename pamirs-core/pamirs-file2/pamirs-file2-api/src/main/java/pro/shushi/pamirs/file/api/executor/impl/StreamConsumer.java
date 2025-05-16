package pro.shushi.pamirs.file.api.executor.impl;

import java.io.IOException;

/**
 * IO流消费者
 *
 * @author Adamancy Zhang at 20:41 on 2024-03-28
 */
public interface StreamConsumer<T> {

    void accept(T stream) throws IOException;

}
