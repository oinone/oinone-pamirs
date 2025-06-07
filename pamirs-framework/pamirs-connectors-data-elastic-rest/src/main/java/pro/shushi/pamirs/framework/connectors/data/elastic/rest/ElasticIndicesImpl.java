package pro.shushi.pamirs.framework.connectors.data.elastic.rest;

import co.elastic.clients.elasticsearch._types.AcknowledgedResponse;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.elasticsearch.indices.close.CloseIndexResult;
import co.elastic.clients.elasticsearch.indices.get_alias.IndexAliases;
import co.elastic.clients.elasticsearch.indices.get_mapping.IndexMappingRecord;
import co.elastic.clients.elasticsearch.indices.update_aliases.Action;
import co.elastic.clients.elasticsearch.indices.update_aliases.AddAction;
import co.elastic.clients.elasticsearch.indices.update_aliases.RemoveAction;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.ElasticIndicesApi;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.domain.ElasticIndex;
import pro.shushi.pamirs.framework.connectors.data.elastic.rest.mapping.ElasticMappingManager;
import pro.shushi.pamirs.framework.connectors.data.elastic.utils.IndicesUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

/**
 * ElasticIndicesImpl
 *
 * @author yakir on 2020/04/16 15:38.
 */
@Component
public class ElasticIndicesImpl implements ElasticIndicesApi {

    private static final Logger log = LoggerFactory.getLogger(ElasticIndicesImpl.class);

    @Autowired(required = false)
    private ElasticsearchIndicesClient elasticsearchIndicesClient;
    @Autowired
    private ElasticMappingManager elasticMappingManager;

    @Override
    public String init(ElasticIndex elasticIndex) {

        if (elasticsearchIndicesClient == null) {
            log.warn("Elastic Properties not enabled.");
            return null;
        }

        if (StringUtils.isAnyBlank(elasticIndex.getIndex(), elasticIndex.getAlias())) {
            log.error("初始化索引参数不合法，索引[{}] 别名:[{}]", elasticIndex.getIndex(), elasticIndex.getAlias());
            return null;
        }

        Boolean isExist = isExist(elasticIndex.relIndex());
        if (null == isExist || isExist) {
            return null;
        }

        try {
            CreateIndexRequest cir = IndicesUtil.cir(elasticIndex);
            CreateIndexResponse resp = elasticsearchIndicesClient.create(cir);
            String rt = resp.index();
            log.info("初始化索引[{}]结果: [{}]", elasticIndex.getIndex(), rt);
            return rt;
        } catch (IOException e) {
            log.info("初始化索引结果失败: [{}]", elasticIndex.getIndex(), e);
            return null;
        }
    }

    @Override
    public String create(ElasticIndex elasticIndex) {

        log.info("创建索引: [{}]", elasticIndex);
        String index = elasticIndex.getIndex();

        Boolean isExist = isExist(elasticIndex.relIndex());
        if (null == isExist || isExist) {
            return null;
        }

        try {
            CreateIndexRequest cir = IndicesUtil.cir(index);
            CreateIndexResponse resp = elasticsearchIndicesClient.create(cir);
            String rt = resp.index();
            log.info("初始化索引结果: [{}]", index);
            return rt;
        } catch (IOException e) {
            log.error("初始化索引结果失败: [{}]", index, e);
            return null;
        }
    }

    @Override
    public String create(String index) {

        log.info("创建索引: [{}]", index);

        Boolean isExist = isExist(index);
        if (null == isExist || isExist) {
            return null;
        }

        CreateIndexRequest cir = IndicesUtil.cir(index);
        try {
            CreateIndexResponse resp = elasticsearchIndicesClient.create(cir);
            String rt = resp.index();
            log.info("创建索引[{}]结果: [{}]", index, rt);
            return rt;
        } catch (IOException e) {
            log.error("创建索引失败: [{}]", index, e);
            return null;
        }
    }

    @Override
    public Boolean isExist(final String index) {

        ExistsRequest existsReq = new ExistsRequest.Builder()
                .index(index)
                .build();
        try {
            BooleanResponse bool = elasticsearchIndicesClient.exists(existsReq);
            boolean rt = bool.value();
            log.warn("索引[{}]{}存在", index, rt ? "已" : "不");
            return rt;
        } catch (IOException e) {
            log.error("执行判断索引是否存在失败", e);
            return null;
        }
    }

    @Override
    public boolean close(String index) {

        CloseIndexRequest closeReq = new CloseIndexRequest.Builder()
                .index(index)
                .build();
        try {
            CloseIndexResponse closeIndexResp = elasticsearchIndicesClient.close(closeReq);
            int closedSize = (int) Optional.ofNullable(closeIndexResp)
                    .filter(CloseIndexResponse::acknowledged)
                    .map(CloseIndexResponse::indices)
                    .filter(_map -> _map.size() > 0)
                    .map(Map::values)
                    .map(Collection::stream)
                    .orElse(Stream.empty())
                    .map(CloseIndexResult::closed)
                    .count();

            boolean rt = (1 == closedSize);
            log.warn("关闭索引[{}]{}", index, rt ? "成功" : "失败");
            return rt;
        } catch (IOException e) {
            log.error("执行判断索引是否存在失败", e);
            return false;
        }
    }

    @Override
    public String get(String index) {

        String rt = Optional.ofNullable(index)
                .filter(StringUtils::isNotBlank)
                .map(_index -> new GetIndexRequest.Builder().index(_index).build())
                .map(_req -> {
                    try {
                        return elasticsearchIndicesClient.get(_req);
                    } catch (IOException e) {
                        log.error("获取索引失败", e);
                        return null;
                    }
                })
                .map(GetIndexResponse::result)
                .map(Map::values)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .findFirst()
                .map(IndexState::toString)
                .orElse(null);

        log.info("获取索引结果 [{}]", rt);

        return rt;
    }

