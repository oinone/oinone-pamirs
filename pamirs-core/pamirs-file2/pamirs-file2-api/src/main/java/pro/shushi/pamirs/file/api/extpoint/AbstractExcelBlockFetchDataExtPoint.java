package pro.shushi.pamirs.file.api.extpoint;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.ExcelExportFetchDataContext;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.faas.hook.HookApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.SystemDirectiveEnum;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.fun.Arg;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.constant.SqlConstants;
import pro.shushi.pamirs.core.common.tmodel.CommonConditionWrapper;
import pro.shushi.pamirs.resource.api.tmodel.ConditionWrapper;

import javax.annotation.Resource;
import java.util.*;

/**
 * 抽象Excel区块获取数据扩展点
 *
 * @author Adamancy Zhang at 16:40 on 2024-03-28
 */
@Slf4j
public abstract class AbstractExcelBlockFetchDataExtPoint<T> extends AbstractExcelExportExtPoint implements ExcelBlockFetchDataExtPoint {

    @Resource
    private HookApi hookApi;

    @Override
    public Object fetchExportData(ExcelExportFetchDataContext context) {
        return rawFetchExportData(context);
    }

    private Object rawFetchExportData(ExcelExportFetchDataContext context) {
        return Models.directive().run(() -> {
            IWrapper<T> wrapper = generatorWrapper(context);
            if (wrapper == null) {
                return null;
            }
            String modelModel = wrapper.getModel();
            if (StringUtils.isBlank(modelModel)) {
                modelModel = getBindingModel(context);
                wrapper.setModel(modelModel);
            }

            Pagination<T> pagination = generatorPagination(context);
            pagination.setModel(modelModel);

            Function function = getBlockFetchFunction(context);
            Object result;
            if (function == null) {
                result = fetchExportDataByDefault(context, modelModel, pagination, wrapper);
            } else {
                result = fetchExportDataByFunction(function, pagination, wrapper);
            }
            return result;
        });
    }

    private Object fetchExportDataByDefault(ExcelExportFetchDataContext context, String modelModel, Pagination<T> pagination, IWrapper<T> wrapper) {
        hookApi.before(wrapper.getModel(), FunctionConstants.queryPage, pagination, wrapper);
        List<T> results = queryData(context, modelModel, pagination, wrapper);
        hookApi.after(wrapper.getModel(), FunctionConstants.queryPage, new Pagination<T>().setModel(modelModel).setContent(results));
        queryAfter(context, results);
        return results;
    }

    private Object fetchExportDataByFunction(Function function, Pagination<T> pagination, IWrapper<T> wrapper) {
        return genericFetchExportData(function.getNamespace(), function.getFun(),
                () -> {
                    List<Arg> args = function.getArguments();
                    if (CollectionUtils.isEmpty(args)) {
                        return new Object[0];
                    }
                    List<Object> arguments = new ArrayList<>();
                    for (Arg arg : args) {
                        if (Pagination.MODEL_MODEL.equals(arg.getModel())) {
                            arguments.add(pagination);
                        } else if (IWrapper.MODEL_MODEL.equals(arg.getModel())) {
                            arguments.add(wrapper);
                        } else {
                            try {
                                arguments.add(Models.orm().objecting(arg.getModel(), new HashMap<>(2)));
                            } catch (Throwable e) {
                                arguments.add(null);
                                log.error("Model objecting error.", e);
                            }
                        }
                    }
                    return arguments.toArray(new Object[0]);
                },
                (args) -> Fun.run(function, args));
    }

//    private Object fetchExportDataByFunction(ExcelExportFetchDataContext context, Function function, Pagination<T> pagination, IWrapper<T> wrapper) {
//        hookApi.before(function.getNamespace(), function.getFun(), pagination, wrapper);
//        Object result = Fun.run(function, pagination, wrapper);
//        Object prepareResult = result;
//        Collection<?> queryAfterResult;
//        if (result instanceof Pagination) {
//            Pagination<?> resultPagination = (Pagination<?>) result;
//            Pagination<?> preparePagination = new Pagination<>();
//            preparePagination.setModel(resultPagination.getModel());
//            preparePagination.setContent(FetchUtil.cast(resultPagination.getContent()));
//            prepareResult = preparePagination;
//            queryAfterResult = resultPagination.getContent();
//        } else if (result instanceof Collection) {
//            queryAfterResult = new ArrayList<>((Collection<?>) result);
//        } else {
//            queryAfterResult = Lists.newArrayList(result);
//        }
//        hookApi.after(function.getNamespace(), function.getFun(), prepareResult);
//        if (queryAfterResult != null) {
//            queryAfter(context, queryAfterResult);
//        }
//        return result;
//    }

    protected List<T> queryData(ExcelExportFetchDataContext context, String modelModel, Pagination<T> pagination, IWrapper<T> wrapper) {
        ExcelExportTask exportTask = context.getExportTask();
        ExcelWorkbookDefinition workbookDefinition = exportTask.getWorkbookDefinition();
        boolean clearExportStyle = this.fetchClearExportStyle(workbookDefinition);
        int maxSupportLength = this.fetchMaxSupportLength(workbookDefinition, clearExportStyle);
        Pagination<T> firstPage = queryFirstPage(pagination, wrapper);
        Long count = firstPage.getTotalElements();
        if (count > (long) maxSupportLength) {
            this.addUnsupportedErrorMessage(exportTask, maxSupportLength, clearExportStyle);
            return null;
        }
        return queryAllPages(modelModel, firstPage, wrapper);
    }

