package pro.shushi.pamirs.file.api.service;

import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link ExcelWorkbookDefinition}服务
 *
 * @author Adamancy Zhang at 22:12 on 2024-06-01
 */
@Fun(ExcelWorkbookDefinitionService.FUN_NAMESPACE)
public interface ExcelWorkbookDefinitionService extends StandardModelService<ExcelWorkbookDefinition> {

    String FUN_NAMESPACE = "file.ExcelWorkbookDefinitionService";

    @Function
    @Override
    ExcelWorkbookDefinition create(ExcelWorkbookDefinition data);

    @Function
    List<ExcelWorkbookDefinition> createBatch(List<ExcelWorkbookDefinition> list);

    @Function
    @Override
    ExcelWorkbookDefinition update(ExcelWorkbookDefinition data);

    @Function
    Integer updateBatch(List<ExcelWorkbookDefinition> list);

    @Function
    @Override
    Integer updateByWrapper(ExcelWorkbookDefinition data, LambdaUpdateWrapper<ExcelWorkbookDefinition> wrapper);

    @Function
    @Override
    ExcelWorkbookDefinition createOrUpdate(ExcelWorkbookDefinition data);

    @Function
    List<ExcelWorkbookDefinition> createOrUpdateBatch(List<ExcelWorkbookDefinition> list);

    @Function
    @Override
    List<ExcelWorkbookDefinition> delete(List<ExcelWorkbookDefinition> list);

    @Function
    @Override
    ExcelWorkbookDefinition deleteOne(ExcelWorkbookDefinition data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<ExcelWorkbookDefinition> wrapper);

    @Function
    @Override
    Pagination<ExcelWorkbookDefinition> queryPage(Pagination<ExcelWorkbookDefinition> page, LambdaQueryWrapper<ExcelWorkbookDefinition> queryWrapper);

    @Function
    @Override
    ExcelWorkbookDefinition queryOneByWrapper(LambdaQueryWrapper<ExcelWorkbookDefinition> queryWrapper);

    @Function
    @Override
    List<ExcelWorkbookDefinition> queryListByWrapper(LambdaQueryWrapper<ExcelWorkbookDefinition> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<ExcelWorkbookDefinition> queryWrapper);
}
