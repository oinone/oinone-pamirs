package pro.shushi.pamirs.file.api.extpoint;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.enmu.FileExpEnumerate;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.entity.ExcelExportFetchDataContext;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.SystemDirectiveEnum;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.SqlConstants;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.resource.api.tmodel.ConditionWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Adamancy Zhang on 2021-03-31 09:54
 */
public abstract class AbstractExcelExportFetchDataExtPointImpl extends AbstractExcelExportExtPoint implements ExcelExportFetchDataExtPoint {

    /**
     * 预处理wrapper
     *
     * @param context 上下文
     * @return 是否继续
     */
    protected boolean prepareWrapper(ExcelExportFetchDataContext context) {
        return true;
    }

    /**
     * 查询前处理wrapper
     *
     * @param context 上下文
     * @param wrapper {@link QueryWrapper}
     * @return 是否继续
     */
    protected IWrapper<?> queryBefore(ExcelExportFetchDataContext context, IWrapper<?> wrapper) {
        return wrapper;
    }

    /**
     * 查询数据
     *
     * @param exportTask      导出任务
     * @param context         上下文
     * @param blockDefinition 当前块
     * @param wrapper         {@link QueryWrapper}
     * @return 块数据列表，每一个块中的所有数据为列表中的一项
     */
    protected List<Object> queryList(ExcelExportTask exportTask, ExcelDefinitionContext context, EasyExcelBlockDefinition blockDefinition, IWrapper<?> wrapper) {
        ExcelWorkbookDefinition workbookDefinition = exportTask.getWorkbookDefinition();
        List<Object> result = new ArrayList<>();
        boolean clearExportStyle = fetchClearExportStyle(workbookDefinition);
        int maxSupportLength = fetchMaxSupportLength(workbookDefinition, clearExportStyle);
        Long count = this.rawCount(wrapper);
        if (count > maxSupportLength) {
            addUnsupportedErrorMessage(exportTask, maxSupportLength, clearExportStyle);
            return null;
        }
        List<?> list = this.rawQueryList(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() > maxSupportLength) {
                addUnsupportedErrorMessage(exportTask, maxSupportLength, clearExportStyle);
                return null;
            }
            result.add(list);
            if (!selectRelationField(Models.data(), blockDefinition.getBindingModel(), blockDefinition.getFieldNodeList(), list, maxSupportLength, list.size())) {
                addUnsupportedErrorMessage(exportTask, maxSupportLength, clearExportStyle);
                return null;
            }
        }
        return result;
    }

    protected Long rawCount(IWrapper<?> wrapper) {
        return Models.data().count(wrapper);
    }

    protected List<?> rawQueryList(IWrapper<?> wrapper) {
        return Models.data().queryListByWrapper(wrapper);
    }

    /**
     * 查询后处理结果集
     *
     * @param context 上下文
     * @param result  结果集
     * @return 是否继续
     */
    protected boolean queryAfter(ExcelExportFetchDataContext context, List<?> result) {
        return true;
    }

    /**
     * 获取导出数据
     *
     * @param exportTask 导出任务
     * @param context    Excel定义上下文
     * @return 结果集，如果需要中断，请返回null
     */
    @Override
    public List<Object> fetchExportData(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        return Models.directive().run(() -> {
            //fixme zbh 第一阶段仅实现单区块
            EasyExcelSheetDefinition sheetDefinition = context.getSheetList().get(0);
            EasyExcelBlockDefinition blockDefinition = sheetDefinition.getBlockDefinitions().get(0);
            String modelModel = blockDefinition.getBindingModel();
            ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(modelModel);
            ModelTypeEnum modelType = modelConfig.getType();
            if (!ModelTypeEnum.STORE.equals(modelType) && !ModelTypeEnum.PROXY.equals(modelType)) {
                throw PamirsException.construct(FileExpEnumerate.EXPORT_MODEL_NOT_ALLOWED).errThrow();
            }
            ExcelWorkbookDefinition workbookDefinition = exportTask.getWorkbookDefinition();
            ExcelExportFetchDataContext fetchDataContext = new ExcelExportFetchDataContext(exportTask, context, sheetDefinition, blockDefinition);
            boolean isContinue;
            isContinue = prepareWrapper(fetchDataContext);
            if (!isContinue) {
                return null;
            }
            IWrapper<?> wrapper = exportTask.temporaryRsql(workbookDefinition.getDomain(), () -> Optional.ofNullable(exportTask.getConditionWrapper())
                    .map(ConditionWrapper::generatorQueryWrapper)
                    .orElseGet(() -> Pops.query().ge(SqlConstants.ID, 0)));
            wrapper.setModel(modelModel);
            wrapper = queryBefore(fetchDataContext, wrapper);
            if (wrapper == null) {
                return null;
            }
            List<Object> result = queryList(exportTask, context, blockDefinition, wrapper);
            isContinue = queryAfter(fetchDataContext, result);
            if (!isContinue) {
                return null;
            }
            return result;
        }, SystemDirectiveEnum.BUILT_ACTION, SystemDirectiveEnum.HOOK);
    }
}
