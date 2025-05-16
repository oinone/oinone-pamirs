package pro.shushi.pamirs.framework.connectors.data.elastic.rest;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ErrorCause;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperationBuilders;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import co.elastic.clients.elasticsearch.core.bulk.UpdateAction;
import co.elastic.clients.elasticsearch.core.bulk.UpdateOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.ElasticDocApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ElasticDocImpl
 *
 * @author yakir on 2020/04/16 21:36.
 */
@SuppressWarnings({"unchecked", "rawtypes", "unused"})
@Component
public class ElasticDocImpl implements ElasticDocApi {

    private static final Logger log = LoggerFactory.getLogger(ElasticDocImpl.class);

    @Autowired(required = false)
    private ElasticsearchClient elasticsearchClient;

    /**
     * batch
     * <p>
     * index / update / delete
     *
     * @param entries [{left: String/index, mid: String/id, right:  String/json serialized obj}]
     * @return 返回失败的
     */
    public List<Map<String, Object>> bulkIndex(final String index, final List<Map<String, Object>> entries) {

        if (elasticsearchClient == null || null == entries || entries.isEmpty()) {
            log.warn("Elastic Properties not enabled.");
            return null;
        }

        List<BulkOperation> operations = new ArrayList<>();
        for (Map<String, Object> triple : entries) {
            if (null == triple) {
                continue;
            }
            Object idObj = triple.get(ID);
            if (null == idObj) {
                continue;
            }
            String id = idObj.toString();
            if (null == id || id.isEmpty()) {
                continue;
            }

            IndexOperation.Builder<Map<String, Object>> iBuilder = BulkOperationBuilders.index();
            iBuilder.index(index);
            iBuilder.document(triple);
            iBuilder.id(id);

            BulkOperation opt = new BulkOperation(iBuilder.build());
            operations.add(opt);
        }

        if (CollectionUtils.isNotEmpty(operations)) {
            BulkRequest bulkRequest = new BulkRequest.Builder().operations(operations).build();
            consumeBulk(bulkRequest, BULK_INDEX);
        }

        return entries;
    }

    public List<Map<String, Object>> bulkUpdate(final String index, final List<Map<String, Object>> entries) {

        List<BulkOperation> operations = new ArrayList<>();
        for (Map<String, Object> triple : entries) {
            if (null == triple) {
                continue;
            }
            Object idObj = triple.get(ID);
            if (null == idObj) {
                continue;
            }
            String id = idObj.toString();
            if (null == id || id.isEmpty()) {
                continue;
            }

            UpdateOperation iBuilder = BulkOperationBuilders.update()
                    .action(new UpdateAction.Builder<>()
                            .upsert(triple)
                            .docAsUpsert(true)
                            .build())
                    .build();

            BulkOperation opt = new BulkOperation(iBuilder);
            operations.add(opt);
        }

        if (CollectionUtils.isNotEmpty(operations)) {
            BulkRequest bulkRequest = new BulkRequest.Builder().operations(operations).build();
            consumeBulk(bulkRequest, BULK_UPDATE);
        }

        return entries;
    }


    public List<Map<String, Object>> bulkDelete(final String index, final List<Map<String, Object>> entries) {

        List<BulkOperation> operations = new ArrayList<>();
        for (Map<String, Object> triple : Optional.ofNullable(entries).orElse(Collections.emptyList())) {
            Object idObj = triple.get(ID);
            if (null == idObj) {
                continue;
            }
            String id = idObj.toString();
            if (null == id || id.isEmpty()) {
                continue;
            }
            BulkOperation opt = new BulkOperation(BulkOperationBuilders.delete()
                    .index(index)
                    .id(id)
                    .build());
            operations.add(opt);
        }

        if (CollectionUtils.isNotEmpty(operations)) {
            BulkRequest bulkRequest = new BulkRequest.Builder()
                    .operations(operations)
                    .build();
            consumeBulk(bulkRequest, BULK_DELETE);
        }

        return entries;
    }

    /**
     * count
     * <p>
     *
     * @param index String
     * @return 数量
     */
    @Override
    public Long count(final String index) {

        try {
            CountRequest request = new CountRequest.Builder()
                    .index(index)
                    .build();
            CountResponse response = elasticsearchClient.count(request);
            return response.count();
        } catch (IOException e) {
            log.error("[{}] Count", index, e);
            return 0L;
        }
    }

    private void consumeBulk(BulkRequest bulkRequest, String opt) {

        // Pair {k: index, v: id}
        try {
            BulkResponse bulkResponse = elasticsearchClient.bulk(bulkRequest);
            //  不用 hasFailures 原因是 hasFailures 遍历了所有item 这部分可以自己来遍历，同时拿出失败的index、和id
            if (bulkResponse.errors()) {
                for (BulkResponseItem item : bulkResponse.items()) {
                    ErrorCause errorCause = item.error();
                    if (null != errorCause) {
                        log.error("数据操作失败:[{}] index:[{}] id:[{}] 失败原因: [{}]", opt, item.index(), item.id(), errorCause);
                    }
                }
            }
        } catch (IOException e) {
            // do nothing ...
        }
    }
}
