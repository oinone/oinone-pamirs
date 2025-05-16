package pro.shushi.pamirs.sequence.manager;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.enmu.SequenceEnum;
import pro.shushi.pamirs.sequence.model.LeafAlloc;
import pro.shushi.pamirs.sequence.utils.ZeroingPeriodUtils;

import java.util.Map;

/**
 * @author drome
 * @date 2021/8/129:02 下午
 */
@Component
public class LeafAllocInitManager {

    private static final Map<SequenceEnum, Boolean> SEQUENCE_KIND_MAP = ImmutableMap.<SequenceEnum, Boolean>builder()
            .put(SequenceEnum.SEQ, false)
            .put(SequenceEnum.ORDERLY_SEQ, true)
            .put(SequenceEnum.DATE_SEQ, false)
            .put(SequenceEnum.DATE_ORDERLY_SEQ, true)
            .put(SequenceEnum.DATE, false)
            .put(SequenceEnum.UUID, false)
            .put(SequenceEnum.DISTRIBUTION, false)
            .build();

    public LeafAlloc init(SequenceConfig sequenceConfig, String period) {
        String code = sequenceConfig.getCode();
        if (StringUtils.isBlank(code)) {
            //没有code,先忽略
            return null;
        }
        String sequence = sequenceConfig.getSequence();
        if (StringUtils.isBlank(sequence)) {
            //未指定序列化方式,错误的配置,忽略
            return null;
        }
        //如果是时间类型的序列化方式,并启用了归零周期,那么加上当前时间周期的条件
        if (ZeroingPeriodUtils.isDateSequence(sequence)) {
            code = ZeroingPeriodUtils.buildLeafAllocCode(code, period == null ? ZeroingPeriodUtils.periodFormat(sequenceConfig.getZeroingPeriod()) : period);
        }
        LeafAlloc leafAlloc = new LeafAlloc().setCode(code);
        if (leafAlloc.count() > 0) {
            //已存在leafAlloc配置,忽略
            // FIXME: 2021/8/12 这个对象只有code
            // TODO: 2021/8/13 调用方要么启动单线程先查询再创建,要么先上锁查询没有再创建, 这里理论上不会执行到
            return leafAlloc;
        }
        for (SequenceEnum item : SequenceEnum.values()) {
            if (item.value().equalsIgnoreCase(sequence)) {
                if (SEQUENCE_KIND_MAP.get(item)) {
                    leafAlloc.setMaxId(0L);
                } else {
                    leafAlloc.setMaxId(1L);
                }
                break;
            }
        }
        //如果配置了起始值,则累加
        Long initial = sequenceConfig.getInitial();
        if (initial != null && initial > 1) {
            leafAlloc.setMaxId(leafAlloc.getMaxId() + initial - 1);
        }

        if (SequenceEnum.ORDERLY_SEQ.value().equals(sequence)
                || SequenceEnum.DATE.value().equals(sequence)
                || SequenceEnum.DATE_ORDERLY_SEQ.value().equals(sequence)
                || SequenceEnum.UUID.value().equals(sequence)) {

            leafAlloc.setNeedLoad(false);
        }

        return leafAlloc.create();
    }
}
