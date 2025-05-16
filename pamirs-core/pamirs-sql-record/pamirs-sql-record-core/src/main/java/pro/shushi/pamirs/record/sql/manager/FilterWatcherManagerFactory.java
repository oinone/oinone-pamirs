package pro.shushi.pamirs.record.sql.manager;

/**
 * FilterWatcherManagerFactory
 *
 * @author yakir on 2023/10/19 17:00.
 */
public class FilterWatcherManagerFactory {

    private FilterWatcherManager filterWatcherManager;

    public static FilterWatcherManager getInstance() {
        return FilterWatcherManagerInner.INSTANCE;
    }

    private static class FilterWatcherManagerInner {
        public static final FilterWatcherManager INSTANCE = new FilterWatcherManager();
    }

}
