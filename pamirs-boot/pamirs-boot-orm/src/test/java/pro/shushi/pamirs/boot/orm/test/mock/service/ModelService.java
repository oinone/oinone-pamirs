package pro.shushi.pamirs.boot.orm.test.mock.service;

import org.junit.Assert;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.domain.module.ModuleCategory;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.Date;
import java.util.List;

/**
 * 模型测试服务
 * <p>
 * 2020/7/30 3:35 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class ModelService<T> {

    private T entity;

    private T updateEntity;

    private T query;

    private List<T> dataList;

    private QueryWrapper<T> queryWrapper;

    private Pagination<T> page;

    public ModelService(T entity, T updateEntity, T query, List<T> dataList, QueryWrapper<T> queryWrapper, Pagination<T> page) {
        this.entity = entity;
        this.updateEntity = updateEntity;
        this.query = query;
        this.dataList = dataList;
        this.queryWrapper = queryWrapper;
        this.page = page;
    }

    //    @Transactional
    public void test() {
        /* write */
        boolean conflict = false;
        if (null != Models.data().queryOne(entity)) {
            conflict = true;
        }
        if (conflict) {
            Models.data().deleteByEntity(entity);
        }
        T result = Models.data().createOne(entity);
        Assert.assertNotEquals(null, result);

        FieldUtils.setFieldValue(entity, "name", "test" + System.nanoTime());
        Integer updateCount = Models.data().createOrUpdate(entity);
        Assert.assertEquals(Integer.valueOf(1), updateCount);
        FieldUtils.setFieldValue(entity, "id", null);
        FieldUtils.setFieldValue(entity, "description", "test" + System.nanoTime());
        updateCount = Models.data().createOrUpdate(entity);
        Assert.assertEquals(Integer.valueOf(1), updateCount);


        updateCount = Models.data().updateByPk(entity);
        Assert.assertEquals(Integer.valueOf(1), updateCount);
        FieldUtils.setFieldValue(entity, "name", "test" + new Date());
        updateCount = Models.data().updateByPk(entity);
        Assert.assertEquals(Integer.valueOf(1), updateCount);

        FieldUtils.setFieldValue(entity, "name", "test" + new Date());
        updateCount = Models.data().updateByUniqueField(entity);
        Assert.assertEquals(Integer.valueOf(1), updateCount);

        conflict = false;
        for (T data : dataList) {
            if (null != Models.data().queryOne(data)) {
                conflict = true;
            }
        }
        if (conflict) {
            Models.data().deleteByPks(dataList);
        }
        List<T> resultList = Models.data().createBatch(dataList);
        updateCount = Models.data().createOrUpdateBatch(dataList);
        int i = 0;
        for (T v : dataList) FieldUtils.setFieldValue(v, "name", "test-" + ++i);
        updateCount = Models.data().updateBatch(dataList);

        FieldUtils.setFieldValue(updateEntity, "name", "test0");
        updateCount = Models.data().updateByEntity(updateEntity, query);
        Assert.assertEquals(Integer.valueOf(1), updateCount);

        FieldUtils.setFieldValue(updateEntity, "name", "test" + System.nanoTime());
        updateCount = Models.data().updateByWrapper(updateEntity, queryWrapper);
        Assert.assertEquals(Integer.valueOf(1), updateCount);

        /* read */
        result = Models.data().queryByPk(query);
        result = Models.data().queryOne(entity);
        result = Models.data().queryOneByWrapper(queryWrapper);
        DataMap dataMap = Models.data().queryOneByWrapper(Pops.query().eq("name", "test").generic(Models.api().getModel(query), new DataMap()));
        resultList = Models.data().queryListByEntity(query);
        resultList = Models.data().queryListByWrapper(queryWrapper);
        resultList = Models.data().queryListByEntity(page, query);
        resultList = Models.data().queryListByWrapper(page, queryWrapper);
        Pagination<T> pageResult = Models.data().queryPage(page, queryWrapper);
        Long count = Models.data().count(query);
        count = Models.data().count(queryWrapper);

        /* field */
        Models.data().createOne(new ModuleDefinition().setModule("test1").setName("test1").setCategory(ModuleCategory.MODEL_MODEL + 1));
        Models.data().createOne(new ModuleDefinition().setModule("test2").setName("test2").setCategory(ModuleCategory.MODEL_MODEL + 1));
        Models.data().createOne(new ModuleDefinition().setModule("test3").setName("test3").setCategory(ModuleCategory.MODEL_MODEL + 2));

        result = Models.data().fieldQuery(entity, ModuleCategory::getModules);
        FieldUtils.setFieldValue(((List) FieldUtils.getFieldValue(result, "modules")).get(0), "url", "test");
        result = Models.data().fieldSave(entity, ModuleCategory::getModules);
        result = Models.data().relationDelete(entity, ModuleCategory::getModules);
        resultList = Models.data().listFieldQuery(dataList, ModuleCategory::getModules);
        resultList = Models.data().listFieldSave(dataList, ModuleCategory::getModules);
        resultList = Models.data().listRelationDelete(dataList, ModuleCategory::getModules);

        /* delete */
        Boolean deleteResult = Models.data().deleteByPk(entity);
        deleteResult = Models.data().deleteByPks(dataList);
        deleteResult = Models.data().deleteByUniqueField(entity);
        updateCount = Models.data().deleteByEntity(entity);
        updateCount = Models.data().deleteByWrapper(queryWrapper);
    }

}
