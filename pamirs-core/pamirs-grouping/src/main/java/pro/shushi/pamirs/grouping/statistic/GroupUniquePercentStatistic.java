//package pro.shushi.pamirs.grouping.statistic;
//
//import org.springframework.stereotype.Component;
//import pro.shushi.pamirs.grouping.model.GroupingData;
//import pro.shushi.pamirs.grouping.model.TableGroupingWrapper;
//import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
//import pro.shushi.pamirs.meta.common.spi.SPI;
//
//import java.util.List;
//
///**
// * 唯一值占比
// *
// * @author Gesi at 9:39 on 2025/9/9
// */
//@SPI.Service("UNIQUE_PERCENT")
//@Component
//@Slf4j
//public class GroupUniquePercentStatistic extends GroupUniqueStatistic implements GroupStatisticApi {
//
//    @Override
//    public Object statistic(TableGroupingWrapper group, GroupingData groupInfo, String statisticField, List<?> dataList) {
//        long total = total(dataList);
//        if (total == 0) {
//            return 0;
//        }
//        return String.format("%.2f", ((double) super.statistic(group, groupInfo, statisticField, dataList)) / total * 100);
//    }
//
//}
