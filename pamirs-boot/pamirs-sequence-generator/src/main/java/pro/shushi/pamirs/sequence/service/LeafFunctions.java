package pro.shushi.pamirs.sequence.service;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.api.datasource.DsHintApi;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.SequenceEnum;
import pro.shushi.pamirs.sequence.common.Result;
import pro.shushi.pamirs.sequence.common.SequenceNameConstants;
import pro.shushi.pamirs.sequence.common.Status;
import pro.shushi.pamirs.sequence.enmu.ExpEnumBid;
import pro.shushi.pamirs.sequence.manager.SequenceConfigCacheManager;
import pro.shushi.pamirs.sequence.model.LeafAlloc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pro.shushi.pamirs.meta.enmu.FunctionSceneEnum.SEQUENCE;
import static pro.shushi.pamirs.sequence.enmu.ExpEnumBid.*;
import static pro.shushi.pamirs.sequence.utils.ZeroingPeriodUtils.buildLeafAllocCode;
import static pro.shushi.pamirs.sequence.utils.ZeroingPeriodUtils.periodFormat;

/**
 * LeafFunctions
 *
 * @author yakir on 2020/04/08 18:05.
 */
@Slf4j
@Base
@Fun(NamespaceConstants.sequence)
public class LeafFunctions implements SequenceNameConstants {

    private static final Map<String, DateTimeFormatter> DTFS = new ConcurrentHashMap<>();

    /**
     * @param code code
     * @return String
     * {@link SequenceEnum#SEQ}
     */
    @Base
    @Function.Advanced(displayName = "获取自增流水号", type = FunctionTypeEnum.QUERY)
    @Function(name = SEQ, scene = {SEQUENCE}, summary = "自增流水号")
    @Function.fun(SEQ)
    public String leafCodeSeq(String code) {

        if (StringUtils.isBlank(code)) {
            return null;
        }

        SequenceConfig data = SequenceConfigCacheManager.get(code);

        if (null == data || null == data.getCode()) {
            log.error("Leaf Code: [{}] [{}] [{}]", SequenceEnum.SEQ, code, ID_GEN_NOT_EXIST_SEQUENCE_CONFIG_CONFIG_ERROR.msg());
            throw PamirsException.construct(ID_GEN_NOT_EXIST_SEQUENCE_CONFIG_CONFIG_ERROR)
                    .appendMsg(SequenceEnum.SEQ.help())
                    .errThrow();
        }

        String rcode = data.getCode();
        String prefix = data.getPrefix();
        String suffix = data.getSuffix();
        Integer step = getStep0(data);
        Integer size = data.getSize();
        LeafSegmentService leafSs = BeanDefinitionUtils.getBean(LeafSegmentService.class);
        Result leafRt;
        try (DsHintApi ignored = DsHintApi.model(LeafAlloc.MODEL_MODEL)) {
            leafRt = leafSs.getId(rcode, step);
        }
        Long leaf = null;

        if (Status.SUCCESS == leafRt.getStatus()) {
            leaf = leafRt.getId();
        } else if (Status.EXCEPTION == leafRt.getStatus()) {
            consumeGenIdExp(leafRt, code, SequenceEnum.SEQ, ID_GEN_NOT_EXIST_LEAFALLOC_CONFIG_ERROR);
        }

        StringBuilder sbuilder = new StringBuilder();
        if (StringUtils.isNotBlank(prefix)) {
            sbuilder.append(prefix);
        }

        if (null != leaf && leaf > 0) {
            if (null != size && size > 0 && size > String.valueOf(leaf).length()) {
                sbuilder.append(String.format("%0" + size + "d", leaf));
            } else {
                sbuilder.append(leaf);
            }
        } else {
            log.error("Leaf Id 丢失 [{}] [{}]", SequenceEnum.SEQ, ID_GEN_ERROR.msg());
            throw PamirsException.construct(ID_GEN_ERROR)
                    .appendMsg(SequenceEnum.SEQ.help())
                    .errThrow();
        }

        if (StringUtils.isNotBlank(suffix)) {
            sbuilder.append(suffix);
        }

        return sbuilder.toString();
    }

