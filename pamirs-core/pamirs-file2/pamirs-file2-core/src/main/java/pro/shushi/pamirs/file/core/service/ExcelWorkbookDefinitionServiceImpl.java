package pro.shushi.pamirs.file.core.service;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.standard.service.impl.AbstractStandardModelService;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.service.ExcelWorkbookDefinitionService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

@Fun(ExcelWorkbookDefinitionService.FUN_NAMESPACE)
@Component
public class ExcelWorkbookDefinitionServiceImpl extends AbstractStandardModelService<ExcelWorkbookDefinition> implements ExcelWorkbookDefinitionService {

    @Function
    @Override
    public ExcelWorkbookDefinition create(ExcelWorkbookDefinition data) {
        return super.create(data);
    }

    @Override
    public List<ExcelWorkbookDefinition> createBatch(List<ExcelWorkbookDefinition> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public ExcelWorkbookDefinition update(ExcelWorkbookDefinition data) {
        return super.update(data);
    }

    @Function
    @Override
    public Integer updateBatch(List<ExcelWorkbookDefinition> list) {
        return super.updateBatch(list);
    }

    @Function
    @Override
    public Integer updateByWrapper(ExcelWorkbookDefinition data, LambdaUpdateWrapper<ExcelWorkbookDefinition> wrapper) {
        return super.updateByWrapper(data, wrapper);
    }

    @Function
    @Override
    public ExcelWorkbookDefinition createOrUpdate(ExcelWorkbookDefinition data) {
        data = super.createOrUpdate(data);
        Models.origin().fieldSave(data, ExcelWorkbookDefinition::getLocations);
        return data;
    }

    @Function
    @Override
    public List<ExcelWorkbookDefinition> createOrUpdateBatch(List<ExcelWorkbookDefinition> list) {
        Models.origin().createOrUpdateBatch(list);
        list = Models.origin().listFieldSave(list, ExcelWorkbookDefinition::getLocations);
        return list;
    }

    @Function
    @Override
    public List<ExcelWorkbookDefinition> delete(List<ExcelWorkbookDefinition> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public ExcelWorkbookDefinition deleteOne(ExcelWorkbookDefinition data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<ExcelWorkbookDefinition> wrapper) {
        return super.deleteByWrapper(wrapper);
    }

    @Function
    @Override
    public Pagination<ExcelWorkbookDefinition> queryPage(Pagination<ExcelWorkbookDefinition> page, LambdaQueryWrapper<ExcelWorkbookDefinition> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public ExcelWorkbookDefinition queryOne(ExcelWorkbookDefinition query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public ExcelWorkbookDefinition queryOneByWrapper(LambdaQueryWrapper<ExcelWorkbookDefinition> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<ExcelWorkbookDefinition> queryListByWrapper(LambdaQueryWrapper<ExcelWorkbookDefinition> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<ExcelWorkbookDefinition> queryWrapper) {
        return super.count(queryWrapper);
    }
}