    protected String getBindingModel(ExcelExportFetchDataContext context) {
        return context.getBlockDefinition().getBindingModel();
    }

    protected Function getBlockFetchFunction(ExcelExportFetchDataContext context) {
        EasyExcelBlockDefinition blockDefinition = context.getBlockDefinition();
        String namespace = Optional.ofNullable(blockDefinition.getFetchNamespace())
                .filter(StringUtils::isNotBlank)
                .orElse(null);
        String fun = Optional.ofNullable(blockDefinition.getFetchFun())
                .filter(StringUtils::isNotBlank)
                .orElse(null);
        Function function = null;
        if (StringUtils.isNoneBlank(namespace, fun)) {
            function = PamirsSession.getContext().getFunction(namespace, fun);
        }
        return function;
    }

    protected IWrapper<T> generatorWrapper(ExcelExportFetchDataContext context) {
        boolean isFirst = context.getResults().isEmpty();
        ExcelExportTask exportTask = context.getExportTask();
        ExcelWorkbookDefinition workbookDefinition = exportTask.getWorkbookDefinition();
        EasyExcelBlockDefinition blockDefinition = context.getBlockDefinition();
        IWrapper<T> wrapper;
        if (isFirst) {
            wrapper = exportTask.temporaryRsql(workbookDefinition.getDomain(), () -> Optional.ofNullable(exportTask.getConditionWrapper())
                    .map(ConditionWrapper::<T>generatorQueryWrapper)
                    .orElseGet(() -> Pops.<T>query().ge(SqlConstants.ID, 0)));
        } else {
            String domain = blockDefinition.getDomain();
            if (StringUtils.isBlank(domain)) {
                wrapper = Pops.<T>query().ge(SqlConstants.ID, 0);
            } else {
                wrapper = Pops.<T>query().setRsql(domain);
            }
        }
        if (!prepareWrapper(context, wrapper)) {
            return null;
        }
        return wrapper;
    }

    protected Pagination<T> generatorPagination(ExcelExportFetchDataContext context) {
        return new Pagination<>();
    }

    protected Pagination<T> queryFirstPage(Pagination<T> pagination, IWrapper<T> wrapper) {
        return queryPage(pagination, wrapper);
    }

    protected List<T> queryAllPages(String model, Pagination<T> firstPage, IWrapper<T> wrapper) {
        List<T> results = firstPage.getContent();
        Integer totalPages = firstPage.getTotalPages();
        if (totalPages == null || totalPages <= 1) {
            return results;
        }
        for (int i = 2; i <= totalPages; i++) {
            Pagination<T> pagination = generatorNextPagination(firstPage, model, i);
            wrapper.setModel(model);
            pagination = queryPage(pagination, wrapper);
            List<T> content = pagination.getContent();
            if (CollectionUtils.isNotEmpty(content)) {
                results.addAll(content);
            }
        }
        return results;
    }

    protected Pagination<T> generatorNextPagination(Pagination<T> firstPage, String model, int currentPage) {
        Pagination<T> pagination = new Pagination<>();
        pagination.setModel(model);
        pagination.setCurrentPage(currentPage);
        pagination.setSize(firstPage.getSize());
        pagination.setGroupBy(firstPage.getGroupBy());
        pagination.setSort(firstPage.getSort());
        pagination.setSortable(firstPage.getSortable());
        return pagination;
    }

    protected Pagination<T> queryPage(Pagination<T> pagination, IWrapper<T> wrapper) {
        return Models.data().queryPage(pagination, wrapper);
    }

    /**
     * 预处理QueryWrapper
     *
     * @param context 上下文
     * @param wrapper wrapper
     * @return 是否继续
     */
    protected boolean prepareWrapper(ExcelExportFetchDataContext context, IWrapper<?> wrapper) {
        return true;
    }

    /**
     * 查询后
     *
     * @param context 上下文
     * @param results 结果集
     */
    protected void queryAfter(ExcelExportFetchDataContext context, Collection<?> results) {
        ExcelExportTask exportTask = context.getExportTask();
        ExcelWorkbookDefinition workbookDefinition = exportTask.getWorkbookDefinition();
        boolean clearExportStyle = this.fetchClearExportStyle(workbookDefinition);
        int maxSupportLength = this.fetchMaxSupportLength(workbookDefinition, clearExportStyle);
        EasyExcelBlockDefinition blockDefinition = context.getBlockDefinition();
        boolean isSuccess = Models.directive().run(() ->
                this.selectRelationField(Models.data(),
                        blockDefinition.getBindingModel(),
                        blockDefinition.getFieldNodeList(),
                        results,
                        maxSupportLength,
                        results.size()), SystemDirectiveEnum.BUILT_ACTION, SystemDirectiveEnum.HOOK);
        if (!isSuccess) {
            this.addUnsupportedErrorMessage(exportTask, maxSupportLength, clearExportStyle);
        }
    }
}
