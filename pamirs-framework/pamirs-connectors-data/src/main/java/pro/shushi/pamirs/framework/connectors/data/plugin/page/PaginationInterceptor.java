package pro.shushi.pamirs.framework.connectors.data.plugin.page;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import com.baomidou.mybatisplus.extension.toolkit.PropertyMapper;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SetOperationList;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DsDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.ISqlParser;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.SQLExecuteDialectService;
import pro.shushi.pamirs.framework.connectors.data.mapper.context.MapperContext;
import pro.shushi.pamirs.framework.connectors.data.optimize.AbstractSqlParserHandler;
import pro.shushi.pamirs.framework.connectors.data.plugin.MybatisParameterHandler;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.framework.connectors.data.sql.config.ModelFieldConfigWrapper;
import pro.shushi.pamirs.framework.connectors.data.util.DataConfigurationHelper;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.condition.Order;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/13 3:29 下午
 */
@Slf4j
@Data
@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
)})
public class PaginationInterceptor extends AbstractSqlParserHandler implements Interceptor {

    /**
     * COUNT SQL 解析
     */
    protected ISqlParser countSqlParser;

    /**
     * 溢出总页数后是否进行处理
     */
    protected boolean overflow = false;
    /**
     * 单页限制 5000 条，小于 0 如 -1 不受限制
     */
    protected long limit = Pagination.defaultSize;
    /**
     * 数据库类型
     *
     * @since 3.3.1
     */
    private DbType dbType;
    /**
     * 方言实现类
     *
     * @since 3.3.1
     */
    private IDialect dialect;
    /**
     * 生成 countSql 优化掉 join
     * 现在只支持 left join
     *
     * @since 3.4.2
     */
    protected boolean optimizeJoin = true;

    /**
     * Physical Page Interceptor for all the queries with parameter {@link RowBounds}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

        // SQL 解析
        this.sqlParser(metaObject);

        // 先判断是不是SELECT操作  (2019-04-10 00:37:31 跳过存储过程)
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (SqlCommandType.SELECT != mappedStatement.getSqlCommandType()
                || StatementType.CALLABLE == mappedStatement.getStatementType()) {
            return invocation.proceed();
        }

        // 针对定义了rowBounds，做为mapper接口方法的参数
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Object paramObj = boundSql.getParameterObject();

        // 先查找是否存在低代码page对象
        Pagination<?> pagination = findLowCodePage(paramObj).orElse(null);
        if (null != pagination) {
            String modelModel = findModel(paramObj).orElse(null);
            if (null == modelModel) {
                return invocation.proceed();
            }

            String originalSql = boundSql.getSql();

            /*
             * 不需要分页的场合，如果 size 小于 0 返回结果集
             */
            if (pagination.getSize() < 0) {
                metaObject.setValue("delegate.boundSql.sql", concatOrderBy(modelModel, originalSql, pagination));
                return invocation.proceed();
            }

            if (this.limit > 0 && this.limit < pagination.getSize()) {
                //处理单页条数限制
                handlerLimit(pagination);
            }

            Connection connection = (Connection) invocation.getArgs()[0];

            if (pagination.isSearchCount() && !pagination.isHitCount()) {
                String countSQL = Dialects.component(SQLExecuteDialectService.class, DataConfigurationHelper.getDsKey()).countSQL(pagination.isOptimizeCountSql(), originalSql, metaObject);
                this.queryTotal(countSQL, mappedStatement, boundSql, pagination, connection);
                if (pagination.getTotalElements() <= 0) {
                    return invocation.proceed();
                }
            }

            IDialect dialect = getDsDialect(connection);
            if (dialect == null) {
                return invocation.proceed();
            }

