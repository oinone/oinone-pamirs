package pro.shushi.pamirs.channel.core.action;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.channel.core.manager.EnhanceModelScanner;
import pro.shushi.pamirs.channel.core.manager.ProcessorManager;
import pro.shushi.pamirs.channel.enmu.DumpStateEnum;
import pro.shushi.pamirs.channel.enmu.IncrementEnum;
import pro.shushi.pamirs.channel.model.ChannelModel;
import pro.shushi.pamirs.channel.model.ElasticIndexLog;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.ElasticIndicesApi;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.domain.ElasticIndex;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.util.IndexNaming;
import pro.shushi.pamirs.framework.connectors.data.elastic.rest.mapping.ElasticMappingManager;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.middleware.canal.entity.RefreshFilterEntity;
import pro.shushi.pamirs.middleware.canal.service.CanalService;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.yaml.snakeyaml.nodes.NodeId.mapping;
import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.CHANNEL_CREATE_INDEX_ERROR;
import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.CHANNEL_CREATE_MAPPING_ERROR;
import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.CHANNEL_ENHANCE_MODEL_INCREMENT_CLOSE_ERROR;
import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.CHANNEL_ENHANCE_MODEL_INCREMENT_CLOSE_FAIL_ERROR;
import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.CHANNEL_ENHANCE_MODEL_INCREMENT_OPEN_ERROR;
import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.CHANNEL_ENHANCE_MODEL_INCREMENT_OPEN_FAIL_ERROR;
import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.CHANNEL_INCREMENT_CLOSE_ILLEGAL_ARG_ERROR;
import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.CHANNEL_INCREMENT_OPEN_ILLEGAL_ARG_ERROR;
import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.CHANNEL_PREPARE_ERROR;
import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.CHANNEL_PREPARE_ERROR_0;
import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_ESCAPE_DOT;

/**
 * ChannelModelActions
 *
 * @author yakir on 2020/04/18 20:26.
 */
@Component
@Slf4j
@Fun(value = ChannelModel.MODEL_MODEL)
public class ChannelModelActions {

    @Autowired
    private ElasticIndicesApi     elasticIndicesApi;
    @Autowired
    private ProcessorManager      processorManager;
    @Autowired
    private ElasticMappingManager elasticMappingManager;
    @Autowired
    private EnhanceModelScanner   enhanceModelScanner;
    @Autowired(required = false)
    private CanalService          canalService;

    @Action(displayName = "全量同步", summary = "全量同步")
    @Transactional
    public ChannelModel dump(ChannelModel channelModel) {

        Long id = channelModel.getId();
        if (null == id) {
            throw PamirsException.construct(CHANNEL_PREPARE_ERROR)
                    .errThrow();
        }

        ChannelModel fromDB = new ChannelModel().queryById(id);
        if (null == fromDB) {
            throw PamirsException.construct(CHANNEL_PREPARE_ERROR_0)
                    .errThrow();
        }

//        log.info("同步配置模型: {}", fromDB);

        int           pos       = Optional.ofNullable(fromDB.getPos()).map(_i -> _i + 1).orElse(1);
        String        model     = fromDB.getModel();
        String        origin    = fromDB.getOrigin();
        String        index     = fromDB.getIndex();
        String        alias     = fromDB.getAlias();
        String        replicas  = fromDB.getReplicas();
        String        shards    = fromDB.getShards();
        Boolean       reAlias   = fromDB.getReAlias();
        DumpStateEnum dumpState = fromDB.getDumpState();

        List<Map<String, String>> anzList = fromDB.getAnalyzers();
//        String        tenant    = PamirsTenantSession.getTenant();
//        String             module      = fromDB.getModule();
//        ModelDefinition    modelDefine = fromDB.getModelDefine();

//        if (StringUtils.isBlank(tenant)) {
//            log.error("[{}]", CHANNEL_TENANT_ERROR.msg());
//            throw PamirsException.construct(CHANNEL_TENANT_ERROR)
//                    .errThrow();
//        }

        String  indexName = null;
        Boolean exist     = false;
        do {
            indexName = IndexNaming.index(index, pos);
            exist     = elasticIndicesApi.isExist(indexName);
            if (exist) {
                pos = pos + 1;
            }
        } while (exist);

        fromDB.setNaming(indexName);
        fromDB.setPos(pos);

        ElasticIndex elasticIndex = new ElasticIndex();
        elasticIndex.setIndex(indexName);
        elasticIndex.setPos(pos);
        elasticIndex.setReplicas(replicas);
        elasticIndex.setShards(shards);

        ElasticIndexLog elasticIndexLog = processorManager.readPrepare(channelModel);
        elasticIndexLog.setIndex(elasticIndex.getIndex());
        elasticIndexLog.setPos(elasticIndex.getPos());
        elasticIndexLog.setReplicas(elasticIndex.getReplicas());
        elasticIndexLog.setShards(elasticIndex.getShards());
        elasticIndexLog.create();

        String createdIndex = elasticIndicesApi.create(elasticIndex);
        if (StringUtils.equalsIgnoreCase(indexName, createdIndex)) {
            log.info("索引创建成功 [{}]", createdIndex);
        } else {
            log.error("创建索引失败");
            throw PamirsException.construct(CHANNEL_CREATE_INDEX_ERROR).errThrow();
        }

        // fromDB.getModel() ?
        String mappedIndex = elasticIndicesApi.createMapping(indexName, model, anzList);
        if (null == mappedIndex) {
            throw PamirsException.construct(CHANNEL_CREATE_MAPPING_ERROR)
                    .errThrow();
        }

        if (StringUtils.equalsIgnoreCase(mappedIndex, indexName)) {
            log.info("创建Mapping成功 [{}], [{}]", indexName, mapping);
        }

        log.warn("开始全量Dump OriginModel:[{}] EnhanceModel:[{}] Naming:[{}] IndexName: [{}] AliasName:[{}]",
                origin, model, index, indexName, alias);

        Long dumpedCount = processorManager.isOk().readBulk(fromDB, elasticIndexLog);

        log.info("dump完成 dumpedCount [{}]", dumpedCount);

        if (reAlias) {
            log.info("dump完成重新 alias别名 [{}]", reAlias);
            if (elasticIndicesApi.existAlias(alias)) {
                log.info("索引别名不存在 创建索引别名 [{}] [{}]", indexName, alias);
                String deleteAlias  = elasticIndicesApi.deleteAlias(alias);
                String createdAlias = elasticIndicesApi.createAlias(indexName, alias);
                if (StringUtils.equalsIgnoreCase(createdAlias, deleteAlias)) {
                    log.info("创建索引别名成功 [{}] [{}]", indexName, deleteAlias);
                }
            } else {
                log.info("索引别名不存在 创建索引别名 [{}] [{}]", indexName, alias);
                String createdAlias = elasticIndicesApi.createAlias(indexName, alias);
                if (StringUtils.equalsIgnoreCase(createdAlias, alias)) {
                    log.info("创建索引别名成功 [{}] [{}]", indexName, alias);
                }
            }
        } else if (dumpState == DumpStateEnum.INIT) {
            log.info("索引初始化 创建索引别名 [{}] [{}]", indexName, alias);
            String createdAlias = elasticIndicesApi.createAlias(indexName, alias);
            if (StringUtils.equalsIgnoreCase(createdAlias, alias)) {
                log.info("创建索引别名成功 [{}] [{}]", indexName, alias);
            }
        }

        fromDB.setLastSync(new Date());
        fromDB.setDumpState(DumpStateEnum.SUCCESS);
        fromDB.updateById();

        return fromDB;
    }

