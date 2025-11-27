package pro.shushi.pamirs.ux.common.utils;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.faas.hook.builtin.PlaceHolderHook;
import pro.shushi.pamirs.framework.gateways.rsql.PamirsRsqlVisitor;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlQuery;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlSearchOperation;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.faas.hook.PlaceHolderParser;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 查询帮助类
 *
 * @author Adamancy Zhang at 09:57 on 2025-11-27
 */
public class QueryHelper {

    private QueryHelper() {
        // reject create object
    }

    public static <T> void queryDataListByQueryPage(String model, QueryWrapper<T> queryWrapper, Consumer<List<T>> consumer) {
        queryDataListByQueryPage(model, queryWrapper, 2000, consumer);
    }

    public static <T> void queryDataListByQueryPage(String model, QueryWrapper<T> queryWrapper, int pageSize, Consumer<List<T>> consumer) {
        Pagination<T> pagination = new Pagination<>(1, pageSize);
        pagination.setModel(model);
        Pagination<T> firstPage = Models.origin().queryPage(pagination, queryWrapper);
        List<T> content = firstPage.getContent();
        if (CollectionUtils.isEmpty(content)) {
            return;
        }
        consumer.accept(content);
        if (content.size() < pageSize) {
            return;
        }
        int totalPage = firstPage.getTotalPages();
        for (int currentPage = 2; currentPage <= totalPage; currentPage++) {
            pagination.setCurrentPage(currentPage);
            Pagination<T> nextPage = Models.origin().queryPage(pagination, queryWrapper);
            content = nextPage.getContent();
            consumer.accept(content);
        }
    }

    public static String replacePlaceholder(String rsql) {
        Map<String, PlaceHolderParser> placeHolderParserMap = PlaceHolderHook.getPlaceHolderParserMap();
        IWrapper<?> wrapper = Pops.query().setRsql(rsql);
        for (String placeHolderParser : placeHolderParserMap.keySet()) {
            placeHolderParserMap.get(placeHolderParser).parse(wrapper);
        }
        return wrapper.getRsql();
    }

    public static String rsqlToSql(String model, String rsql) {
        rsql = replacePlaceholder(rsql);
        Node parse = new RSQLParser(RsqlSearchOperation.getOperators()).parse(rsql);
        RsqlQuery query = parse.accept(new PamirsRsqlVisitor(), PamirsSession.getContext().getSimpleModelConfig(model));
        return query.getWhere().toString();
    }
}