            String buildSql = concatOrderBy(modelModel, originalSql, pagination);
            DialectModel model = dialect.buildPaginationSql(buildSql, pagination.getStart(), pagination.getSize());
            Configuration configuration = mappedStatement.getConfiguration();
            List<ParameterMapping> mappings = new ArrayList<>(boundSql.getParameterMappings());
            Map<String, Object> additionalParameters = (Map<String, Object>) metaObject.getValue("delegate.boundSql.additionalParameters");
            model.consumers(mappings, configuration, additionalParameters);
            metaObject.setValue("delegate.boundSql.sql", model.getDialectSql());
            metaObject.setValue("delegate.boundSql.parameterMappings", mappings);
        } else {
            // 判断参数里是否有page对象
            IPage<?> page = ParameterUtils.findPage(paramObj).orElse(null);

            if (null == page) {
                return invocation.proceed();
            }

            String originalSql = boundSql.getSql();

            /*
             * 不需要分页的场合，如果 size 小于 0 返回结果集
             */
            if (page.getSize() < 0) {
                metaObject.setValue("delegate.boundSql.sql", concatOrderBy(originalSql, page));
                return invocation.proceed();
            }

            if (this.limit > 0 && this.limit < page.getSize()) {
                //处理单页条数限制
                handlerLimit(page);
            }

            Connection connection = (Connection) invocation.getArgs()[0];

            if (page.searchCount()/* && !page.isHitCount()*/) {
                String countSQL = Dialects.component(SQLExecuteDialectService.class, DataConfigurationHelper.getDsKey()).countSQL(page.optimizeCountSql(), originalSql, metaObject);
                this.queryTotal(countSQL, mappedStatement, boundSql, page, connection);
                if (page.getTotal() <= 0) {
                    return invocation.proceed();
                }
            }

            IDialect dialect = getDsDialect(connection);
            if (dialect == null) {
                return invocation.proceed();
            }

            String buildSql = concatOrderBy(originalSql, page);
            DialectModel model = dialect.buildPaginationSql(buildSql, page.offset(), page.getSize());
            Configuration configuration = mappedStatement.getConfiguration();
            List<ParameterMapping> mappings = new ArrayList<>(boundSql.getParameterMappings());
            Map<String, Object> additionalParameters = (Map<String, Object>) metaObject.getValue("delegate.boundSql.additionalParameters");
            model.consumers(mappings, configuration, additionalParameters);
            metaObject.setValue("delegate.boundSql.sql", model.getDialectSql());
            metaObject.setValue("delegate.boundSql.parameterMappings", mappings);
        }
        return invocation.proceed();
    }

    private IDialect getDsDialect(Connection connection) throws SQLException {
        DbType dbType = this.dbType;
        if (dbType == null) {
            String dsKey = DataConfigurationHelper.getDsKey();
            dbType = Dialects.component(DsDialectComponent.class, dsKey).getDbType(dsKey, connection);
        }
        IDialect dialect = this.dialect;
        if (dialect == null) {
            dialect = DialectFactory.getDialect(dbType);
        }
        return dialect;
    }

    /**
     * 查询SQL拼接Order By
     *
     * @param originalSql 需要拼接的SQL
     * @param page        page对象
     * @return ignore
     */
    public String concatOrderBy(String originalSql, IPage<?> page) {
        if (CollectionUtils.isNotEmpty(page.orders())) {
            try {
                List<OrderItem> orderList = page.orders();
                Select selectStatement = (Select) JsqlParserGlobal.parse(originalSql);
                if (selectStatement instanceof PlainSelect plainSelect) {
                    List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
                    List<OrderByElement> orderByElementsReturn = addOrderByElements(orderList, orderByElements);
                    plainSelect.setOrderByElements(orderByElementsReturn);
                    return plainSelect.toString();
                } else if (selectStatement instanceof SetOperationList setOperationList) {
                    List<OrderByElement> orderByElements = setOperationList.getOrderByElements();
                    List<OrderByElement> orderByElementsReturn = addOrderByElements(orderList, orderByElements);
                    setOperationList.setOrderByElements(orderByElementsReturn);
                    return setOperationList.toString();
                } else {
                    return originalSql;
                }

            } catch (JSQLParserException e) {
                log.warn("failed to concat orderBy from IPage, sql={}, exception={}", originalSql, e.getMessage(), e);
            }
        }
        return originalSql;
    }

    /**
     * 查询SQL拼接Order By
     *
     * @param model       模型编码
     * @param originalSql 需要拼接的SQL
     * @param page        page对象
     * @return ignore
     */
    public String concatOrderBy(String model, String originalSql, Pagination<?> page) {
        if (CollectionUtils.isNotEmpty(page.orders())) {
            try {
                List<Order> orderList = page.orders();
                Select selectStatement = (Select) JsqlParserGlobal.parse(originalSql);
                if (selectStatement instanceof PlainSelect plainSelect) {
                    List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
                    List<OrderByElement> orderByElementsReturn = addLowCodeOrderByElements(model, orderList, orderByElements);
                    plainSelect.setOrderByElements(orderByElementsReturn);
                    return plainSelect.toString();
                } else if (selectStatement instanceof SetOperationList setOperationList) {
                    List<OrderByElement> orderByElements = setOperationList.getOrderByElements();
                    List<OrderByElement> orderByElementsReturn = addLowCodeOrderByElements(model, orderList, orderByElements);
                    setOperationList.setOrderByElements(orderByElementsReturn);
                    return setOperationList.toString();
                } else {
                    return originalSql;
                }

            } catch (JSQLParserException e) {
                log.warn("failed to concat orderBy from IPage, sql={}, exception={}", originalSql, e.getMessage(), e);
            }
        }
        return originalSql;
    }

    private static List<OrderByElement> addOrderByElements(List<OrderItem> orderList, List<OrderByElement> orderByElements) {
        orderByElements = CollectionUtils.isEmpty(orderByElements) ? new ArrayList<>(orderList.size()) : orderByElements;
        List<OrderByElement> orderByElementList = orderList.stream()
                .filter(item -> StringUtils.isNotBlank(item.getColumn()))
                .map(item -> {
                    OrderByElement element = new OrderByElement();
                    element.setExpression(new Column(item.getColumn()));
                    element.setAsc(item.isAsc());
                    element.setAscDescPresent(true);
                    return element;
                })
                .collect(Collectors.toList());
        orderByElements.addAll(orderByElementList);
        return orderByElements;
    }

    private static List<OrderByElement> addLowCodeOrderByElements(String model, List<Order> orderList, List<OrderByElement> orderByElements) {
        orderByElements = CollectionUtils.isEmpty(orderByElements) ? new ArrayList<>(orderList.size()) : orderByElements;
        List<OrderByElement> orderByElementList = orderList.stream()
                .filter(item -> StringUtils.isNotBlank(getLowCodeColumnFromField(model, item.getField())))
                .map(item -> {
                    OrderByElement element = new OrderByElement();
                    element.setExpression(new Column(getLowCodeColumnFromField(model, item.getField())));
                    element.setAsc(item.getDirection().equals(SortDirectionEnum.ASC));
                    element.setAscDescPresent(true);
                    return element;
                })
                .collect(Collectors.toList());
        orderByElements.addAll(orderByElementList);
        return orderByElements;
    }

    private static String getLowCodeColumnFromField(String model, String field) {
        return Optional.ofNullable(PamirsSession.getContext())
                .map(v -> v.getModelField(model, field))
                .map(Configs::wrap)
                .map(ModelFieldConfigWrapper::getColumn).orElse(null);
    }

    /**
     * 处理超出分页条数限制,默认归为限制数
     *
     * @param page IPage
     */
    protected void handlerLimit(IPage<?> page) {
        if (log.isWarnEnabled()) {
            log.warn("Page sizes greater than {} may have an impact on performance. size: {}", this.limit, page.getSize());
        }
    }

    /**
     * 处理超出分页条数限制,默认归为限制数
     *
     * @param page IPage
     */
    protected void handlerLimit(Pagination<?> page) {
        if (log.isWarnEnabled()) {
            log.warn("Page sizes greater than {} may have an impact on performance. size: {}", this.limit, page.getSize());
        }
    }

    /**
     * 查询总记录条数
     *
     * @param sql             count sql
     * @param mappedStatement MappedStatement
     * @param boundSql        BoundSql
     * @param page            IPage
     * @param connection      Connection
     */
    protected void queryTotal(String sql, MappedStatement mappedStatement, BoundSql boundSql, IPage<?> page, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            MybatisParameterHandler parameterHandler = new MybatisParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            long total = 0;
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    total = resultSet.getLong(1);
                }
            }
            page.setTotal(total);
            if (this.overflow && page.getCurrent() > page.getPages()) {
                //溢出总页数处理
                handlerOverflow(page);
            }
        } catch (SQLException e) {
            throw ExceptionUtils.mpe("Error: Method queryTotal execution error of sql by SQLException\nSQLState: %s\nErrorCode: %s\nSQL: %s\nBoundSQL: %s\n", e, e.getSQLState(), e.getErrorCode(), sql, boundSql.getSql());
        } catch (Exception e) {
            throw ExceptionUtils.mpe("Error: Method queryTotal execution error of sql\nSQL: %s\nBoundSQL: %s\n", e, sql, boundSql.getSql());
        }
    }

    /**
     * 查询低代码总记录条数
     *
     * @param sql             count sql
     * @param mappedStatement MappedStatement
     * @param boundSql        BoundSql
     * @param page            page
     * @param connection      Connection
     */
    protected void queryTotal(String sql, MappedStatement mappedStatement, BoundSql boundSql, Pagination<?> page, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            MybatisParameterHandler parameterHandler = new MybatisParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            long total = 0;
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    total = resultSet.getLong(1);
                }
            }
            page.setTotalElements(total);
            if (this.overflow && page.getCurrentPage() > page.getTotalPages()) {
                //溢出总页数处理
                handlerOverflow(page);
            }
        } catch (SQLException e) {
            throw ExceptionUtils.mpe("Error: Method queryTotal execution error of sql by SQLException\nSQLState: %s\nErrorCode: %s\nSQL: %s\nBoundSQL: %s\n", e, e.getSQLState(), e.getErrorCode(), sql, boundSql.getSql());
        } catch (Exception e) {
            throw ExceptionUtils.mpe("Error: Method queryTotal execution error of sql\nSQL: %s\nBoundSQL: %s\n", e, sql, boundSql.getSql());
        }
    }

    /**
     * 处理页数溢出,默认设置为第一页
     *
     * @param page IPage
     */
    protected void handlerOverflow(IPage<?> page) {
        page.setCurrent(1);
    }

    /**
     * 处理页数溢出,默认设置为第一页
     *
     * @param page IPage
     */
    protected void handlerOverflow(Pagination<?> page) {
        page.setCurrentPage(1);
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        PropertyMapper.newInstance(properties)
                .whenNotBlank("overflow", Boolean::parseBoolean, this::setOverflow)
                .whenNotBlank("dbType", DbType::getDbType, this::setDbType)
                .whenNotBlank("dialect", ClassUtils::newInstance, this::setDialect)
                .whenNotBlank("limit", Long::parseLong, this::setLimit)
                .whenNotBlank("optimizeJoin", Boolean::parseBoolean, this::setOptimizeJoin);

    }

    /**
     * 查找低代码分页参数
     *
     * @param parameterObject 参数对象
     * @return 分页参数
     */
    public static Optional<Pagination<?>> findLowCodePage(Object parameterObject) {
        if (parameterObject != null) {
            if (parameterObject instanceof Map<?, ?> parameterMap) {
                for (Map.Entry<?, ?> entry : parameterMap.entrySet()) {
                    if (entry.getValue() != null && entry.getValue() instanceof Pagination) {
                        return Optional.of((Pagination<?>) entry.getValue());
                    }
                }
            } else if (parameterObject instanceof Pagination) {
                return Optional.of((Pagination<?>) parameterObject);
            }
        }
        return Optional.empty();
    }

    public static Optional<String> findModel(Object parameterObject) {
        if (parameterObject != null) {
            if (parameterObject instanceof Map<?, ?> parameterMap) {
                return Optional.ofNullable(MapperContext.model(parameterMap));
            } else if (parameterObject instanceof Pagination) {
                return Optional.of((Pagination<?>) parameterObject).map(Pagination::getModel);
            }
        }
        return Optional.empty();
    }

}
