package pro.shushi.pamirs.channel.core.manager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.channel.core.config.ChannelConfig;
import pro.shushi.pamirs.channel.model.ChannelModel;
import pro.shushi.pamirs.channel.model.ElasticIndexLog;
import pro.shushi.pamirs.framework.connectors.data.api.orm.BatchSizeHintApi;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.ShardingDefineConfiguration;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.ElasticDocApi;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.ModelDirectiveApi;
import pro.shushi.pamirs.meta.api.dto.condition.*;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;

import java.util.*;
import java.util.concurrent.*;

import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.CHANNEL_DUMP_MANAGER_ERROR;
import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.SYSTEM_ERROR;
import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_ASTERISK;

/**
 * ProcessManager
 *
 * @author yakir on 2020/04/23 13:48.
 */
@Slf4j
@Component
public class ProcessorManager extends AbstractProcessor {

    private static final String MINID = "minId";
    private static final String MAXID = "maxId";
    private static final String COUNTID = "countId";
    private static final String ID = "id";
    private static final String AS = "as";
    private static final String SPACE = " ";
    private static final String COMMA = ",";

    @Autowired
    private ElasticDocApi elasticDocApi;
    @Autowired(required = false)
    private GenericMapper genericMapper;
    @Autowired
    private ElasticDataConverter elasticDataConverter;
    @Autowired
    private ChannelConfig channelConfig;
    @Autowired
    private ShardingDefineConfiguration shardingDefineConfiguration;

    public ProcessorManager isOk() {
        if (null != elasticDocApi) {
            log.info("Bulk isOK!");
        } else {
            throw PamirsException.construct(CHANNEL_DUMP_MANAGER_ERROR)
                    .errThrow();
        }
        return this;
    }

    public ElasticIndexLog readPrepare(ChannelModel channelModel) {

        String model = channelModel.getModel();
        final String originModel = channelModel.getOrigin();

        if (StringUtils.isAnyBlank(model, originModel)) {
            throw PamirsException.construct(SYSTEM_ERROR)
                    .errThrow();
        }

        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        ModelConfig originModelConfig = PamirsSession.getContext().getModelConfig(originModel);

        if (null == modelConfig || null == originModelConfig) {
            throw PamirsException.construct(SYSTEM_ERROR)
                    .errThrow();
        }

        String sql = "min(id)".concat(SPACE).concat(AS).concat(SPACE).concat(MINID).concat(COMMA).concat(SPACE)
                .concat("max(id)").concat(SPACE).concat(AS).concat(SPACE).concat(MAXID).concat(COMMA).concat(SPACE)
                .concat("count(1)").concat(SPACE).concat(AS).concat(SPACE).concat(COUNTID).concat(SPACE);

        QueryWrapper<DataMap> wrapper = new QueryWrapper<>();
        wrapper.from(originModel);
        wrapper.select(sql);
        List<DataMap> res = genericMapper.selectList(wrapper);

        if (null == res || res.isEmpty()) {
            throw PamirsException.construct(SYSTEM_ERROR)
                    .errThrow();
        }

        DataMap aggMap = res.get(0);
        Long minId = Optional.ofNullable(aggMap.get(MINID)).map(Object::toString).map(Long::valueOf).orElse(0L);
        Long maxId = Optional.ofNullable(aggMap.get(MAXID)).map(Object::toString).map(Long::valueOf).orElse(0L);
        Long countId = Optional.ofNullable(aggMap.get(COUNTID)).map(Object::toString).map(Long::valueOf).orElse(0L);

        log.info("Channel Dump Prepare minId:[{}] maxId:[{}] countId:[{}]", minId, maxId, countId);

        return new ElasticIndexLog().setIdMax(maxId).setIdMin(minId).setIdCount(countId);
    }

    public Long readBulk(ChannelModel channelModel, ElasticIndexLog elasticIndexLog) {

        String model = channelModel.getModel();
        String naming = channelModel.getNaming();

        final boolean isShardingModel = null != shardingDefineConfiguration && shardingDefineConfiguration.isSharding(channelModel.getModule(), model);

        final String originModel = channelModel.getOrigin();
        final Long batchSize = Optional.ofNullable(channelModel.getBatchSize()).orElse(Pagination.defaultSize);

        Long minId = elasticIndexLog.getIdMin();
        Long maxId = elasticIndexLog.getIdMax();
        Long countId = elasticIndexLog.getIdCount();

        if (0L == minId || 0 == maxId || 0L == countId) {
            return 0L;
        }

        ChannelDumpExecutor dumpExecutor = new ChannelDumpExecutor(channelConfig.getThreadSize());
        ExecutorService executor = dumpExecutor.getExecutorService();

        // 流控
        int semaphoreSize = Math.max(channelConfig.getThreadSize(), 1);
        Semaphore semaphore = new Semaphore(semaphoreSize);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        while (minId < maxId) {

            final long _minId = minId;
            final long _maxId = minId + batchSize;

            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                log.error("获取信号量异常", e);
                continue;
            }

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try (BatchSizeHintApi ignored = BatchSizeHintApi.use(-1)) {
                    Pagination<DataMap> pagination = new Pagination<>();
                    pagination.setSort(new Sort().addOrder(new Order().setField(ID).setDirection(SortDirectionEnum.ASC)));
                    pagination.setSize(batchSize);
                    QueryWrapper<DataMap> wrapper = new QueryWrapper<>();
                    wrapper.from(originModel);
                    wrapper.select(SEPARATOR_ASTERISK);
                    wrapper.setBatchSize(batchSize.intValue());
                    if (!isShardingModel) {
                        // 分库分表退化成不同分页查选
                        String sql = ID + ">=" + _minId + " and " + ID + "<" + _maxId;
                        wrapper.apply(sql);
                    }
                    List<DataMap> dataMaps = genericMapper.selectListByPage(pagination, wrapper);
                    if (null == dataMaps || dataMaps.isEmpty()) {
                        return;
                    }
                    ModelDirectiveApi directiveApi = Models.modelDirective();
                    if (!directiveApi.isDoColumn(dataMaps)) {
                        directiveApi.enableColumn(dataMaps);
                    }
                    List<?> dataList = elasticDataConverter.out(model, dataMaps);
                    if (null == dataList || dataList.isEmpty()) {
                        return;
                    }
                    List<?> list = Models.directive().run(() -> Fun.run(model, "synchronize", dataList));
                    List<Map<String, Object>> maps = elasticDataConverter.in(model, list);
                    List<Map<String, Object>> resBulk = elasticDocApi.bulkIndex(naming, maps);
                    log.info("[{}] [{}] [{}]-[{}]", naming, resBulk.size(), _minId, _maxId);
                } catch (Throwable throwable) {
                    log.error("Dump发生异常", throwable);
                } finally {
                    semaphore.release();
                }
            }, executor);
            futures.add(future);
            minId = _maxId;
        }

        CompletableFuture<?>[] futureArray = futures.toArray(new CompletableFuture[0]);

        CompletableFuture<Void> allFuture = CompletableFuture.allOf(futureArray);

        try {
            allFuture.join();
            dumpExecutor.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Long syncedCount = elasticDocApi.count(naming);
        log.info("同步数量: [{}] DB数量: [{}]", syncedCount, countId);

        return syncedCount;
    }
}
