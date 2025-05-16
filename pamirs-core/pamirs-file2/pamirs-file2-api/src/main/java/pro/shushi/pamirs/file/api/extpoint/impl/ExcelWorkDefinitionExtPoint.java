package pro.shushi.pamirs.file.api.extpoint.impl;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.file.api.model.ExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.base.extpoint.DefaultReadWriteExtPoint;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;

import java.util.List;
import java.util.Optional;

@Ext(ExcelWorkbookDefinition.class)
public class ExcelWorkDefinitionExtPoint extends DefaultReadWriteExtPoint<ExcelWorkbookDefinition> {

    @Override
    @ExtPoint.Implement
    public ExcelWorkbookDefinition createBefore(ExcelWorkbookDefinition data) {
        verificationAndSet(data, Boolean.FALSE);
        return data;
    }

    @Override
    @ExtPoint.Implement
    public ExcelWorkbookDefinition updateBefore(ExcelWorkbookDefinition data) {
        verificationAndSet(data, Boolean.TRUE);
        return data;
    }

    @Override
    @ExtPoint.Implement
    public List<ExcelWorkbookDefinition> deleteBefore(List<ExcelWorkbookDefinition> data) {
        //todo zbh 删除OSS上已上传的模板文件
        return data;
    }

    @Override
    @ExtPoint.Implement
    public Pagination<ExcelWorkbookDefinition> queryPageAfter(Pagination<ExcelWorkbookDefinition> page) {
        List<ExcelWorkbookDefinition> dataList = page.getContent();
        for (ExcelWorkbookDefinition item : dataList) {
            removeSheetDefinitionsScope(item);
        }
        return page;
    }

    @Override
    @ExtPoint.Implement
    public List<ExcelWorkbookDefinition> queryListAfter(List<ExcelWorkbookDefinition> dataList) {
        for (ExcelWorkbookDefinition item : dataList) {
            removeSheetDefinitionsScope(item);
        }
        return dataList;
    }

    @Override
    @ExtPoint.Implement
    public ExcelWorkbookDefinition queryOneAfter(ExcelWorkbookDefinition data) {
        removeSheetDefinitionsScope(data);
        return data;
    }

    private void removeSheetDefinitionsScope(ExcelWorkbookDefinition data) {
      Optional.ofNullable(data)
                .map(_data -> _data.getSheetDefinitions())
                .filter(StringUtils::isNotBlank)
                .map(_str->StringUtils.substring(_str, 32))
              .ifPresent(_str->data.setSheetDefinitions(_str));
    }

    private void verificationAndSet(ExcelWorkbookDefinition data, boolean isUpdate) {
        ExcelWorkbookDefinition origin = null;
        Long id = null;
        if (isUpdate) {
            origin = FetchUtil.fetchOne(data);
            if (origin == null) {
                throw PamirsException.construct(ExpEnumerate.SELECT_NULL).errThrow();
            }
            id = origin.getId();
        }
        //大区名称校验全局唯一
        String name = data.getName();
        if (StringUtils.isBlank(name)) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg("工作簿名称是必填项").errThrow();
        }
        //工作簿名称校验全局唯一
        LambdaQueryWrapper<ExcelWorkbookDefinition> wrapper = Pops.<ExcelWorkbookDefinition>lambdaQuery()
                .from(ExcelWorkbookDefinition.MODEL_MODEL)
                .eq(ExcelWorkbookDefinition::getName, name);
        if (isUpdate) {
            wrapper.ne(ExcelWorkbookDefinition::getId, id);
        }
        Long count = data.count(wrapper);
        if (count >= 1) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg("工作簿名称不允许重复").errThrow();
        }
        String sheetDefinitions = data.getSheetDefinitions();
        if (StringUtils.isBlank(sheetDefinitions)) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg("工作表定义不允许为空").errThrow();
        }
        if (!JSONArray.isValidArray(sheetDefinitions)) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg("工作表定义必须是JSON数组").errThrow();
        }
        List<ExcelSheetDefinition> sheetList;
        try {
            sheetList = JSONArray.parseArray(sheetDefinitions, ExcelSheetDefinition.class);
        } catch (Exception e) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR, e).appendMsg("工作表定义格式错误").errThrow();
        }
        if (CollectionUtils.isEmpty(sheetList)) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg("工作表定义必须不为空").errThrow();
        }
        data.setSheetList(sheetList);
        data.storeSheetDefinitions();
    }
}
