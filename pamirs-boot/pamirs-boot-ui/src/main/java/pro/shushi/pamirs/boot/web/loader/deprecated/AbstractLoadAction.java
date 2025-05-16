package pro.shushi.pamirs.boot.web.loader.deprecated;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author shier
 * date  2020/12/2 9:31 下午
 */
public class AbstractLoadAction {

    <R, T extends IdModel> List<R> fillMainDataListList(List<R> mainDataList, Function<R, List<T>> getter, ModelFieldConfig modelFieldConfig) {
        List<T> referenceDataList = new ArrayList<>();
        mainDataList.stream().map(getter).forEach(v -> {
            if (v != null) {
                referenceDataList.addAll(v);
            }
        });
        return fillData(mainDataList, modelFieldConfig, referenceDataList);
    }

    <R, T extends IdModel> List<R> fillMainDataList(List<R> mainDataList, Function<R, T> getter, ModelFieldConfig modelFieldConfig) {
        List<T> referenceDataList = mainDataList.stream().map(getter).collect(Collectors.toList());
        return fillData(mainDataList, modelFieldConfig, referenceDataList);
    }

    private <R, T extends IdModel> List<R> fillData(List<R> mainDataList, ModelFieldConfig modelFieldConfig, List<T> referenceDataList) {
        List<String> sqls = new ArrayList<>();
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>().from(modelFieldConfig.getReferences());
        for (Object referenceData : referenceDataList) {
            if (referenceData == null) {
                continue;
            }
            List<String> andSqls = new ArrayList<>();
            for (String referenceField : modelFieldConfig.getReferenceFields()) {
                StringBuilder stringBuilder = new StringBuilder();
                String referenceFieldValue = (String) FieldUtils.getReferenceFieldValue(referenceData, modelFieldConfig.getReferences(), referenceField);
                andSqls.add(stringBuilder.append("`").append(referenceField).append("` = '").append(referenceFieldValue).append("'").toString());
            }
            sqls.add(StringUtils.join(andSqls, " and "));
        }
        if (CollectionUtils.isEmpty(sqls)) {
            return mainDataList;
        }
        boolean isFirst = true;
        for (String sql : sqls) {
            if (isFirst) {
                isFirst = false;
            } else {
                queryWrapper.or();
            }
            queryWrapper.apply(sql);
        }
        List<T> views = Models.origin().queryListByWrapper(queryWrapper);

        Map<String, T> mapperMap = new HashMap<>();
        for (T t : views) {
            StringBuilder value = new StringBuilder();
            for (String referenceField : modelFieldConfig.getReferenceFields()) {
                String referenceFieldValue = (String) FieldUtils.getReferenceFieldValue(t, modelFieldConfig.getReferences(), referenceField);
                value.append(referenceFieldValue).append("#");
            }
            mapperMap.put(value.toString(), t);
        }

        for (R r : mainDataList) {
            StringBuilder value = new StringBuilder();
            for (String relationField : modelFieldConfig.getRelationFields()) {
                String relationFieldValue = (String) FieldUtils.getRelationFieldValue(r, modelFieldConfig.getModel(), relationField);
                value.append(relationFieldValue).append("#");
            }
            FieldUtils.setFieldValue(r, modelFieldConfig.getLname(), mapperMap.get(value.toString()));
        }
        return mainDataList;
    }

    List<ViewAction> loadViewAction(List<ViewAction> viewActions) {
//        ModelFieldConfig page = PamirsSession.getContext().getModelField(ViewAction.MODEL_MODEL, "page");
        ModelFieldConfig resView = PamirsSession.getContext().getModelField(ViewAction.MODEL_MODEL, "resView");
//        viewActions = fillMainDataList(viewActions, ViewAction::getPage, page);
//        viewActions = loadPage(viewActions);
        // TODO @shier 填充viewAction的viewList
        viewActions = fillMainDataList(viewActions, ViewAction::getResView, resView);
        return viewActions;
    }

//    List<ViewAction> loadPage(List<ViewAction> viewActions) {
//        ModelFieldConfig page = PamirsSession.getContext().getModelField(Page.MODEL_MODEL, "viewList");
//        List<Page> pages = viewActions.stream().filter(v -> null != v.getPage()).map(v -> v.getPage()).collect(Collectors.toList());
//        if (CollectionUtils.isEmpty(pages)){
//            return viewActions;
//        }
//        viewActions = fillMainDataListList(viewActions, ViewAction::getViewList, page);
//        Map<Long, Page> pageMap = pages.stream().collect(Collectors.toMap(v -> v.getId(), v -> v, (a, b) -> a));
//        for (ViewAction viewAction:viewActions){
//            if (viewAction.getPage()!=null) {
//                viewAction.setPage(pageMap.get(viewAction.getPage().getId()));
//            }
//        }
//        return viewActions;
//    }

    List<ServerAction> loadServerAction(List<ServerAction> serverActions) {
        ModelFieldConfig functionDefinition = PamirsSession.getContext().getModelField(ServerAction.MODEL_MODEL, "functionDefinition");
        serverActions = fillMainDataList(serverActions, ServerAction::getFunctionDefinition, functionDefinition);
        return serverActions;
    }


}
