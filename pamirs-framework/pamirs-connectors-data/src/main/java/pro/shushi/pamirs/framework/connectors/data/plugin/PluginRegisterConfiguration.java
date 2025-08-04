package pro.shushi.pamirs.framework.connectors.data.plugin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.framework.connectors.data.optimize.JSqlParserCountOptimize;
import pro.shushi.pamirs.framework.connectors.data.plugin.debug.SqlDebugInterceptor;
import pro.shushi.pamirs.framework.connectors.data.plugin.logicdelete.LogicDeleteInterceptor;
import pro.shushi.pamirs.framework.connectors.data.plugin.optlock.OptimisticLockerInterceptor;
import pro.shushi.pamirs.framework.connectors.data.plugin.page.PageLocalCacheDisableInterceptor;
import pro.shushi.pamirs.framework.connectors.data.plugin.page.PaginationInterceptor;
import pro.shushi.pamirs.framework.connectors.data.plugin.sequence.IdGeneratorInterceptor;
import pro.shushi.pamirs.framework.connectors.data.plugin.sequence.SequenceGeneratorInterceptor;
import pro.shushi.pamirs.framework.connectors.data.plugin.sql.IllegalSQLInterceptor;
import pro.shushi.pamirs.framework.connectors.data.plugin.sql.SqlDialectInterceptor;
import pro.shushi.pamirs.framework.connectors.data.plugin.type.TypeAndToolInterceptor;

/**
 * 持久层插件注册配置
 * <p>
 * 2020/6/13 5:08 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Configuration
public class PluginRegisterConfiguration {

    /**
     * SQL调试插件
     *
     * @return 调试插件
     */
    @Bean
    @Order(10000)
    public SqlDebugInterceptor sqlDebugInterceptor() {
        return new SqlDebugInterceptor();
    }

    /**
     * SQL方言插件
     *
     * @return 方言插件
     */
    @Bean
    @Order(9999)
    public SqlDialectInterceptor sqlDialectInterceptor() {
        return new SqlDialectInterceptor();
    }

    /**
     * 类型与工具插件
     *
     * @return 工具插件
     */
    @Bean
    @Order(999)
    public TypeAndToolInterceptor toolInterceptor() {
        return new TypeAndToolInterceptor();
    }

    /**
     * 页缓存失效插件
     *
     * @return 页缓存失效插件
     */
    @Bean
    @Order(999)
    public PageLocalCacheDisableInterceptor pageLocalCacheDisableInterceptor() {
        return new PageLocalCacheDisableInterceptor();
    }

    /**
     * 分页插件
     *
     * @return 分页插件
     */
    @Bean
    @Order(999)
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        // paginationInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        // paginationInterceptor.setLimit(500);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setCountSqlParser(new JSqlParserCountOptimize(true));
        return paginationInterceptor;
    }

    /**
     * 逻辑删除插件
     *
     * @return 逻辑删除插件
     */
    @Bean
    @Order(999)
    public LogicDeleteInterceptor logicDeleteInterceptor() {
        return new LogicDeleteInterceptor();
    }

    /**
     * 乐观锁插件
     *
     * @return 乐观锁插件
     */
    @Bean
    @Order(999)
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }

    @Bean
    @Order(999)
    public SequenceGeneratorInterceptor sequenceGeneratorInterceptor() {
        return new SequenceGeneratorInterceptor();
    }

    @Bean
    @Order(999)
    public IdGeneratorInterceptor idGeneratorInterceptor() {
        return new IdGeneratorInterceptor();
    }

    @Bean
    @Order(999)
    public IllegalSQLInterceptor illegalSQLInterceptor() {
        return new IllegalSQLInterceptor();
    }

}
