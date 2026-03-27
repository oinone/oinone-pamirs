package pro.shushi.pamirs.framework.connectors.data.elastic.rest;

import pro.shushi.pamirs.locale.utils.I18nUtils;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.util.IndexNaming;
import pro.shushi.pamirs.framework.connectors.data.elastic.rsql.ElasticRSQLHelper;
import pro.shushi.pamirs.meta.api.core.orm.convert.DataConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.condition.Order;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.condition.Sort;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.search.ElasticSearchApi;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ElasticRestSearchApi
 *
 * @author yakir on 2020/04/18 20:22.
 */
@Component
public class ElasticRestSearchApi implements ElasticSearchApi {

    private static final Logger log = LoggerFactory.getLogger(ElasticRestSearchApi.class);

    @Autowired(required = false)
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private DataConverter persistenceDataConverter;

    private static final String ID = LambdaUtil.fetchFieldName(IdModel::getId);
    private static final String CREATE_DATE = LambdaUtil.fetchFieldName(IdModel::getCreateDate);
    private static final String IS_DELETED = "isDeleted";

    @Override
    @SuppressWarnings({"rawtypes"})
    public <T> Pagination<T> search(Pagination<T> page, IWrapper<T> queryWrapper) {
        String modelModel = queryWrapper.getModel();
        if (null == modelModel || modelModel.isEmpty()) {
            return page;
        }
        ModelConfig modelCfg = PamirsSession.getContext().getModelConfig(modelModel);
        if (null == modelCfg) {
            return page;
        }
        String rsql = queryWrapper.getOriginRsql();
        if (StringUtils.isBlank(rsql)) {
            rsql = "id>0";
        }
        BoolQuery.Builder queryBuilder = ElasticRSQLHelper.parseRSQL(modelCfg, rsql);
        TermQuery isDeletedTerm = QueryBuilders.term()
                .queryName(IS_DELETED)
                .field(IS_DELETED).value(0)
                .build();
        queryBuilder.must(new Query(isDeletedTerm));
        String alias = IndexNaming.aliasByModel(modelModel);
        Query query = new Query(queryBuilder.build());
        log.info("{}", query);

        List<Order> orders = Optional.ofNullable(page.getSort()).map(Sort::getOrders).orElse(new ArrayList<>());
        int currentPage = Optional.ofNullable(page.getCurrentPage()).orElse(1);
        Long size = Optional.ofNullable(page.getSize()).orElse(10L);
        int pageSize = size.intValue();
        List<SortOptions> sortOptions = new ArrayList<>();
        if (CollectionUtils.isEmpty(orders)) {
            orders.add(new Order(SortDirectionEnum.DESC, ID));
            orders.add(new Order(SortDirectionEnum.DESC, CREATE_DATE));
        }
        for (Order order : orders) {
            sortOptions.add(new SortOptions.Builder()
                    .field(SortOptionsBuilders.field()
                            .field(order.getField())
                            .order(SortDirectionEnum.DESC.equals(order.getDirection()) ? SortOrder.Desc : SortOrder.Asc)
                            .build())
                    .build());
        }
        SearchRequest request = new SearchRequest.Builder()
                .index(alias)
                .from((currentPage - 1) * pageSize)
                .size(pageSize)
                .sort(sortOptions)
                .query(query)
                .build();
        SearchResponse<HashMap> response = null;
        try {
            log.info("ES search request parameters: {}", request.toString());
            response = elasticsearchClient.search(request, HashMap.class);
        } catch (ElasticsearchException e) {
            log.error("Index exception", e);
            PamirsSession.getMessageHub()
                    .msg(Message.init()
                            .setLevel(InformationLevelEnum.WARN)
                            .msg(I18nUtils.getMessage("pamirs-connectors-data-elastic-rest.ElasticRestSearchApi.indexException")));
            return page;
        } catch (IOException e) {
            log.error("ElasticSearch runtime status exception", e);
            PamirsSession.getMessageHub()
                    .msg(Message.init()
                            .setLevel(InformationLevelEnum.WARN)
                            .msg(I18nUtils.getMessage("pamirs-connectors-data-elastic-rest.ElasticRestSearchApi.elasticsearchIsOperatingAbnorm")));
            return page;
        }

        if (null == response || response.timedOut()) {
            return page;
        }

        HitsMetadata<HashMap> hits = response.hits();
        if (null == hits) {
            return page;
        }

        TotalHits totalHits = hits.total();
        long total = Optional.ofNullable(totalHits).map(TotalHits::value).orElse(0L);

        List<HashMap> dataMapList = Optional.of(hits)
                .map(HitsMetadata<HashMap>::hits)
                .map(List::stream)
                .orElse(Stream.empty())
                .map(Hit::source)
                .collect(Collectors.toList());

        List<T> context = persistenceDataConverter.out(modelModel, dataMapList);

        page.setSize(size);
        page.setTotalElements(total);
        page.setContent(context);
        log.info("ES search request returns total, {}", total);
        return page;
    }
}
