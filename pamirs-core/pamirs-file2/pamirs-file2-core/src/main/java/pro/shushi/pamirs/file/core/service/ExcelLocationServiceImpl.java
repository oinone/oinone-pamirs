package pro.shushi.pamirs.file.core.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.standard.service.impl.AbstractStandardModelService;
import pro.shushi.pamirs.file.api.model.ExcelLocation;
import pro.shushi.pamirs.file.api.service.ExcelLocationService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * Excel国际化配置服务
 *
 * @author Adamancy Zhang at 17:50 on 2024-06-01
 */
@Service
@Fun(ExcelLocationService.FUN_NAMESPACE)
public class ExcelLocationServiceImpl extends AbstractStandardModelService<ExcelLocation> implements ExcelLocationService {

    @Function
    @Override
    public ExcelLocation create(ExcelLocation data) {
        return super.create(data);
    }

    @Function
    @Override
    public ExcelLocation update(ExcelLocation data) {
        return super.update(data);
    }

    @Function
    @Override
    public Integer updateByWrapper(ExcelLocation data, LambdaUpdateWrapper<ExcelLocation> wrapper) {
        return super.updateByWrapper(data, wrapper);
    }

    @Function
    @Override
    public ExcelLocation createOrUpdate(ExcelLocation data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<ExcelLocation> createOrUpdateBatch(List<ExcelLocation> list) {
        Models.origin().createOrUpdateBatch(list);
        return list;
    }

    @Function
    @Override
    public List<ExcelLocation> delete(List<ExcelLocation> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public ExcelLocation deleteOne(ExcelLocation data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<ExcelLocation> wrapper) {
        return super.deleteByWrapper(wrapper);
    }

    @Function
    @Override
    public Pagination<ExcelLocation> queryPage(Pagination<ExcelLocation> page, LambdaQueryWrapper<ExcelLocation> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public ExcelLocation queryOne(ExcelLocation query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public ExcelLocation queryOneByWrapper(LambdaQueryWrapper<ExcelLocation> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<ExcelLocation> queryListByWrapper(LambdaQueryWrapper<ExcelLocation> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<ExcelLocation> queryWrapper) {
        return super.count(queryWrapper);
    }
}
