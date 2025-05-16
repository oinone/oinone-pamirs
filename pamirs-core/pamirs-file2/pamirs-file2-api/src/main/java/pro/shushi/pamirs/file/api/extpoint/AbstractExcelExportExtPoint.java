package pro.shushi.pamirs.file.api.extpoint;

import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.cache.MemoryIterableSearchCache;
import pro.shushi.pamirs.file.api.config.FileConstant;
import pro.shushi.pamirs.file.api.entity.EasyExcelCellDefinition;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.ExcelExportHelper;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.faas.hook.HookApi;
import pro.shushi.pamirs.meta.api.core.orm.ReadApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationReadApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 抽象Excel导出扩展点
 *
 * @author Adamancy Zhang at 15:12 on 2024-04-01
 */
public class AbstractExcelExportExtPoint {

    @Resource
    private HookApi hookApi;

    protected <R> R genericFetchExportData(String namespace, String fun, Object... parameters) {
        return genericFetchExportData(namespace, fun, () -> parameters, (args) -> Fun.run(namespace, fun, args));
    }

    protected <R> R genericFetchExportData(String namespace, String fun, Supplier<Object[]> supplierParameters, Function<Object[], R> fetch) {
        return Models.directive().run(() -> {
            Object[] parameters = supplierParameters.get();
            hookApi.before(namespace, fun, parameters);
            R result = fetch.apply(parameters);
            hookApi.after(namespace, fun, result);
            return result;
        });
    }

    protected boolean fetchClearExportStyle(ExcelWorkbookDefinition workbookDefinition) {
        return ExcelExportHelper.fetchClearExportStyle(workbookDefinition);
    }

    protected int fetchMaxSupportLength(ExcelWorkbookDefinition workbookDefinition, boolean clearExportStyle) {
        return ExcelExportHelper.fetchMaxSupportLength(workbookDefinition, clearExportStyle);
    }

    protected void addUnsupportedErrorMessage(ExcelExportTask exportTask, int maxSupportLength, boolean clearExportStyle) {
        ExcelExportHelper.addUnsupportedErrorMessage(exportTask, maxSupportLength, clearExportStyle);
    }

    protected <DM extends ReadApi> boolean selectRelationField(DM dataManager, String model, List<TreeNode<EasyExcelCellDefinition>> fieldNodeList, Collection<?> list, Integer maxSupportLength, int currentMaxLength) {
        Set<String> hasQueryFields = new HashSet<>();
        RelationReadApi relationManagerProcessor = CommonApiFactory.getApi(RelationReadApi.class);
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        if (modelConfig == null) {
            return true;
        }
        MemoryIterableSearchCache<String, ModelFieldConfig> modelFieldsCache = new MemoryIterableSearchCache<>(modelConfig.getModelFieldConfigList(), ModelFieldConfig::getLname);
        for (TreeNode<EasyExcelCellDefinition> fieldNode : fieldNodeList) {
            String fieldKey = fieldNode.getKey();
            TreeNode<EasyExcelCellDefinition> parentFieldNode = fieldNode.getParent();
            if (parentFieldNode != null) {
                fieldKey = fieldKey.substring(parentFieldNode.getKey().length() + 1);
                int pi = fieldKey.indexOf(FileConstant.POINT_CHARACTER);
                if (pi != -1) {
                    fieldKey = fieldKey.substring(0, pi);
                }
            }
            ModelFieldConfig modelFieldConfig = modelFieldsCache.get(fieldKey);
            if (modelFieldConfig == null) {
                throw new IllegalArgumentException("模板中存在无效的模型字段 model=" + model + ", field=" + fieldKey);
            }
            if (!TtypeEnum.isRelationType(modelFieldConfig.getTtype())) {
                continue;
            }
            List<Object> needQueryList = new ArrayList<>();
            for (Object item : list) {
                if (relationManagerProcessor.isNeedQueryRelation(modelFieldConfig, FieldUtils.getFieldValue(item, modelFieldConfig.getLname()))) {
                    needQueryList.add(item);
                }
            }
            if (needQueryList.isEmpty()) {
                continue;
            }
            String field, indexString;
            int li = fieldKey.indexOf("["), ri = fieldKey.indexOf("]"), index;
            if (li != -1 && ri != -1) {
                indexString = fieldKey.substring(li + 1, ri);
                index = Integer.parseInt(indexString);
                field = fieldKey.substring(0, li);
                if (!hasQueryFields.contains(field)) {
                    currentMaxLength = computeCurrentMaxLength(maxSupportLength, currentMaxLength, needQueryList.size(), FetchUtil.listFieldQuery(dataManager, needQueryList, modelFieldsCache.get(field), maxSupportLength));
                    hasQueryFields.add(field);
                }
                if (index < needQueryList.size()) {
                    Object value = needQueryList.get(index);
                    if (!selectRelationField(dataManager, modelFieldConfig.getReferences(), fieldNode.getChildren(), Collections.singletonList(value), maxSupportLength, currentMaxLength)) {
                        return false;
                    }
                }
            } else if (li == -1 && ri == -1) {
                field = fieldKey;
                if (!hasQueryFields.contains(field)) {
                    currentMaxLength = computeCurrentMaxLength(maxSupportLength, currentMaxLength, needQueryList.size(), FetchUtil.listFieldQuery(dataManager, needQueryList, modelFieldsCache.get(field), maxSupportLength));
                    if (currentMaxLength == -1) {
                        return false;
                    }
                    hasQueryFields.add(field);
                }
                boolean isCollection = fieldNode.getValue().getIsCollection();
                List<Object> childList = new ArrayList<>();
                for (Object item : needQueryList) {
                    Object relationValue = FieldUtils.getFieldValue(item, field);
                    if (relationValue == null) {
                        continue;
                    }
                    if (isCollection) {
                        childList.addAll((Collection<?>) relationValue);
                    } else {
                        childList.add(relationValue);
                    }
                }
                if (!selectRelationField(dataManager, modelFieldConfig.getReferences(), fieldNode.getChildren(), childList, maxSupportLength, currentMaxLength)) {
                    return false;
                }
            }
        }
        return true;
    }

    private int computeCurrentMaxLength(int excelMaxSupportLength, int currentMaxLength, int needQuerySize, int listFieldQuerySize) {
        int incrementalSize = listFieldQuerySize - needQuerySize;
        if (incrementalSize <= 0) {
            return currentMaxLength;
        }
        currentMaxLength += incrementalSize;
        if (currentMaxLength > excelMaxSupportLength) {
            return -1;
        }
        return currentMaxLength;
    }
}
