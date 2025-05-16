package pro.shushi.pamirs.framework.compute.retry;

import org.apache.commons.collections4.ListUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 重试管理器
 * <p>解决循环计算问题
 * 2021/5/24 1:30 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class RetryManager {

    private final static ThreadLocal<RetryItem> retryItemThreadLocal = new ThreadLocal<>();

    public static RetryItem init() {
        RetryItem retryItem = retryItemThreadLocal.get();
        if (null == retryItem) {
            retryItem = new RetryItem().setRetryList(new ArrayList<>());
            retryItemThreadLocal.set(retryItem);
        }
        return retryItem;
    }

    public static void retry(String fireModel, ModelDefinition retryModelDefinition) {
        RetryItem retryItem = retryItemThreadLocal.get();
        int index = ListUtils.indexOf(retryItem.getRetryList(), v -> fireModel.equals(v.getModel()));
        if (-1 == index) {
            retryItem.getRetryList().add(retryModelDefinition);
        } else {
            retryItem.getRetryList().add(index, retryModelDefinition);
        }
    }

    public static void fire(Consumer<ModelDefinition> consumer) {
        RetryItem retryItem = retryItemThreadLocal.get();
        retryItem.setRetry(true);
        List<ModelDefinition> retryList = retryItem.getRetryList();
        if (!CollectionUtils.isEmpty(retryList)) {
            for (ModelDefinition modelDefinition : retryList) {
                consumer.accept(modelDefinition);
            }
        }
    }

    public static boolean isRetry() {
        return retryItemThreadLocal.get().isRetry();
    }

    public static void clear() {
        retryItemThreadLocal.remove();
    }
}
