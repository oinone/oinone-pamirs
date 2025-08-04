package pro.shushi.pamirs.file.api.extpoint.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelExportFetchDataExtPointImpl;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.faas.hook.HookApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.SystemDirectiveEnum;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.manager.data.DataManager;
import pro.shushi.pamirs.meta.constant.FunctionConstants;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 所有逻辑迁移到DefaultExcelExportFetchDataExtPoint
 */
@Ext(ExcelExportTask.class)
@Component
public class ExcelExportSameQueryPageTemplate<T> extends AbstractExcelExportFetchDataExtPointImpl {

    @Resource
    private HookApi hookApi;

    @Override
    protected List<Object> queryList(ExcelExportTask exportTask, ExcelDefinitionContext context, EasyExcelBlockDefinition blockDefinition, IWrapper<?> wrapper) {
        return Models.directive().run(() -> {
            ExcelWorkbookDefinition workbookDefinition = exportTask.getWorkbookDefinition();
            List<Object> result = new ArrayList<>();
            DataManager dataManager = Models.data();
            boolean clearExportStyle = this.fetchClearExportStyle(workbookDefinition);
            int maxSupportLength = this.fetchMaxSupportLength(workbookDefinition, clearExportStyle);
            hookApi.before(wrapper.getModel(), FunctionConstants.count, wrapper);
            Long count = this.count(wrapper, workbookDefinition.getModel());
            if (count > (long) maxSupportLength) {
                this.addUnsupportedErrorMessage(exportTask, maxSupportLength, clearExportStyle);
                return null;
            } else {
                List<Object> list = this.queryPageAll(count, wrapper, workbookDefinition.getModel());
                Pagination<Object> pagination = new Pagination<>();
                pagination.setContent(list);
                if (CollectionUtils.isNotEmpty(list)) {
                    if (list.size() > maxSupportLength) {
                        this.addUnsupportedErrorMessage(exportTask, maxSupportLength, clearExportStyle);
                        return null;
                    }
                    hookApi.after(wrapper.getModel(), FunctionConstants.queryPage, pagination);
                    result.add(list);
                    boolean isSuccess = Models.directive().run(() ->
                            this.selectRelationField(dataManager,
                                    blockDefinition.getBindingModel(),
                                    blockDefinition.getFieldNodeList(),
                                    list,
                                    maxSupportLength,
                                    list.size()), SystemDirectiveEnum.BUILT_ACTION, SystemDirectiveEnum.HOOK);
                    if (!isSuccess) {
                        this.addUnsupportedErrorMessage(exportTask, maxSupportLength, clearExportStyle);
                        return null;
                    }
                }
                return result;
            }
        });
    }

    @ExtPoint.Implement(
            priority = 998
    )
    @Override
    public List<Object> fetchExportData(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        return super.fetchExportData(exportTask, context);
    }


    private String getTemplateKey(String model, String templateName) {
        return model + "-" + templateName;
    }

    private Long count(IWrapper<?> wrapper, String model) {
        // 自定义的Action可能存在调用ArgUtils把模型转换问题(代理模型->存储模型)
        wrapper.setModel(model);
        Pagination<?> page = new Pagination<>();
        page.setCurrentPage(1);
        page.setModel(model);
        page.setSize(1L);
        Pagination<T> result = (Pagination) Fun.run(wrapper.getModel(), FunctionConstants.queryPage, new Object[]{page, wrapper});
        return result.getTotalElements();
    }

    private List<Object> queryPageAll(Long count, IWrapper<?> wrapper, String model) {
        // 自定义的Action可能存在调用ArgUtils把模型转换问题(代理模型->存储模型)
        wrapper.setModel(model);
        List<Object> content = new ArrayList<>();
        Pagination<?> page = new Pagination<>();
        page.setCurrentPage(1);
        page.setModel(model);
        //导出数据
        int pageNos = (int) Math.ceil(count / page.getSize()) + 1;
        for (Integer i = 1; i <= pageNos; i++) {
            page.setCurrentPage(i);
            PamirsSession.directive().disableHook();
            Pagination<T> result = (Pagination) Fun.run(wrapper.getModel(), FunctionConstants.queryPage, new Object[]{page, wrapper});
            List<T> data = result.getContent();
            content.addAll(data);
        }
        return content;
    }
}