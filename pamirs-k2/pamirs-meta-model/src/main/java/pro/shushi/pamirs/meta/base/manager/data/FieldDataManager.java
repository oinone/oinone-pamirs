package pro.shushi.pamirs.meta.base.manager.data;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.clone.ReferenceUtils;
import pro.shushi.pamirs.meta.constant.FunctionConstants;

import java.util.List;

/**
 * 数据管理器
 * <p>
 * 2020/5/7 11:58 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class FieldDataManager implements FunctionConstants {

    // 数据管理器
    private volatile static FieldDataManager DATA_MANAGER;

    protected String getModel(Object... modelObject) {
        for (Object obj : modelObject) {
            String model = Models.api().getDataModel(obj);
            if (StringUtils.isNotBlank(model)) {
                return model;
            }
        }
        return null;
    }

    public static FieldDataManager getInstance() {
        if (null == DATA_MANAGER) {
            synchronized (Models.class) {
                if (null == DATA_MANAGER) {
                    DATA_MANAGER = new FieldDataManager();
                }
            }
        }
        return DATA_MANAGER;
    }

    public static OriginDataManager origin() {
        return Models.origin();
    }

    public <T> T createWithField(T data) {
        T result =  Models.directive().run(() -> Fun.run(getModel(data), create, data));
        ReferenceUtils.deal(result, data);
        return data;
    }

    public <T> List<T> createWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        List<T> result =  Models.directive().run(() -> Fun.run(getModel(dataList), createWithFieldBatch, dataList));
        ReferenceUtils.dealList(result, dataList);
        return dataList;
    }

    public <T> T updateWithField(T data) {
        origin().excludeList(data);
        T result =  Models.directive().run(() -> Fun.run(getModel(data), update, data));
        ReferenceUtils.deal(result, data);
        return data;
    }

    public <T> List<T> updateWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        List<T> result =  Models.directive().run(() -> Fun.run(getModel(dataList), updateWithFieldBatch, dataList));
        ReferenceUtils.dealList(result, dataList);
        return dataList;
    }

    public <T> T createOrUpdateWithField(T data) {
        origin().excludeList(data);
        T result =  Models.directive().run(() -> Fun.run(getModel(data), createOrUpdateWithField, data));
        ReferenceUtils.deal(result, data);
        return data;
    }

    public <T> List<T> createOrUpdateWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        List<T> result =  Models.directive().run(() -> Fun.run(getModel(dataList), createOrUpdateWithFieldBatch, dataList));
        ReferenceUtils.dealList(result, dataList);
        return dataList;
    }

    public <T> T deleteWithField(T data) {
        origin().excludeList(data);
        return  Models.directive().run(() -> Fun.run(getModel(data), delete, data));
    }

    public <T> List<T> deleteWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        return  Models.directive().run(() -> Fun.run(getModel(dataList), deleteWithFieldBatch, dataList));
    }

}
