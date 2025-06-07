package pro.shushi.pamirs.file.api.service;

import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.file.api.model.ExcelLocation;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * Excel国际化配置服务
 *
 * @author Adamancy Zhang at 17:48 on 2024-06-01
 */
@Fun(ExcelLocationService.FUN_NAMESPACE)
public interface ExcelLocationService extends StandardModelService<ExcelLocation> {

    String FUN_NAMESPACE = "file.ExcelLocationService";

    @Function
    @Override
    ExcelLocation create(ExcelLocation data);

    @Function
    @Override
    ExcelLocation update(ExcelLocation data);

    @Function
    @Override
    Integer updateByWrapper(ExcelLocation data, LambdaUpdateWrapper<ExcelLocation> wrapper);

    @Function
    @Override
    ExcelLocation createOrUpdate(ExcelLocation data);

    @Function
    List<ExcelLocation> createOrUpdateBatch(List<ExcelLocation> list);

    @Function
    @Override
    List<ExcelLocation> delete(List<ExcelLocation> list);

    @Function
    @Override
    ExcelLocation deleteOne(ExcelLocation data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<ExcelLocation> wrapper);

    @Function
    @Override
    Pagination<ExcelLocation> queryPage(Pagination<ExcelLocation> page, LambdaQueryWrapper<ExcelLocation> queryWrapper);

    @Function
    @Override
    ExcelLocation queryOne(ExcelLocation query);

    @Function
    @Override
    ExcelLocation queryOneByWrapper(LambdaQueryWrapper<ExcelLocation> queryWrapper);

    @Function
    @Override
    List<ExcelLocation> queryListByWrapper(LambdaQueryWrapper<ExcelLocation> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<ExcelLocation> queryWrapper);
}
