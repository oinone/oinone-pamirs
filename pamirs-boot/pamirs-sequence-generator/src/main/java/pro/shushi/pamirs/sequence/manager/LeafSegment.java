package pro.shushi.pamirs.sequence.manager;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.enmu.TimePeriodEnum;
import pro.shushi.pamirs.sequence.common.Result;
import pro.shushi.pamirs.sequence.common.SequenceConstants;
import pro.shushi.pamirs.sequence.common.Status;
import pro.shushi.pamirs.sequence.dao.ISequenceDao;
import pro.shushi.pamirs.sequence.model.LeafAlloc;
import pro.shushi.pamirs.sequence.model.Segment;
import pro.shushi.pamirs.sequence.model.SegmentBuffer;
import pro.shushi.pamirs.sequence.utils.ThreadFactorys;
import pro.shushi.pamirs.sequence.utils.ZeroingPeriodUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * LeafSegment
 *
 * @author yakir on 2020/04/08 16:17.
 */
@Component
public class LeafSegment implements ILeaf {

    private static final Logger log = LoggerFactory.getLogger(LeafSegment.class);

    @Autowired
    private ISequenceDao dao;

    @Autowired
    private LeafAllocInitManager leafAllocInitManager;

    // 最大步长不超过100,0000
    private static final int MAX_STEP = 1000000;
    // 一个Segment维持时间为15分钟
    private static final long SEGMENT_DURATION = 15 * 60 * 1000L;

    private volatile boolean initOK = false;

    private final Map<String, SegmentBuffer> CACHE = new ConcurrentHashMap<>();
    private final ExecutorService SERVICE = new ThreadPoolExecutor(2, 64, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactorys.UpdateThreadFactory());
    // private final ScheduledExecutorService UPDATE_CACHE_SERVICE = Executors.newSingleThreadScheduledExecutor(new ThreadFactorys.UpdateCacheFromDbFactory());

    @Override
    public void init() {
        log.info("Segment Init ...");
        // 确保加载到kv后才初始化成功
        updateCacheFromDb();
        initOK = true;
        // UPDATE_CACHE_SERVICE.scheduleWithFixedDelay(this::updateCacheFromDb, 60, 60, TimeUnit.SECONDS);
        updateCacheFromDbAtEveryMinute();
        log.info("Segment Service Init [{}]", initOK);
    }

    @Override
    public boolean getInitStatus() {
        return initOK;
    }

    private void updateCacheFromDbAtEveryMinute() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new ThreadFactorys.UpdateCacheFromDbFactory());
        service.scheduleWithFixedDelay(new UpdateCacheRunnable(this::updateCacheFromDb), 60, 60, TimeUnit.SECONDS);
    }

    private void updateCacheFromDb() {
        log.info("update cache from db");
        StopWatch sw = new Slf4JStopWatch(log);
        try {
            List<String> dbTags = dao.getAllTags();
            if (dbTags == null || dbTags.isEmpty()) {
                return;
            }
            //过滤已过期的周期性数据
            List<String> overdueTags = dbTags.stream()
                    .filter(i -> i.contains(ZeroingPeriodUtils.PERIOD_SEPARATOR))//包含周期分隔符的
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(overdueTags)) {
                LocalDate nowDate = LocalDate.now();
                // TODO: 2021/8/13 如果有请求在周期切换前进来尝试获取code.同时发生定时刷新导致上一个周期的信息被清空. 这里考虑把上一个周期的字符串也加入有效的周期列表
                List<String> periods = Arrays.asList(
                        ZeroingPeriodUtils.periodFormat(TimePeriodEnum.YEAR, nowDate),
                        ZeroingPeriodUtils.periodFormat(TimePeriodEnum.MONTH, nowDate),
                        ZeroingPeriodUtils.periodFormat(TimePeriodEnum.DAY, nowDate)
                );
                overdueTags = overdueTags.stream()
                        .filter(i -> {
                            for (String period : periods) {
                                if (i.endsWith(period)) {
                                    //忽略当前时间区间,有效值
                                    return false;
                                }
                            }
                            return true;
                        })
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(overdueTags)) {
                    dbTags.removeAll(overdueTags);
                }
            }

            List<String> cacheTags = new ArrayList<>(CACHE.keySet());
            Set<String> insertTagsSet = new HashSet<>(dbTags);
            Set<String> removeTagsSet = new HashSet<>(cacheTags);
            //db中新加的tags灌进cache
            for (int i = 0; i < cacheTags.size(); i++) {
                String tmp = cacheTags.get(i);
                if (insertTagsSet.contains(tmp)) {
                    insertTagsSet.remove(tmp);
                }
            }
            for (String tag : insertTagsSet) {
                SegmentBuffer buffer = new SegmentBuffer();
                buffer.setKey(tag);
                Segment segment = buffer.getCurrent();
                segment.setValue(new AtomicLong(0));
                segment.setMax(0);
                segment.setStep(0);
                CACHE.put(tag, buffer);
                log.info("Add tag {} from db to IdCache, SegmentBuffer {}", tag, buffer);
            }
            //cache中已失效的tags从cache删除
            for (int i = 0; i < dbTags.size(); i++) {
                String tmp = dbTags.get(i);
                if (removeTagsSet.contains(tmp)) {
                    removeTagsSet.remove(tmp);
                }
            }
            for (String tag : removeTagsSet) {
                CACHE.remove(tag);
                log.info("Remove tag {} from IdCache", tag);
            }

            //过期的db数据删除
            if (CollectionUtils.isNotEmpty(overdueTags)) {
                Models.origin().deleteByWrapper(
                        Pops.<LeafAlloc>lambdaQuery()
                                .from(LeafAlloc.MODEL_MODEL)
                                .in(LeafAlloc::getCode, overdueTags)
                );
            }
        } catch (Exception e) {
            log.warn("update cache from db exception", e);
        } finally {
            sw.stop("updateCacheFromDb");
        }
    }

    @Override
