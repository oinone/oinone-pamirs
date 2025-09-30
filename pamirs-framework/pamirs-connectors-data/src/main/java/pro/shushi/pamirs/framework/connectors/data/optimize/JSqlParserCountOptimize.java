package pro.shushi.pamirs.framework.connectors.data.optimize;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.reflection.MetaObject;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.ISqlParser;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.*;

/**
 * JSqlParserCountOptimize
 *
 * @author yakir on 2025/04/14 17:48.
 */
@Slf4j
@Data
public class JSqlParserCountOptimize implements ISqlParser {

    private static final List<SelectItem<?>> COUNT_SELECT_ITEM = Collections.singletonList(defaultCountSelectItem());

    private boolean optimizeJoin = false;

    static {
        JsqlParserGlobal.setParserMultiFunc((sql)-> {
            String formatSql = CCJSqlParserUtil.sanitizeSingleSql(sql);
            return CCJSqlParserUtil.parseStatements(formatSql, JsqlParserGlobal.getExecutorService(), null);
        });
        JsqlParserGlobal.setParserSingleFunc((sql)-> {
            String formatSql = CCJSqlParserUtil.sanitizeSingleSql(sql);
            return CCJSqlParserUtil.parse(formatSql, JsqlParserGlobal.getExecutorService(), null);
        });
    }

    public JSqlParserCountOptimize() {
        this.optimizeJoin = true;
    }

    public JSqlParserCountOptimize(boolean optimizeJoin) {
        this.optimizeJoin = optimizeJoin;
    }

    /**
     * 获取jsqlparser中count的SelectItem
     */
    private static SelectItem<?> defaultCountSelectItem() {
        return new SelectItem<>(new Column().withColumnName("COUNT(*)")).withAlias(new Alias("total"));
    }

    @Override
    public String parser(MetaObject metaObject, String sql) {
        if (log.isDebugEnabled()) {
            log.debug("JsqlParserCountOptimize sql=" + sql);
        }
        try {
            Select selectStatement = (Select) JsqlParserGlobal.parse(sql);
            PlainSelect plainSelect = (PlainSelect) selectStatement;
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
                    return SqlParserUtils.getOriginalCountSql(selectStatement.toString());
                }
            }
            // 包含 distinct、groupBy不优化
            if (distinct != null || null != groupBy) {
                return SqlParserUtils.getOriginalCountSql(selectStatement.toString());
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
            plainSelect.setSelectItems(COUNT_SELECT_ITEM);
            return selectStatement.toString();
        } catch (Throwable e) {
            // 无法优化使用原 SQL
            return SqlParserUtils.getOriginalCountSql(sql);
        }
    }
}