    /**
     * @param code code
     * @return String
     * {@link SequenceEnum#ORDERLY_SEQ}
     */
    @Base
    @Function.Advanced(displayName = "获取强有序流水号", type = FunctionTypeEnum.QUERY)
    @Function(name = ORDERLY_SEQ, scene = {SEQUENCE}, summary = "自增强有序流水号")
    @Function.fun(ORDERLY_SEQ)
    public String leafCodeOrderlySeq(String code) {

        if (StringUtils.isBlank(code)) {
            return null;
        }
        SequenceConfig data = SequenceConfigCacheManager.get(code);

        if (null == data || null == data.getCode()) {
            log.error("Leaf Code: [{}] [{}] [{}]", SequenceEnum.ORDERLY_SEQ, code, ID_GEN_NOT_EXIST_SEQUENCE_CONFIG_CONFIG_ERROR.msg());
            throw PamirsException.construct(ID_GEN_NOT_EXIST_SEQUENCE_CONFIG_CONFIG_ERROR)
                    .appendMsg(SequenceEnum.ORDERLY_SEQ.help())
                    .errThrow();
        }

        String rcode = data.getCode();
        String prefix = data.getPrefix();
        String suffix = data.getSuffix();
        Integer step = getStep0(data);
        Integer size = data.getSize();
        LeafSegmentService leafSs = BeanDefinitionUtils.getBean(LeafSegmentService.class);
        Result leafRt;
        try (DsHintApi ignored = DsHintApi.model(LeafAlloc.MODEL_MODEL)) {
            leafRt = leafSs.getOrderId(rcode, step);
        }
        Long leaf = null;

        if (Status.SUCCESS == leafRt.getStatus()) {
            leaf = leafRt.getId();
        } else if (Status.EXCEPTION == leafRt.getStatus()) {
            consumeGenIdExp(leafRt, code, SequenceEnum.ORDERLY_SEQ, ID_GEN_NOT_EXIST_LEAFALLOC_CONFIG_ERROR);
        }

        StringBuilder sbuilder = new StringBuilder();
        if (StringUtils.isNotBlank(prefix)) {
            sbuilder.append(prefix);
        }

        if (null != leaf && leaf > 0) {
            if (null != size && size > 0 && size > String.valueOf(leaf).length()) {
                sbuilder.append(String.format("%0" + size + "d", leaf));
            } else {
                sbuilder.append(leaf);
            }
        } else {
            log.error("Leaf Id 丢失 [{}] [{}]", SequenceEnum.ORDERLY_SEQ, ID_GEN_ERROR.msg());
            throw PamirsException.construct(ID_GEN_ERROR)
                    .appendMsg(SequenceEnum.ORDERLY_SEQ.help())
                    .errThrow();
        }

        if (StringUtils.isNotBlank(suffix)) {
            sbuilder.append(suffix);
        }

        return sbuilder.toString();
    }

    /**
     * @param code code
     * @return String
     * {@link SequenceEnum#DATE_SEQ}
     */
    @Base
    @Function.Advanced(displayName = "获取日期流水号", type = FunctionTypeEnum.QUERY)
    @Function(name = DATE_SEQ, scene = {SEQUENCE}, summary = "日期流水号")
    @Function.fun(DATE_SEQ)
    public String leafCodeDateSeq(String code) {

        if (StringUtils.isBlank(code)) {
            return null;
        }

        SequenceConfig data = SequenceConfigCacheManager.get(code);

        if (null == data || null == data.getCode()) {
            log.error("Leaf Code: [{}] [{}] [{}]", SequenceEnum.DATE_SEQ, code, ID_GEN_NOT_EXIST_SEQUENCE_CONFIG_CONFIG_ERROR.msg());
            throw PamirsException.construct(ID_GEN_NOT_EXIST_SEQUENCE_CONFIG_CONFIG_ERROR)
                    .appendMsg(SequenceEnum.DATE_SEQ.help())
                    .errThrow();
        }
        LocalDateTime nowDate = LocalDateTime.now();

        String rcode = buildLeafAllocCode(data.getCode(), periodFormat(data.getZeroingPeriod(), nowDate));
        String fmt = data.getFormat();
        String prefix = data.getPrefix();
        String suffix = data.getSuffix();
        Integer step = getStep0(data);
        Integer size = data.getSize();
        LeafSegmentService leafSs = BeanDefinitionUtils.getBean(LeafSegmentService.class);
        Result leafRt;
        try (DsHintApi ignored = DsHintApi.model(LeafAlloc.MODEL_MODEL)) {
            leafRt = leafSs.getId(rcode, step);
        }

        Long leaf = null;
        if (Status.SUCCESS == leafRt.getStatus()) {
            leaf = leafRt.getId();
        } else if (Status.EXCEPTION == leafRt.getStatus()) {
            consumeGenIdExp(leafRt, code, SequenceEnum.DATE_SEQ, ID_GEN_NOT_EXIST_LEAFALLOC_CONFIG_ERROR);
        }

        String now = dateFmt(fmt, nowDate);
        StringBuilder sbuilder = new StringBuilder();
        if (StringUtils.isNotBlank(prefix)) {
            sbuilder.append(prefix);
        }
        if (StringUtils.isNotBlank(now)) {
            sbuilder.append(now);
        }

        if (null != leaf && leaf > 0) {
            if (null != size && size > 0 && size > String.valueOf(leaf).length()) {
                sbuilder.append(String.format("%0" + size + "d", leaf));
            } else {
                sbuilder.append(leaf);
            }
        } else {
            log.error("Leaf Id 丢失 [{}] [{}]", SequenceEnum.DATE_SEQ, ID_GEN_ERROR.msg());
            throw PamirsException.construct(ID_GEN_ERROR)
                    .appendMsg(SequenceEnum.DATE_SEQ.help())
                    .errThrow();
        }

        if (StringUtils.isNotBlank(suffix)) {
            sbuilder.append(suffix);
        }

        return sbuilder.toString();
    }