    @Action(displayName = "开启增量", summary = "开启增量")
    @Action.Advanced(invisible = "activeRecord.increment != 'CLOSE'")
    public ChannelModel openIncrement(ChannelModel channelModel) {

        Optional.ofNullable(channelModel)
                .filter(_channel -> null != _channel.getId())
                .map(ChannelModel::getId)
                .filter(_id -> 0L != _id)
                .orElseThrow(() -> PamirsException.construct(CHANNEL_INCREMENT_OPEN_ILLEGAL_ARG_ERROR).errThrow());

        ChannelModel fromDB = channelModel.queryById();

        if (IncrementEnum.OPEN.equals(fromDB.getIncrement())) {
            log.warn("[{}] {}", fromDB.getNaming(), CHANNEL_ENHANCE_MODEL_INCREMENT_OPEN_ERROR.msg());
            throw PamirsException.construct(CHANNEL_ENHANCE_MODEL_INCREMENT_OPEN_ERROR).errThrow();
        }

        ChannelModel willUpdate = new ChannelModel();

        if (PamirsSession.getContext().getModuleCache().keySet().contains("sql_record")) {
            RefreshFilterEntity filterEntity = new RefreshFilterEntity();
            filterEntity.setDestination("pamirsBinlog");
            ModelDefinition modelDefinition = PamirsSession.getContext().getModelConfig(channelModel.getOrigin()).getModelDefinition();
            filterEntity.setNewFilter(modelDefinition.getDsKey() + SEPARATOR_ESCAPE_DOT + modelDefinition.getTable());
            if (null != canalService) {
                canalService.appendFilter(filterEntity);
            }
        }

        if (DumpStateEnum.SUCCESS.equals(fromDB.getDumpState()) && IncrementEnum.CLOSE.equals(fromDB.getIncrement())) {
            willUpdate.setId(fromDB.getId());
            willUpdate.setIncrement(IncrementEnum.OPEN);
            willUpdate.updateById();
            log.info("开启增量同步成功");
            return willUpdate.queryById();
        } else {
            throw PamirsException.construct(CHANNEL_ENHANCE_MODEL_INCREMENT_OPEN_FAIL_ERROR).errThrow();
        }
    }

    @Action(displayName = "关闭增量", summary = "关闭增量")
    @Action.Advanced(invisible = "activeRecord.increment != 'OPEN'")
    public ChannelModel closeIncrement(ChannelModel channelModel) {

        Optional.ofNullable(channelModel)
                .filter(_channel -> null != _channel.getId())
                .map(ChannelModel::getId)
                .filter(_id -> 0L != _id)
                .orElseThrow(() -> PamirsException.construct(CHANNEL_INCREMENT_CLOSE_ILLEGAL_ARG_ERROR).errThrow());

        ChannelModel fromDB = channelModel.queryById();

        if (IncrementEnum.CLOSE.equals(fromDB.getIncrement())) {
            log.warn("[{}] {}", fromDB.getNaming(), CHANNEL_ENHANCE_MODEL_INCREMENT_CLOSE_ERROR.msg());
            throw PamirsException.construct(CHANNEL_ENHANCE_MODEL_INCREMENT_CLOSE_ERROR).errThrow();
        }

        ChannelModel willUpdate = new ChannelModel();
        if (DumpStateEnum.SUCCESS.equals(fromDB.getDumpState()) && IncrementEnum.OPEN.equals(fromDB.getIncrement())) {
            willUpdate.setId(fromDB.getId());
            willUpdate.setIncrement(IncrementEnum.CLOSE);
            willUpdate.updateById();
            log.info("关闭增量同步成功");
            return willUpdate.queryById();
        } else {
            throw PamirsException.construct(CHANNEL_ENHANCE_MODEL_INCREMENT_CLOSE_FAIL_ERROR).errThrow();
        }
    }
}
