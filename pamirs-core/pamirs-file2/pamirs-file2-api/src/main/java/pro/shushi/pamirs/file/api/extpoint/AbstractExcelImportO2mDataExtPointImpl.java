package pro.shushi.pamirs.file.api.extpoint;

import pro.shushi.pamirs.file.api.context.ExcelImportContext;

import java.util.List;

/**
 * @author Adamancy Zhang on 2021-04-14 20:20
 */
public abstract class AbstractExcelImportO2mDataExtPointImpl<T> implements ExcelImportO2mDataExtPoint<T> {

    @Override
    public List<T> importO2mData(ExcelImportContext importContext, List<T> dataList) {
        // 默认什么都不做
        return dataList;
    }
}