    /**
     * @param code code
     * @return String
     * {@link SequenceEnum#DATE_ORDERLY_SEQ}
     */
    @Base
    @Function.Advanced(displayName = "获取日期强有序流水号", type = FunctionTypeEnum.QUERY)
    @Function(name = DATE_ORDERLY_SEQ, scene = {SEQUENCE}, summary = "日期强有序流水号")
    @Function.fun(DATE_ORDERLY_SEQ)
    public String leafCodeDateOrderlySeq(String code) {

        if (StringUtils.isBlank(code)) {
            return null;
        }

        SequenceConfig data = SequenceConfigCacheManager.get(code);

        if (null == data || null == data.getCode()) {
            log.error("Leaf Code: [{}] [{}] [{}]", SequenceEnum.DATE_ORDERLY_SEQ, code, ID_GEN_NOT_EXIST_SEQUENCE_CONFIG_CONFIG_ERROR.msg());
            throw PamirsException.construct(ID_GEN_NOT_EXIST_SEQUENCE_CONFIG_CONFIG_ERROR)
                    .appendMsg(SequenceEnum.DATE_ORDERLY_SEQ.help())
                    .errThrow();
        }
        LocalDateTime nowDate = LocalDateTime.now();

        String rcode = buildLeafAllocCode(data.getCode(), periodFormat(data.getZeroingPeriod(), nowDate));
        String fmt = data.getFormat();
        String prefix = data.getPrefix();
        String suffix = data.getSuffix();
        Integer step = getStep0(data);
        Integer size = data.getSize();
        LeafSegmentService leafSs = BeanDefinitionUtils.getBean(LeafSegmentService.class);
        Result leafRt;
        try (DsHintApi ignored = DsHintApi.model(LeafAlloc.MODEL_MODEL)) {
            leafRt = leafSs.getOrderId(rcode, step);
        }

        Long leaf = null;
        if (Status.SUCCESS == leafRt.getStatus()) {
            leaf = leafRt.getId();
        } else if (Status.EXCEPTION == leafRt.getStatus()) {
            consumeGenIdExp(leafRt, code, SequenceEnum.DATE_ORDERLY_SEQ, ID_GEN_NOT_EXIST_LEAFALLOC_CONFIG_ERROR);
        }

        String now = dateFmt(fmt, nowDate);
        StringBuilder sbuilder = new StringBuilder();
        if (StringUtils.isNotBlank(prefix)) {
            sbuilder.append(prefix);
        }
        if (StringUtils.isNotBlank(now)) {
            sbuilder.append(now);
        }

        if (null != leaf && leaf > 0) {
            if (null != size && size > 0 && size > String.valueOf(leaf).length()) {
                sbuilder.append(String.format("%0" + size + "d", leaf));
            } else {
                sbuilder.append(leaf);
            }
        } else {
            log.error("Leaf Id 丢失 [{}] [{}]", SequenceEnum.DATE_ORDERLY_SEQ, ID_GEN_ERROR.msg());
            throw PamirsException.construct(ID_GEN_ERROR)
                    .appendMsg(SequenceEnum.DATE_ORDERLY_SEQ.help())
                    .errThrow();
        }

        if (StringUtils.isNotBlank(suffix)) {
            sbuilder.append(suffix);
        }

        return sbuilder.toString();
    }

