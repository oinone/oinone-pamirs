package pro.shushi.pamirs.file.api.extpoint;

import pro.shushi.pamirs.core.common.function.FunctionConstant;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.SystemDirectiveEnum;
import pro.shushi.pamirs.meta.base.K2;
import pro.shushi.pamirs.meta.base.manager.data.DataManager;

/**
 * @author Adamancy Zhang on 2021-04-14 20:20
 */
public abstract class AbstractExcelImportDataExtPointImpl<T> implements ExcelImportDataExtPoint<T> {

    @Override
    public Boolean importData(ExcelImportContext importContext, T data) {
        DataManager dataManager = Models.data();
        T finalData;
        if (data instanceof K2) {
            finalData = ((K2) data).construct();
        } else {
            finalData = Fun.run(Models.api().getModel(data), FunctionConstant.construct, data);
        }
        Models.directive().runWithoutResult(() -> dataManager.createOrUpdateWithField(finalData),
                SystemDirectiveEnum.EXT_POINT,
                SystemDirectiveEnum.FROM_CLIENT,
                SystemDirectiveEnum.BUILT_ACTION);
        return true;
    }
}
