package pro.shushi.pamirs.channel.core.manager;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.channel.enmu.IncrementEnum;
import pro.shushi.pamirs.channel.model.ChannelModel;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.ElasticDocApi;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.ModelDirectiveApi;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.CHANNEL_DUMP_MANAGER_ERROR;
import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.SYSTEM_ERROR;
import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_ASTERISK;
import static pro.shushi.pamirs.meta.constant.FieldConstants.ID;

/**
 * ProcessorOneManager
 *
 * @author yakir on 2020/04/23 13:48.
 */
@Slf4j
@Component
public class ProcessorOneManager extends AbstractProcessor {

    @Autowired
    private ElasticDocApi elasticDocApi;
    @Autowired
    private GenericMapper genericMapper;
    @Autowired
    private ElasticDataConverter elasticDataConverter;

    public ProcessorOneManager isOk() {
        if (null != elasticDocApi) {
            log.info("One isOK!");
        } else {
            throw PamirsException.construct(CHANNEL_DUMP_MANAGER_ERROR).errThrow();
        }
        return this;
    }

    public Long readOne(ChannelModel channelModel, Long id, Map<String, Object> shardingKeyMap, boolean isDeleted) {

        if (null == channelModel || null == id || 0L == id) {
            return 0L;
        }

        String model = channelModel.getModel();
        String naming = channelModel.getNaming();

        IncrementEnum incrementEnum = channelModel.getIncrement();
        if (null == incrementEnum || incrementEnum == IncrementEnum.CLOSE) {
            return 0L;
        }

        final String originModel = channelModel.getOrigin();

        if (StringUtils.isAnyBlank(model, originModel)) {
            throw PamirsException.construct(SYSTEM_ERROR).errThrow();
        }
        StringBuilder applySql = new StringBuilder();
        applySql.append("id = ").append(id);
        for (Map.Entry<String, Object> entry : shardingKeyMap.entrySet()) {
            applySql.append(" and ").append(entry.getKey()).append(" = ");
            if (entry.getValue() instanceof String) {
                applySql.append("'").append(entry.getValue()).append("'");
            } else {
                applySql.append(entry.getValue());
            }
        }

        DataMap dataMap = null;
        if (isDeleted) {
            dataMap = new DataMap(originModel);
            dataMap.setValue(ID, id);
            dataMap.setValue("isDeleted", new Date().getTime());
        } else {
            QueryWrapper<DataMap> wrapper = new QueryWrapper<>();
            wrapper.from(originModel);
            wrapper.select(SEPARATOR_ASTERISK);
            wrapper.apply(applySql.toString());
            log.info("tenant: [{}]", PamirsTenantSession.getTenant());
            dataMap = genericMapper.selectOne(wrapper);
            if (null == dataMap || dataMap.isEmpty()) {
                log.error("Empty Data {}", id);
                return 0L;
            }
        }
        ModelDirectiveApi directiveApi = Models.modelDirective();
        if (!directiveApi.isDoColumn(dataMap)) {
            directiveApi.enableColumn(dataMap);
        }
        List<?> dataList = elasticDataConverter.out(model, Lists.newArrayList(dataMap));
        if (null == dataList || dataList.isEmpty()) {
            return 0L;
        }

        List<?> result = Models.directive().run(() -> Fun.run(model, "synchronize", dataList));

        if (null == result) {
            log.error("Error {}", id);
            return 0L;
        }
        List<Map<String, Object>> maps = elasticDataConverter.in(model, result);
        List<Map<String, Object>> resBluk = elasticDocApi.bulkIndex(naming, maps);
        log.info("BINLOG_EVENT_TOPIC: dump [{}] [{}]", naming, resBluk == null ? 0 : resBluk.size());

        return id;
    }

}
