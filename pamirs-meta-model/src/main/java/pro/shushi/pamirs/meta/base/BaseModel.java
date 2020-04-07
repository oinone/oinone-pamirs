package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.MetaApiFactory;
import pro.shushi.pamirs.meta.api.core.compute.ExtPointApi;
import pro.shushi.pamirs.meta.api.core.orm.ConstructApi;
import pro.shushi.pamirs.meta.api.core.orm.ReadApi;
import pro.shushi.pamirs.meta.api.core.orm.WriteApi;
import pro.shushi.pamirs.meta.api.dto.crud.*;
import pro.shushi.pamirs.meta.enmu.FunctionUsageEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

/**
 * 无id模型定义，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.BaseModel")
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model(displayName = "模型抽象基类", summary = "模型抽象基类")
public class BaseModel extends AbstractModel implements ConstructApi, ReadApi, WriteApi {

    @Function.Advanced(usage = FunctionUsageEnum.READ)
    @Function(summary = "数据构造函数")
    public <T> T construct(T data){
        return MetaApiFactory.getApi(ConstructApi.class).construct(data);
    }

    @ExtPoint.Using({"createBefore","createAfter"})
    @Action(displayName = "添加")
    public <T> T create(T data){
        data = (T) MetaApiFactory.getApi(ExtPointApi.class).runDefault("createBefore", data);
        T result = MetaApiFactory.getApi(WriteApi.class).create(data);
        result = (T)MetaApiFactory.getApi(ExtPointApi.class).runDefault("createAfter", result);
        return result;
    }

    @ExtPoint.Using({"updateBefore","updateAfter"})
    @Action(displayName = "修改")
    public <T> T update(T data){
        data = (T) MetaApiFactory.getApi(ExtPointApi.class).runDefault("updateBefore", data);
        T result = MetaApiFactory.getApi(WriteApi.class).update(data);
        result = (T)MetaApiFactory.getApi(ExtPointApi.class).runDefault("updateAfter", result);
        return result;
    }

    @ExtPoint.Using({"updateConditionBefore","updateConditionAfter"})
    @Function.fun("updateCondition")
    @Function(name = "updateCondition")
    public <T> T update(UpdateCondition<T> condition){
        condition = (UpdateCondition<T>) MetaApiFactory.getApi(ExtPointApi.class).runDefault("updateConditionBefore", condition);
        T result = MetaApiFactory.getApi(WriteApi.class).update(condition);
        result = (T)MetaApiFactory.getApi(ExtPointApi.class).runDefault("updateConditionAfter", result);
        return result;
    }

    @ExtPoint.Using({"createBatchBefore","createBatchAfter"})
    @Function.fun("createBatch")
    @Function(name = "createBatch")
    public <T> List<T> create(List<T> dataList){
        dataList = (List<T>) MetaApiFactory.getApi(ExtPointApi.class).runDefault("createBatchBefore", dataList);
        List<T> result = MetaApiFactory.getApi(WriteApi.class).create(dataList);
        result = (List<T>)MetaApiFactory.getApi(ExtPointApi.class).runDefault("createBatchAfter", result);
        return result;
    }

    @ExtPoint.Using({"updateBatchBefore","updateBatchAfter"})
    @Function.fun("updateBatch")
    @Function(name = "updateBatch")
    public <T> List<T> update(List<T> dataList){
        dataList = (List<T>) MetaApiFactory.getApi(ExtPointApi.class).runDefault("updateBatchBefore", dataList);
        List<T> result = MetaApiFactory.getApi(WriteApi.class).update(dataList);
        result = (List<T>)MetaApiFactory.getApi(ExtPointApi.class).runDefault("updateBatchAfter", result);
        return result;
    }

    @ExtPoint.Using({"deleteBefore"})
    @Action(displayName = "删除")
    public Boolean delete(Condition condition){
        condition = (Condition) MetaApiFactory.getApi(ExtPointApi.class).runDefault("deleteBefore", condition);
        return MetaApiFactory.getApi(WriteApi.class).delete(condition);
    }

    @ExtPoint.Using({"queryOneBefore","queryOneAfter"})
    @Function.Advanced(usage = FunctionUsageEnum.READ)
    @Function
    public <T> T queryOne(T query){
        query = (T)MetaApiFactory.getApi(ExtPointApi.class).runDefault("queryOneBefore", query);
        T result = MetaApiFactory.getApi(ReadApi.class).queryOne(query);
        result = (T)MetaApiFactory.getApi(ExtPointApi.class).runDefault("queryOneAfter", result);
        return result;
    }

    @ExtPoint.Using({"queryListBefore","queryListAfter"})
    @Function.Advanced(usage = FunctionUsageEnum.READ)
    @Function
    public <T> List<T> queryList(PageCondition<T> condition){
        condition = (PageCondition) MetaApiFactory.getApi(ExtPointApi.class).runDefault("queryListBefore", condition);
        List<T> result = MetaApiFactory.getApi(ReadApi.class).queryList(condition);
        result = (List<T>)MetaApiFactory.getApi(ExtPointApi.class).runDefault("queryListAfter", result);
        return result;
    }

    @ExtPoint.Using({"queryPageBefore","queryPageAfter"})
    @Function.Advanced(usage = FunctionUsageEnum.READ)
    @Function
    public <T> Page<T> queryPage(PageCondition<T> condition){
        condition = (PageCondition) MetaApiFactory.getApi(ExtPointApi.class).runDefault("queryPageBefore", condition);
        Page<T> result = MetaApiFactory.getApi(ReadApi.class).queryPage(condition);
        result = (Page<T>)MetaApiFactory.getApi(ExtPointApi.class).runDefault("queryPageAfter", result);
        return result;
    }

    @ExtPoint.Using({"countBefore"})
    @Function.Advanced(usage = FunctionUsageEnum.READ)
    @Function
    public <T> Long count(Condition<T> condition){
        condition = (QueryCondition) MetaApiFactory.getApi(ExtPointApi.class).runDefault("countBefore", condition);
        Long result = MetaApiFactory.getApi(ReadApi.class).count(condition);
        return result;
    }

}
