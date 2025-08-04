package pro.shushi.pamirs.framework.connectors.data.dialect;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.ibatis.reflection.MetaObject;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.ISqlParser;
import pro.shushi.pamirs.framework.connectors.data.optimize.JSqlParserCountOptimize;
import pro.shushi.pamirs.framework.connectors.data.util.CountSQLParserUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 分页CountSQL优化解析
 *
 * @author Adamancy Zhang at 12:42 on 2024-11-05
 * @see JSqlParserCountOptimize
 */
@Slf4j
public class DefaultCountSQLParser implements ISqlParser {

    private final List<SelectItem<?>> countSelectItem;

    private boolean optimizeJoin = false;

    public DefaultCountSQLParser() {
        this.countSelectItem = Collections.singletonList(defaultCountSelectItem());
    }

    public void setOptimizeJoin(boolean optimizeJoin) {
        this.optimizeJoin = optimizeJoin;
    }

    /**
     * 获取jsqlparser中count的SelectItem
     */
    protected SelectItem<?> defaultCountSelectItem() {
        return new SelectItem<>(new Column().withColumnName("COUNT(*)")).withAlias(new Alias("total"));
    }

    protected String getOriginalCountSql(String sql) {
        return CountSQLParserUtils.INSTANCE.getOriginalCountSql(sql);
    }

    @Override
    public String parser(MetaObject metaObject, String sql) {
        if (log.isDebugEnabled()) {
            log.debug("DefaultCountSQLParser sql: {}", sql);
        }
        try {
            Select selectStatement = (Select) CCJSqlParserUtil.parse(sql);
            PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
            Distinct distinct = plainSelect.getDistinct();
            GroupByElement groupBy = plainSelect.getGroupBy();
            List<OrderByElement> orderBy = plainSelect.getOrderByElements();

            // 添加包含groupBy 不去除orderBy
            if (null == groupBy && CollectionUtils.isNotEmpty(orderBy)) {
                plainSelect.setOrderByElements(null);
            }
            //#95 Github, selectItems contains #{} ${}, which will be translated to ?, and it may be in a function: power(#{myInt},2)
            for (SelectItem<?> item : plainSelect.getSelectItems()) {
                if (item.toString().contains(StringPool.QUESTION_MARK)) {
                    return getOriginalCountSql(selectStatement.toString());
                }
            }
            // 包含 distinct、groupBy不优化
            if (distinct != null || null != groupBy) {
                return getOriginalCountSql(selectStatement.toString());
            }
            // 包含 join 连表,进行判断是否移除 join 连表
            List<Join> joins = plainSelect.getJoins();
            if (optimizeJoin && CollectionUtils.isNotEmpty(joins)) {
                boolean canRemoveJoin = true;
                String whereS = Optional.ofNullable(plainSelect.getWhere()).map(Expression::toString).orElse(StringPool.EMPTY);
                // 不区分大小写
                whereS = whereS.toLowerCase();
                for (Join join : joins) {
                    if (!join.isLeft()) {
                        canRemoveJoin = false;
                        break;
                    }
                    Table table = (Table) join.getRightItem();
                    String str = Optional.ofNullable(table.getAlias()).map(Alias::getName).orElse(table.getName()) + StringPool.DOT;
                    // 不区分大小写
                    str = str.toLowerCase();
                    String onExpressionS = join.getOnExpression().toString();
                    /* 如果 join 里包含 ?(代表有入参) 或者 where 条件里包含使用 join 的表的字段作条件,就不移除 join */
                    if (onExpressionS.contains(StringPool.QUESTION_MARK) || whereS.contains(str)) {
                        canRemoveJoin = false;
                        break;
                    }
                }
                if (canRemoveJoin) {
                    plainSelect.setJoins(null);
                }
            }
            // 优化 SQL
            plainSelect.setSelectItems(countSelectItem);
            return selectStatement.toString();
        } catch (Throwable e) {
            // 无法优化使用原 SQL
            return getOriginalCountSql(sql);
        }
    }
}
