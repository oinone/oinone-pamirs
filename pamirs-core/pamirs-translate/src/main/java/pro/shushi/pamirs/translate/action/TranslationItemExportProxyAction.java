package pro.shushi.pamirs.translate.action;

import pro.shushi.pamirs.locale.utils.I18nUtils;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.action.AbstractExcelExportTaskAction;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.service.ExcelFileService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.tmodel.ConditionWrapper;
import pro.shushi.pamirs.translate.enmu.TranslateEnumerate;
import pro.shushi.pamirs.translate.proxy.TranslationItemExportProxy;
import pro.shushi.pamirs.translate.template.TranslateTemplate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@Model.model(TranslationItemExportProxy.MODEL_MODEL)
public class TranslationItemExportProxyAction extends AbstractExcelExportTaskAction<ExcelExportTask> {

    @Autowired
    ExcelFileService excelFileService;

    public TranslationItemExportProxyAction(ExcelFileService excelFileService) {
        super(excelFileService);
    }

    @Function(openLevel = FunctionOpenEnum.API, summary = "导出翻译文件构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public TranslationItemExportProxy construct(TranslationItemExportProxy data) {
        TranslationItemExportProxy translationItemExportProxy = data.construct();
        translationItemExportProxy.setState(null);
        return translationItemExportProxy;
    }

    @Action(displayName = "导出翻译文件", bindingType = {ViewTypeEnum.TABLE}, contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Action.Advanced(type = FunctionTypeEnum.QUERY)
    public TranslationItemExportProxy export(TranslationItemExportProxy data) {
        Optional.ofNullable(data.getIsTranslate())
                .orElseThrow(() -> PamirsException.construct(TranslateEnumerate.TRANSLATION_MUST_BE_SELECTED_DURING_THE_INITIAL_TRANSLATION).errThrow());

        String rsql = ObjectToRsqlConverter.convertToRsql(data);
        String jsonString = JSON.toJSONString(data);
        Map map = JSON.parseObject(jsonString, Map.class);
        ConditionWrapper conditionWrapper = new ConditionWrapper().setQueryData(map).setRsql(rsql);
        ExcelExportTask excelExportTask = (ExcelExportTask) new ExcelExportTask()
                .setConditionWrapper(conditionWrapper)
                .setWorkbookDefinition(new ExcelWorkbookDefinition().setName(TranslateTemplate.TEMPLATE_NAME).setModel(ResourceTranslation.MODEL_MODEL));
        super.createExportTask(excelExportTask);

        PamirsSession.getMessageHub().msg(Message.init().setLevel(InformationLevelEnum.SUCCESS).setMessage(I18nUtils.getMessage("pamirs-translate.TranslationItemExportProxyAction.exportSuccessfulTheFileHasBeen")));
        return data;
    }

    @Override
    protected void doExport(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        excelFileService.doExportAsync(exportTask, context);
    }

    private static final class ObjectToRsqlConverter {
        //排除的字段数组
        private static String[] excludes = {"MODEL_MODEL"};

        /**
         * 将对象的非空属性（排除特定字段）转换为 RSQL 查询字符串。
         *
         * @param obj 任意对象
         * @return RSQL 格式的查询字符串
         */
        public static String convertToRsql(TranslationItemExportProxy obj) {
            List<String> conditions = new ArrayList<>();
            List<Field> fields = getAllFields(new ArrayList<>(), obj.getClass());

            try {
                for (Field field : fields) {
                    if (isFieldExcluded(field.getName(), excludes)) {
                        continue; // 如果字段在排除列表中，跳过处理
                    }
                    field.setAccessible(true); // 允许访问私有字段
                    Object value = obj.get_d().get(field.getName());

                    if (value != null) {
                        String condition = formatRsqlCondition(field.getName(), value);
                        conditions.add(condition);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return java.lang.String.join(" and ", conditions);
        }

        /**
         * 递归获取所有字段，包括父类字段。
         *
         * @param fields 存放字段的列表
         * @param type   类型
         * @return 字段列表
         */
        private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
            if (type != null && type != Object.class) {
                Field[] declaredFields = type.getDeclaredFields();
                for (Field field : declaredFields) {
                    if (!fields.contains(field)) {
                        fields.add(field);
                    }
                }
                getAllFields(fields, type.getSuperclass());
            }
            return fields;
        }


        /**
         * 检查字段是否应被排除。
         *
         * @param fieldName     字段名
         * @param excludeFields 排除的字段数组
         * @return true 如果字段应被排除
         */
        private static boolean isFieldExcluded(String fieldName, String[] excludeFields) {
            for (String excludeField : excludeFields) {
                if (excludeField.equals(fieldName)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 格式化为 RSQL 条件字符串。
         *
         * @param fieldName 字段名
         * @param value     字段值
         * @return RSQL 条件字符串
         */
        private static String formatRsqlCondition(String fieldName, Object value) {
            if (value instanceof String) {
                return fieldName + "==\"" + value + "\"";
            } else {
                return fieldName + "==" + value;
            }
        }
    }
}

