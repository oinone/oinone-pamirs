package pro.shushi.pamirs.core.sequence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pro.shushi.pamirs.core.sequence.condition.SequenceDebugSwitchCondition;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.sequence.domain.SegmentBufferView;
import pro.shushi.pamirs.sequence.manager.LeafSegment;
import pro.shushi.pamirs.sequence.model.LeafAlloc;
import pro.shushi.pamirs.sequence.model.SegmentBuffer;
import pro.shushi.pamirs.sequence.service.LeafSegmentService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController("leaf")
@Conditional(SequenceDebugSwitchCondition.class)
public class LeafMonitorController {

    @Autowired
    private LeafSegmentService segmentService;

    @ResponseBody
    @GetMapping(value = "cache")
    public Map<String, SegmentBufferView> getCache() {
        Map<String, SegmentBufferView> data = new HashMap<>();
        LeafSegment segmentIDGen = segmentService.getLeaf();
        if (segmentIDGen == null) {
            throw new IllegalArgumentException("You should config leaf.segment.enable=true first");
        }
        Map<String, SegmentBuffer> cache = segmentIDGen.getCache();
        for (Map.Entry<String, SegmentBuffer> entry : cache.entrySet()) {
            SegmentBufferView sv = new SegmentBufferView();
            SegmentBuffer buffer = entry.getValue();
            sv.setInitOk(buffer.isInitOk());
            sv.setKey(buffer.getKey());
            sv.setThreadRunning(buffer.getThreadRunning().get());
            sv.setPos(buffer.getCurrentPos());
            sv.setNextReady(buffer.isNextReady());
            sv.setMax0(buffer.getSegments()[0].getMax());
            sv.setValue0(buffer.getSegments()[0].getValue().get());
            sv.setStep0(buffer.getSegments()[0].getStep());

            sv.setMax1(buffer.getSegments()[1].getMax());
            sv.setValue1(buffer.getSegments()[1].getValue().get());
            sv.setStep1(buffer.getSegments()[1].getStep());

            data.put(entry.getKey(), sv);

        }
        log.info("Cache info {}", data);
        return data;
    }

    @ResponseBody
    @GetMapping(value = "db")
    public List<LeafAlloc> getDb() {
        LeafSegment segmentIDGen = segmentService.getLeaf();
        if (segmentIDGen == null) {
            throw new IllegalArgumentException("You should config leaf.segment.enable=true first");
        }
        List<LeafAlloc> items = segmentIDGen.getAllLeafAllocs();
        log.info("DB info {}", items);
        return items;
    }

    /**
     * the output is like this:
     * {
     * "timestamp": "1567733700834(2019-09-06 09:35:00.834)",
     * "sequenceId": "3448",
     * "workerId": "39"
     * }
     */
    @ResponseBody
    @GetMapping(value = "decodeSnowflakeId")
    public Map<String, String> decodeSnowflakeId(@RequestParam("snowflakeId") String snowflakeIdStr) {
        Map<String, String> map = new HashMap<>();
        try {
            long snowflakeId = Long.parseLong(snowflakeIdStr);

            long originTimestamp = (snowflakeId >> 22) + 1288834974657L;
            Date date = new Date(originTimestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            map.put("timestamp", String.valueOf(originTimestamp) + "(" + sdf.format(date) + ")");

            long workerId = (snowflakeId >> 12) ^ (snowflakeId >> 22 << 10);
            map.put("workerId", String.valueOf(workerId));

            long sequence = snowflakeId ^ (snowflakeId >> 12 << 12);
            map.put("sequenceId", String.valueOf(sequence));
        } catch (NumberFormatException e) {
            map.put("errorMsg", "snowflake Id反解析发生异常!");
        }
        return map;
    }
}