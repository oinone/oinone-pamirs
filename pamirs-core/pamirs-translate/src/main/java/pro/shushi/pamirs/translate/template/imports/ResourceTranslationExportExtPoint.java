package pro.shushi.pamirs.translate.template.imports;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.entity.ExcelExportFetchDataContext;
import pro.shushi.pamirs.file.api.extpoint.impl.DefaultExcelExportFetchDataExtPoint;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.constant.SqlConstants;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.resource.api.enmu.TranslationApplicationScopeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.translate.constant.TranslateConstants;
import pro.shushi.pamirs.translate.proxy.TranslationItemExportProxy;
import pro.shushi.pamirs.translate.service.TranslationDslNodeVisitor;
import pro.shushi.pamirs.translate.template.TranslateTemplate;
import pro.shushi.pamirs.translate.utils.UniversalParser;

import java.util.*;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.translate.constant.TranslateConstants.FIELD_TO_EXCLUDE;


/**
 * @author Adamancy Zhang
 * @date 2020-11-04 18:09
 */
@Slf4j
@Component
@Ext(ExcelExportTask.class)
@SuppressWarnings({"unchecked"})
public class ResourceTranslationExportExtPoint extends DefaultExcelExportFetchDataExtPoint {

    private String resLangCodeColumn = PStringUtils.fieldName2Column(LambdaUtil.fetchFieldName(ResourceTranslationItem::getResLangCode));
    private String langCodeColumn = PStringUtils.fieldName2Column(LambdaUtil.fetchFieldName(ResourceTranslationItem::getLangCode));
    private String moduleColumn = PStringUtils.fieldName2Column(LambdaUtil.fetchFieldName(ResourceTranslationItem::getModule));