    /**
     * @param code code
     * @return String
     * {@link SequenceEnum#DATE}
     */
    @Base
    @Function.Advanced(displayName = "获取日期", type = FunctionTypeEnum.QUERY)
    @Function(name = DATE, scene = {SEQUENCE}, summary = "日期")
    @Function.fun(DATE)
    public String leafCodeDate(String code) {

        if (StringUtils.isBlank(code)) {
            return null;
        }

        SequenceConfig data = SequenceConfigCacheManager.get(code);

        if (null == data || null == data.getCode()) {
            log.error("Leaf Code: [{}] [{}] [{}]", SequenceEnum.DATE_SEQ, code, ID_GEN_NOT_EXIST_SEQUENCE_CONFIG_CONFIG_ERROR.msg());
            throw PamirsException.construct(ID_GEN_NOT_EXIST_SEQUENCE_CONFIG_CONFIG_ERROR)
                    .appendMsg(SequenceEnum.DATE_SEQ.help())
                    .errThrow();
        }

        String fmt = data.getFormat();
        String prefix = data.getPrefix();
        String suffix = data.getSuffix();
        String now = dateFmt(fmt, LocalDateTime.now());
        StringBuilder sbuilder = new StringBuilder();

        if (StringUtils.isNotBlank(prefix)) {
            sbuilder.append(prefix);
        }
        if (StringUtils.isNotBlank(now)) {
            sbuilder.append(now);
        }

        if (StringUtils.isNotBlank(suffix)) {
            sbuilder.append(suffix);
        }

        return sbuilder.toString();
    }

    /**
     * @param code code
     * @return String
     * {@link SequenceEnum#UUID}
     */
    @Base
    @Function.Advanced(displayName = "获取UUID", type = FunctionTypeEnum.QUERY)
    @Function(name = UUID, scene = {SEQUENCE}, summary = "UUID")
    @Function.fun(UUID)
    public String leafUUID(String code) {

        if (StringUtils.isBlank(code)) {
            return null;
        }

        SequenceConfig data = SequenceConfigCacheManager.get(code);

        if (null == data || null == data.getCode()) {
            log.error("Leaf Code: [{}] [{}] [{}]", SequenceEnum.DATE_SEQ, code, ID_GEN_NOT_EXIST_SEQUENCE_CONFIG_CONFIG_ERROR.msg());
            throw PamirsException.construct(ID_GEN_NOT_EXIST_SEQUENCE_CONFIG_CONFIG_ERROR)
                    .appendMsg(SequenceEnum.UUID.help())
                    .errThrow();
        }

        String fmt = data.getFormat();
        String prefix = data.getPrefix();
        String suffix = data.getSuffix();
        String now = dateFmt(fmt, LocalDateTime.now());
        String uuid = UUIDUtil.getUUIDNumberString();

        StringBuilder sbuilder = new StringBuilder();
        if (StringUtils.isNotBlank(prefix)) {
            sbuilder.append(prefix);
        }
        if (StringUtils.isNotBlank(now)) {
            sbuilder.append(now);
        }

        sbuilder.append(uuid);

        if (StringUtils.isNotBlank(suffix)) {
            sbuilder.append(suffix);
        }

        return sbuilder.toString();
    }

    public void consumeGenIdExp(Result leafRt, String code, SequenceEnum sequenceEnum, ExpEnumBid expEnumBid) {
        Long eid = leafRt.getId();
        if (null != eid) {
            if (eid == -1) {
                log.error("Leaf Code: [{}] [{}] [{}] [{}]", sequenceEnum, leafRt.getId(), code, ID_GEN_EXCEPTION_ID_IDCACHE_INIT_FALSE.msg());
                throw PamirsException.construct(ID_GEN_EXCEPTION_ID_IDCACHE_INIT_FALSE).errThrow();
            } else if (eid == -2) {
                log.error("Leaf Code: [{}] [{}] [{}] [{}]", sequenceEnum, leafRt.getId(), code, ID_GEN_EXCEPTION_ID_KEY_NOT_EXISTS.msg());
                throw PamirsException.construct(ID_GEN_EXCEPTION_ID_KEY_NOT_EXISTS).errThrow();
            } else if (eid == -3) {
                log.error("Leaf Code: [{}] [{}] [{}] [{}]", sequenceEnum, leafRt.getId(), code, ID_GEN_EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL.msg());
                throw PamirsException.construct(ID_GEN_EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL).errThrow();
            }
        }
        log.error("Leaf Code: [{}] [{}] [{}] [{}]", sequenceEnum, leafRt.getId(), code, expEnumBid.msg());
        throw PamirsException.construct(expEnumBid).errThrow();
    }

    private Integer getStep0(SequenceConfig data) {
        Integer step = data.getStep();
        if (!Boolean.TRUE.equals(data.getIsRandomStep()) || step <= 1) {
            //不需要随机
            return step;
        }
        //在1-指定步长之间,随机1个
        return RandomUtils.nextInt(1, step + 1);
    }

    private String dateFmt(String fmt, LocalDateTime date) {
        if (StringUtils.isBlank(fmt)) {
            return null;
        }
        return DTFS.computeIfAbsent(fmt, DateTimeFormatter::ofPattern).format(date);
    }
}
