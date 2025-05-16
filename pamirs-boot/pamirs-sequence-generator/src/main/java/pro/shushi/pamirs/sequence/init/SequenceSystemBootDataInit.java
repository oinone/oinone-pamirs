package pro.shushi.pamirs.sequence.init;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.SystemBootAfterInit;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.sequence.manager.LeafAllocInitManager;
import pro.shushi.pamirs.sequence.manager.LeafSegment;
import pro.shushi.pamirs.sequence.model.LeafAlloc;
import pro.shushi.pamirs.sequence.utils.ZeroingPeriodUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SequenceSystemBootDataInit implements SystemBootAfterInit {

    private static final Logger log = LoggerFactory.getLogger(SequenceSystemBootDataInit.class);

    @Autowired
    private LeafSegment leafSegment;

    @Autowired
    private LeafAllocInitManager leafAllocInitManager;

    private static final String SEQ_CONFIG_MODEL = "base.SequenceConfig";


    @Override
    public boolean init(AppLifecycleCommand command) {

        log.info("Sequence Data init ...");

        QueryWrapper<SequenceConfig> qw = new QueryWrapper<>();
        qw.setModel(SEQ_CONFIG_MODEL);
        qw.select("`code`", "`prefix`", "`suffix`", "`size`", "`format`", "`step`", "`initial`", "`sequence`", "`zeroing_period` as zeroingPeriod");
        List<SequenceConfig> mapList = Models.data().queryListByWrapper(qw);

        //查询全部leafAlloc,准备删除无效数据
        List<LeafAlloc> leafAllocs = Models.origin().queryListByWrapper(
                Pops.<LeafAlloc>query().from(LeafAlloc.MODEL_MODEL).select("code")
        );
        List<String> leafAllocCodes = leafAllocs.stream().map(LeafAlloc::getCode).collect(Collectors.toList());

//        List<LeafAlloc> leafAllocs = Lists.newArrayListWithCapacity(mapList.size());
        for (SequenceConfig data : mapList) {
            String period = null;
            String rCode = data.getCode();
            if (ZeroingPeriodUtils.isDateSequence(data.getSequence())) {
                period = ZeroingPeriodUtils.periodFormat(data.getZeroingPeriod());
                rCode = ZeroingPeriodUtils.buildLeafAllocCode(data.getCode(), period);
            }
            if(leafAllocCodes.contains(rCode)){
                leafAllocCodes.remove(rCode);
                continue;
            }
            leafAllocInitManager.init(data, period);
        }
        if(CollectionUtils.isNotEmpty(leafAllocCodes)) {
            //删除无效的数据
            Models.origin().deleteByWrapper(
                    Pops.<LeafAlloc>lambdaQuery()
                            .from(LeafAlloc.MODEL_MODEL)
                            .in(LeafAlloc::getCode, leafAllocCodes)
            );
        }

//        int leafAllocsSize = new LeafAlloc().createOrUpdateBatch(leafAllocs);
//        log.info("Will Install LeafAlloc Size: [{}] Update Size: [{}]", leafAllocs.size(), leafAllocsSize);

        leafSegment.init();
        return true;
    }

    @Override
    public int priority() {
        return 0;
    }
}