    @Override
    @ExtPoint.Implement(expression = "context.name==\"" + TranslateTemplate.TEMPLATE_NAME + "\" && context.model==\"" + ResourceTranslation.MODEL_MODEL + "\"")
    public List<Object> fetchExportData(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        ArrayList<Object> objects = new ArrayList<>();
        Map<String, Object> queryData = exportTask.getConditionWrapper().getQueryData();

        TranslationItemExportProxy data = JSON.parseObject(JSON.toJSONString(queryData), TranslationItemExportProxy.class);
        LambdaQueryWrapper<TranslationItemExportProxy> queryWrapper = Pops.<TranslationItemExportProxy>lambdaQuery()
                .from(TranslationItemExportProxy.MODEL_MODEL)
                .eq(StringUtils.isNotBlank(data.getModule()), ResourceTranslationItem::getModule, data.getModule())
                .eq(ResourceTranslationItem::getResLangCode, TranslateConstants.RES_LANG_CODE)
                .eq(StringUtils.isNotBlank(data.getLangCode()), ResourceTranslationItem::getLangCode, data.getLangCode())
                .eq(data.getState() != null, ResourceTranslationItem::getState, data.getState())
                .like(StringUtils.isNotBlank(data.getResLangInclude()), ResourceTranslationItem::getOrigin, data.getResLangInclude())
                .like(StringUtils.isNotBlank(data.getTargetInclude()), ResourceTranslationItem::getTarget, data.getResLangInclude());

        Map<String, String> moduleNameMap = Models.origin().queryListByWrapper(Pops.<ModuleDefinition>lambdaQuery()
                        .from(ModuleDefinition.MODEL_MODEL)
                        .eq(StringUtils.isNotBlank(data.getModule()), ModuleDefinition::getModule, data.getModule())
                        .eq(ModuleDefinition::getApplication, true))
                .stream()
                .collect(Collectors.toMap(ModuleDefinition::getModule, ModuleDefinition::getDisplayName, (_a, _b) -> _a));
        ModuleDefinition moduleDefinition = new ModuleDefinition();
        moduleDefinition.setModule(TranslateConstants.PUBLIC_RESOURCE);
        moduleDefinition = moduleDefinition.queryOne();
        moduleNameMap.put(moduleDefinition.getModule(), moduleDefinition.getDisplayName());


        List<TranslationItemExportProxy> translationItemExportProxies = new ArrayList<>();
        //TODO 源语言无法识别请在YAML 中直接配置
        switch (data.getIsTranslate()) {
            case TRANSLATED:
                queryWrapper.eq(data.getScope() != null, ResourceTranslationItem::getScope, data.getScope()).isNotNull(ResourceTranslationItem::getTarget);
                translationItemExportProxies = new TranslationItemExportProxy().queryList(queryWrapper);
                Models.origin().listFieldQuery(translationItemExportProxies, TranslationItemExportProxy::getTranslation);
                if (CollectionUtils.isNotEmpty(translationItemExportProxies)) {
                    for (TranslationItemExportProxy item : translationItemExportProxies) {
                        item.setModule(Optional.ofNullable(item.getModule()).map(moduleNameMap::get).orElse(item.getModule()));
                        item.setComments(Optional.ofNullable(item.getTranslation().getComments()).orElse(""));
                    }
                }
                if (CollectionUtils.isNotEmpty(translationItemExportProxies)) {
                    objects.add(translationItemExportProxies);
                }
                break;
            case NOT_TRANSLATED:
                translationItemExportProxies = new TranslationItemExportProxy().queryList(queryWrapper);
                if (CollectionUtils.isNotEmpty(translationItemExportProxies)) {
                    translationItemExportProxies = new TranslationItemExportProxy().listFieldQuery(translationItemExportProxies, TranslationItemExportProxy::getTranslation);
                }
                if (TranslationApplicationScopeEnum.GLOBAL.equals(data.getScope()) || Boolean.TRUE.equals(data.getState())
                        || StringUtils.isNotBlank(data.getTargetInclude()) || StringUtils.isNotBlank(data.getResLangInclude())) {
                    break;
                }
                //解析XML
                Map<String, Set<String>> translationContext = parseXmlAndModelAndEnumAndErrorInfo(data, moduleNameMap);

                // 初始化结果容器
                List<TranslationItemExportProxy> noTranslateItems = new ArrayList<>();
                Map<Boolean, Map<String, List<String>>> origins = new HashMap<>();
                origins.put(true, new HashMap<>());
                origins.put(false, new HashMap<>());

                // 单次遍历处理两个操作
                translationItemExportProxies.forEach(item -> {
                    // 处理第一个过滤和收集操作
                    if (StringUtils.isEmpty(item.getTarget()) && Boolean.TRUE.equals(Optional.ofNullable(item.getSystem()).orElse(Boolean.FALSE))) {
                        item.setModule(Optional.ofNullable(item.getModule()).map(moduleNameMap::get).orElse(item.getModule()));
                        item.setState(false);
                        item.setComments("");
                        item.setLangCode(Optional.ofNullable(data.getLangCode()).orElse(""));
                        item.setResLangCode(TranslateConstants.RES_LANG_CODE);
                        noTranslateItems.add(item);
                    }

                    // 处理分类和收集操作
                    if (TranslationApplicationScopeEnum.GLOBAL.equals(item.getScope()) || TranslationApplicationScopeEnum.MODULE.equals(item.getScope())) {
                        boolean isGlobal = TranslationApplicationScopeEnum.GLOBAL.equals(item.getScope());
                        String module = isGlobal ? "" : item.getModule();

                        origins.computeIfAbsent(isGlobal, k -> new HashMap<>())
                                .computeIfAbsent(module, k -> new ArrayList<>())
                                .add(item.getOrigin());
                    }
                });

                List<String> overallOrigins = origins.get(true).get("");
                Map<String, List<String>> applicationOrigins = origins.get(false);


                for (Map.Entry<String, Set<String>> entry : translationContext.entrySet()) {
                    if (applicationOrigins.containsKey(entry.getKey())) {
                        List<String> list = applicationOrigins.get(entry.getKey());
                        entry.getValue().removeAll(list);
                        entry.getValue().removeAll(overallOrigins);
                    }
                }

                //生成系统中未翻译的字段
                translationItemExportProxies = getTranslationItemExportProxies(moduleNameMap, translationContext, data);
                translationItemExportProxies.addAll(noTranslateItems);
                if (CollectionUtils.isNotEmpty(translationItemExportProxies)) {
                    objects.add(translationItemExportProxies);
                }
                break;
            default:
                List<TranslationItemExportProxy> result = new ArrayList<>();
                queryWrapper.eq(data.getScope() != null, ResourceTranslationItem::getScope, data.getScope());
                translationItemExportProxies = new TranslationItemExportProxy().queryList(queryWrapper);
                if (CollectionUtils.isNotEmpty(translationItemExportProxies)) {
                    translationItemExportProxies = new TranslationItemExportProxy().listFieldQuery(translationItemExportProxies, TranslationItemExportProxy::getTranslation);
                }

                if (TranslationApplicationScopeEnum.GLOBAL.equals(data.getScope()) || Boolean.TRUE.equals(data.getState()) || StringUtils.isNotBlank(data.getTargetInclude())) {
                    if (CollectionUtils.isNotEmpty(translationItemExportProxies)) {
                        for (TranslationItemExportProxy item : translationItemExportProxies) {
                            item.setModule(Optional.ofNullable(item.getModule()).map(moduleNameMap::get).orElse(item.getModule()));
                            item.setComments(Optional.ofNullable(item.getTranslation()).map(ResourceTranslation::getComments).orElse(null));
                        }
                        result.addAll(translationItemExportProxies);
                        objects.add(result);
                    }
                    break;
                }
                Map<String, Set<String>> tContext = parseXmlAndModelAndEnumAndErrorInfo(data, moduleNameMap);

                Map<Boolean, Map<String, List<String>>> origins0 = translationItemExportProxies.stream()
                        .filter(item -> TranslationApplicationScopeEnum.GLOBAL.equals(item.getScope()) || TranslationApplicationScopeEnum.MODULE.equals(item.getScope()))
                        .collect(Collectors.partitioningBy(
                                item -> TranslationApplicationScopeEnum.GLOBAL.equals(item.getScope()),
                                Collectors.groupingBy(
                                        item -> TranslationApplicationScopeEnum.MODULE.equals(item.getScope()) ? item.getModule() : "",
                                        Collectors.mapping(TranslationItemExportProxy::getOrigin, Collectors.toList())
                                )
                        ));

                List<String> overOrigins = origins0.get(true).getOrDefault("", Collections.emptyList());
                Map<String, List<String>> applicationsOrigins = origins0.get(false);


                for (Map.Entry<String, Set<String>> entry : tContext.entrySet()) {
                    if (applicationsOrigins.containsKey(entry.getKey())) {
                        List<String> list = applicationsOrigins.get(entry.getKey());
                        entry.getValue().removeAll(list);
                        entry.getValue().removeAll(overOrigins);
                    }
                }

                //生成系统中未翻译的字段
                List<TranslationItemExportProxy> noTranslation = getTranslationItemExportProxies(moduleNameMap, tContext, data);
                if (CollectionUtils.isNotEmpty(translationItemExportProxies)) {
                    for (TranslationItemExportProxy item : translationItemExportProxies) {
                        item.setModule(Optional.ofNullable(item.getModule()).map(moduleNameMap::get).orElse(item.getModule()));
                        item.setComments(Optional.ofNullable(item.getTranslation()).map(ResourceTranslation::getComments).orElse(null));
                    }
                    result.addAll(translationItemExportProxies);
                }
                if (CollectionUtils.isNotEmpty(noTranslation)) {
                    result.addAll(noTranslation);
                }
                objects.add(result);
                break;
        }
        return objects;
    }

