package pro.shushi.pamirs.sequence.manager;

import pro.shushi.pamirs.sequence.common.Result;

/**
 * ILeaf
 *
 * @author yakir on 2020/04/08 16:17.
 */
public interface ILeaf {

    Result get(final String key, int rstep);

    Result getOrderID(final String key, Integer step);

    void init(/*ApplicationStartedEvent event*/);

    boolean getInitStatus();
}
