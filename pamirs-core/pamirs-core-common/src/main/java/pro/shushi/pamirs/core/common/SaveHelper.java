package pro.shushi.pamirs.core.common;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 保存数据帮助类
 *
 * @author Adamancy Zhang at 19:22 on 2025-02-19
 */
@Slf4j
public class SaveHelper {

    private SaveHelper() {
        // reject create object
    }

    /**
     * 批量创建或更新数据
     *
     * @param list 数据集
     * @param <T>  任意类型
     * @return 保存成功的数据集
     */
    public static <T extends D> List<T> createOrUpdateBatch(List<T> list) {
        List<List<T>> saveGroups = DataShardingHelper.build().sharding(list);
        List<T> retryItems = new ArrayList<>();
        List<T> results = new ArrayList<>();
        for (List<T> saveGroup : saveGroups) {
            try {
                Models.origin().createOrUpdateBatch(saveGroup);
                results.addAll(saveGroup);
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn("create or update error.", e);
                }
                retryItems.addAll(saveGroup);
            }
        }
        for (T item : retryItems) {
            try {
                Models.origin().createOrUpdate(item);
                results.add(item);
            } catch (Throwable e) {
                if (log.isErrorEnabled()) {
                    log.error("create or update error. data: {}", JsonUtils.toJSONString(item), e);
                }
            }
        }
        return results;
    }
}