//    @Transactional  // fixme yakir Transactional
    public Result getOrderID(final String key, Integer step) {
        if (!initOK) {
            return new Result(SequenceConstants.EXCEPTION_ID_IDCACHE_INIT_FALSE, Status.EXCEPTION);
        }

        if (StringUtils.isBlank(key)) {
            return new Result(SequenceConstants.EXCEPTION_ID_IDCACHE_INIT_FALSE, Status.EXCEPTION);
        }

        LeafAlloc query = new LeafAlloc().setCode(key);
        LeafAlloc fromDB = dao.getLeafAllocOneByRequired(query);
        if (null == fromDB) {
            fromDB = initLeafAlloc(key, dao::getLeafAllocOneByRequired);
        }

        final LeafAlloc fromDBF = fromDB;
        LeafAlloc updated = dao.updateMaxIdByCustomStepAndGetLeafAllocByRequired(fromDBF.setStep(step));

        if (null == updated) {
            return new Result(SequenceConstants.EXCEPTION_ID_IDCACHE_INIT_FALSE, Status.EXCEPTION);
        }

        Long bid = updated.getMaxId();

        return new Result(bid, Status.SUCCESS);
    }

    @Override
    public Result get(final String key, int rstep) {
        if (!initOK) {
            return new Result(SequenceConstants.EXCEPTION_ID_IDCACHE_INIT_FALSE, Status.EXCEPTION);
        }
        if (!CACHE.containsKey(key)) {
            synchronized (this) {
                //初始化LeafAlloc
                initLeafAlloc(key);
                //锁住重新确认
                if (!CACHE.containsKey(key)) {
                    SegmentBuffer buffer = new SegmentBuffer();
                    buffer.setKey(key);
                    Segment segment = buffer.getCurrent();
                    segment.setValue(new AtomicLong(0));
                    segment.setMax(0);
                    segment.setStep(0);
                    CACHE.put(key, buffer);
                    log.info("Add tag {} from db to IdCache, SegmentBuffer {}", key, buffer);
                }
            }
        }
        SegmentBuffer buffer = CACHE.get(key);
        if (!buffer.isInitOk()) {
            synchronized (buffer) {
                if (!buffer.isInitOk()) {
                    try {
                        updateSegmentFromDb(key, buffer.getCurrent());
                        log.info("Init buffer. Update leafkey {} {} from db", key, buffer.getCurrent());
                        buffer.setInitOk(true);
                    } catch (Exception e) {
                        log.warn("Init buffer {} exception", buffer.getCurrent(), e);
                    }
                }
            }
        }
        return getIdFromSegmentBuffer(CACHE.get(key), rstep);
    }

    /**
     * 初始化 leafAlloc. 场景 1:周期性的序列化配置,时间周期发生切换. 2:低代码在运行过程中配置了新的sequenceConfig
     *
     * @param key 包含时间周期的字符串
     * @return
     */
    private LeafAlloc initLeafAlloc(String key) {
        return initLeafAlloc(key, dao::getLeafAllocOne);
    }

    private LeafAlloc initLeafAlloc(String key, Function<LeafAlloc, LeafAlloc> function) {
        String sequenceConfigCode;
        String period = null;
        if (!key.contains(ZeroingPeriodUtils.PERIOD_SEPARATOR)) {
            sequenceConfigCode = key;
        } else {
            String[] keys = key.split(ZeroingPeriodUtils.PERIOD_SEPARATOR);
            sequenceConfigCode = keys[0];
            period = keys[1];
        }
        synchronized (this) {
            //锁住重新查询
            LeafAlloc query = new LeafAlloc().setCode(key);
            LeafAlloc leafAlloc = function.apply(query);
            if (leafAlloc != null) {
                return leafAlloc;
            }
            // 新建leaf_alloc
            return leafAllocInitManager.init(
                    SequenceConfigCacheManager.get(sequenceConfigCode),
                    period
            );
        }
    }

    public void updateSegmentFromDb(String code, Segment segment) {
        StopWatch sw = new Slf4JStopWatch(log);
        SegmentBuffer buffer = segment.getBuffer();
        LeafAlloc leafAlloc;
        if (!buffer.isInitOk()) {
            leafAlloc = dao.updateMaxIdAndGetLeafAlloc(new LeafAlloc().setCode(code));
            buffer.setStep(leafAlloc.getStep());
            buffer.setMinStep(leafAlloc.getStep());//leafAlloc中的step为DB中的step
        } else if (buffer.getUpdateTimestamp() == 0) {
            leafAlloc = dao.updateMaxIdAndGetLeafAlloc(new LeafAlloc().setCode(code));
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(leafAlloc.getStep());
            buffer.setMinStep(leafAlloc.getStep());//leafAlloc中的step为DB中的step
        } else {
            long duration = System.currentTimeMillis() - buffer.getUpdateTimestamp();
            int nextStep = buffer.getStep();
            if (duration < SEGMENT_DURATION) {
                if (nextStep * 2 > MAX_STEP) {
                    //do nothing
                } else {
                    nextStep = nextStep * 2;
                }
            } else if (duration < SEGMENT_DURATION * 2) {
                //do nothing with nextStep
            } else {
                nextStep = nextStep / 2 >= buffer.getMinStep() ? nextStep / 2 : nextStep;
            }
            log.info("leafKey[{}], step[{}], duration[{}mins], nextStep[{}]", code, buffer.getStep(), String.format("%.2f", ((double) duration / (1000 * 60))), nextStep);
            LeafAlloc temp = new LeafAlloc();
            temp.setCode(code);
            temp.setStep(nextStep);
            leafAlloc = dao.updateMaxIdByCustomStepAndGetLeafAlloc(temp);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(nextStep);
            buffer.setMinStep(leafAlloc.getStep());//leafAlloc的step为DB中的step
        }
        // must set value before set max
        long value = leafAlloc.getMaxId() - buffer.getStep();
        segment.getValue().set(value);
        segment.setMax(leafAlloc.getMaxId());
        segment.setStep(buffer.getStep());
        sw.stop("updateSegmentFromDb", code + " " + segment);
    }

    public Result getIdFromSegmentBuffer(final SegmentBuffer buffer, int rstep) {
        while (true) {
            buffer.rLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                if (!buffer.isNextReady() && (segment.getIdle() < 0.9 * segment.getStep()) && buffer.getThreadRunning().compareAndSet(false, true)) {
                    log.debug("[{}] Will Update Segment", buffer.getKey());
                    SERVICE.execute(new UpdateCacheRunnable(() -> {
                        Segment next = buffer.getSegments()[buffer.nextPos()];
                        boolean updateOk = false;
                        try {
                            updateSegmentFromDb(buffer.getKey(), next);
                            updateOk = true;
                            log.info("update segment {} from db {}", buffer.getKey(), next);
                        } catch (Exception e) {
                            log.warn("{} updateSegmentFromDb exception", buffer.getKey(), e);
                        } finally {
                            if (updateOk) {
                                buffer.wLock().lock();
                                buffer.setNextReady(true);
                                buffer.getThreadRunning().set(false);
                                buffer.wLock().unlock();
                            } else {
                                buffer.getThreadRunning().set(false);
                            }
                        }
                    }));
                }
                long value = segment.getValue().getAndAdd(rstep);
                if (value < segment.getMax()) {
                    return new Result(value, Status.SUCCESS);
                }
            } finally {
                buffer.rLock().unlock();
            }
            waitAndSleep(buffer);
            buffer.wLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                long value = segment.getValue().getAndAdd(rstep);
                if (value < segment.getMax()) {
                    return new Result(value, Status.SUCCESS);
                }
                if (buffer.isNextReady()) {
                    buffer.switchPos();
                    buffer.setNextReady(false);
                } else {
                    log.error("Both two segments in [{}] are not ready!", buffer);
                    return new Result(SequenceConstants.EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL, Status.EXCEPTION);
                }
            } finally {
                buffer.wLock().unlock();
            }
        }
    }

    private void waitAndSleep(SegmentBuffer buffer) {
        int roll = 0;
        while (buffer.getThreadRunning().get()) {
            roll += 1;
            if (roll < 10000) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    log.warn("Thread {} Interrupted", Thread.currentThread().getName());
                    break;
                }
            } else {
                break;
            }
        }
    }

    private static class UpdateCacheRunnable implements Runnable {

        private final Map<String, String> sessionContext;
        private final Runnable runnable;

        public UpdateCacheRunnable(Runnable runnable) {
            this.sessionContext = PamirsSession.fetchSessionMap();
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                PamirsSession.clear();
                PamirsSession.fillSessionFromMap(sessionContext);
                runnable.run();
            } finally {
                PamirsSession.clear();
            }
        }
    }


    public List<LeafAlloc> getAllLeafAllocs() {
        return dao.getAllLeafAllocs();
    }

    public Map<String, SegmentBuffer> getCache() {
        return CACHE;
    }

    public ISequenceDao getDao() {
        return dao;
    }

    public LeafSegment setDao(ISequenceDao dao) {
        this.dao = dao;
        return this;
    }
}