    @Override
    public Boolean
    existAlias(String alias) {

        Boolean rt = Optional.ofNullable(alias)
                .filter(StringUtils::isNotBlank)
                .map(_alias -> new ExistsAliasRequest.Builder().name(_alias).build())
                .map(_req -> {
                    try {
                        return elasticsearchIndicesClient.existsAlias(_req);
                    } catch (IOException e) {
                        log.error("执行判断索引是否存在失败", e);
                        return null;
                    }
                })
                .map(BooleanResponse::value)
                .orElse(null);

        log.info("检查别名[{}]是否存在 结果 [{}]", alias, rt);

        return rt;
    }

    @Override
    public Boolean existAlias(String index, String alias) {

        if (StringUtils.isAnyBlank(index, alias)) {
            log.error("参数不合法 索引 [{}] 别名 [{}]", index, alias);
            return null;
        }

        ExistsAliasRequest request = new ExistsAliasRequest.Builder()
                .index(index)
                .name(alias)
                .build();
        try {
            BooleanResponse response = elasticsearchIndicesClient.existsAlias(request);
            boolean rt = response.value();
            log.info("检查索引[{}]是否存在别名[{}], 结果:[{}]", index, alias, rt);
            return rt;
        } catch (IOException e) {
            log.error("执行判断索引是否存在别名失败", e);
            return null;
        }
    }


    @Override
    public String moveAlias(String indexR, String indexA, String alias) {

        Action action0 = new Action.Builder()
                .remove(new RemoveAction.Builder().index(indexR).alias(alias).build())
                .build();
        Action action1 = new Action.Builder()
                .add(new AddAction.Builder().index(indexA).alias(alias).build())
                .build();
        UpdateAliasesRequest request = new UpdateAliasesRequest.Builder()
                .actions(Lists.newArrayList(action0, action1))
                .build();

        try {
            UpdateAliasesResponse resp = elasticsearchIndicesClient.updateAliases(request);
            log.info("移动别名:[{}] 从索引 [{}] -> [{}]", alias, indexR, indexA);
            return resp.acknowledged() ? alias : null;
        } catch (IOException e) {
            log.error("移动别名失败[{}] 从索引 [{}] -> [{}]", alias, indexR, indexA, e);
            return null;
        }
    }

    @Override
    public String createAlias(String index, String alias) {

        log.info("创建索引别名 索引 [{}], 别名 [{}]", index, alias);
        PutAliasRequest req = new PutAliasRequest.Builder()
                .name(alias)
                .index(index)
                .isWriteIndex(true)
                .build();
        try {
            PutAliasResponse resp = elasticsearchIndicesClient.putAlias(req);
            return resp.acknowledged() ? alias : null;
        } catch (IOException e) {
            log.error("添加索引别名失败 [{}] [{}]", index, alias, e);
            return null;
        }
    }

    @Override
    public String deleteAlias(String alias) {

        try {
            GetAliasRequest getAliasRequest = new GetAliasRequest.Builder()
                    .name(alias)
                    .build();

            GetAliasResponse getAliasResponse = elasticsearchIndicesClient.getAlias(getAliasRequest);
            Map<String, IndexAliases> indexAliasesMap = getAliasResponse.result();
            List<String> indexList = new ArrayList<>();
            for (Map.Entry<String, IndexAliases> entry : indexAliasesMap.entrySet()) {
                indexList.add(entry.getKey());
            }

            if (CollectionUtils.isEmpty(indexList)) {
                return alias;
            }

            log.info("删除索引别名 [{}]", alias);
            DeleteAliasRequest req = new DeleteAliasRequest.Builder()
                    .index(indexList)
                    .name(alias)
                    .build();
            AcknowledgedResponse resp = elasticsearchIndicesClient.deleteAlias(req);
            return resp.acknowledged() ? alias : null;
        } catch (IOException e) {
            log.error("删除索引别名失败 [{}]", alias, e);
            return null;
        }
    }

    @Override
    public String getMapping(String index) {
        GetMappingRequest req = new GetMappingRequest.Builder()
                .index(index)
                .build();
        try {
            GetMappingResponse resp = elasticsearchIndicesClient.getMapping(req);
            String mapping = Optional.ofNullable(resp)
                    .map(GetMappingResponse::result)
                    .map(_map -> _map.get(index))
                    .map(IndexMappingRecord::mappings)
                    .map(TypeMapping::toString)
                    .orElse(null);
            log.info("索引mapping: {}", mapping);
            return mapping;
        } catch (IOException e) {
            log.error("获取索引 [{}] mapping失败", index, e);
            return null;
        }
    }

    @Override
    public String createMapping(String index, String modelModel, List<Map<String, String>> analyzers) {

        Map<String, Property> mapping = elasticMappingManager.mapping(modelModel, analyzers);
        PutMappingRequest req = new PutMappingRequest.Builder()
                .index(index)
                .properties(mapping)
                .build();
        try {
            AcknowledgedResponse resp = elasticsearchIndicesClient.putMapping(req);
            log.info("创建索引 [{}] mapping: {}", index, mapping);
            return resp.acknowledged() ? index : null;
        } catch (IOException e) {
            log.error("创建索引 [{}] mapping失败 mapping {}", index, mapping, e);
            return null;
        }
    }
}