    private Map<String, Set<String>> parseXmlAndModelAndEnumAndErrorInfo(TranslationItemExportProxy data, Map<String, String> moduleNameMap) {
        //解析XML
        TranslationDslNodeVisitor translationVisitor = UniversalParser.getTranslationDslNodeVisitor(data);
        Map<String, Set<String>> tContext = translationVisitor.context;
        //解析模型
        UniversalParser.parseModel(moduleNameMap, tContext, data.getModule());
        //解析枚举
        UniversalParser.parseModelEnum(moduleNameMap, tContext, data.getModule());
        //解析错误信息
        UniversalParser.parseErrorInfo(moduleNameMap, tContext, data.getModule());
        return tContext;
    }


    /**
     * 生成系统中未翻译的字段
     *
     * @param moduleNameMap      模块编码与名称的映射
     * @param translationContext 需要翻译的字段
     * @param data
     * @return
     */
    private static List<TranslationItemExportProxy> getTranslationItemExportProxies(Map<String, String> moduleNameMap, Map<String, Set<String>> translationContext, TranslationItemExportProxy data) {
        List<TranslationItemExportProxy> translationItemExportProxies = new ArrayList<>();

        for (Map.Entry<String, Set<String>> entry : translationContext.entrySet()) {
            for (String origin : entry.getValue()) {
                if (FIELD_TO_EXCLUDE.contains(origin.toLowerCase())) {
                    continue;
                }
                if (StringUtils.isNotBlank(data.getResLangInclude())) {
                    if (origin.contains(data.getResLangInclude())) {
                        TranslationItemExportProxy item = new TranslationItemExportProxy();
                        item.setModule(Optional.ofNullable(entry.getKey()).map(moduleNameMap::get).orElse(entry.getKey()));
                        item.setResLangCode(TranslateConstants.RES_LANG_CODE);
                        item.setLangCode(Optional.ofNullable(data.getLangCode()).orElse(""));
                        item.setOrigin(origin);
                        item.setState(false);
                        item.setScope(TranslationApplicationScopeEnum.MODULE);
                        item.setComments("");
                        translationItemExportProxies.add(item);
                    }
                } else {
                    TranslationItemExportProxy item = new TranslationItemExportProxy();
                    item.setModule(Optional.ofNullable(entry.getKey()).map(moduleNameMap::get).orElse(entry.getKey()));
                    item.setResLangCode(TranslateConstants.RES_LANG_CODE);
                    item.setLangCode(Optional.ofNullable(data.getLangCode()).orElse(""));
                    item.setOrigin(origin);
                    item.setState(false);
                    item.setScope(TranslationApplicationScopeEnum.MODULE);
                    item.setComments("");
                    translationItemExportProxies.add(item);

                }
            }
        }
        return translationItemExportProxies;
    }


    @Override
    protected IWrapper<?> queryBefore(ExcelExportFetchDataContext context, IWrapper<?> wrapper) {
        wrapper.setModel(ResourceTranslation.MODEL_MODEL);
        List<?> translations = Models.data().queryListByWrapper(wrapper);

        QueryWrapper<ResourceTranslationItem> itemWrapper = Pops.query();
        itemWrapper.setModel(ResourceTranslationItem.MODEL_MODEL);

        if (CollectionUtils.isEmpty(translations)) {
            itemWrapper.lt(SqlConstants.ID, 0);
        } else {
            itemWrapper.in(
                    Lists.newArrayList(resLangCodeColumn, langCodeColumn, moduleColumn),
                    translations.stream()
                            .map(i -> ((ResourceTranslation) i).getResLangCode())
                            .collect(Collectors.toList()),
                    translations.stream()
                            .map(i -> ((ResourceTranslation) i).getLangCode())
                            .collect(Collectors.toList()),
                    translations.stream()
                            .map(i -> ((ResourceTranslation) i).getModule())
                            .collect(Collectors.toList())
            );
        }
        return itemWrapper;
    }
}
