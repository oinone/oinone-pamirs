package pro.shushi.pamirs.channel.api;

import java.util.Set;

/**
 * EnhanceModelCache
 *
 * @author yakir on 2022/09/07 10:37.
 */
public interface EnhanceModelCache {

    Set<String> table2Model(String tableName);

    String model2Alias(String model);
}
