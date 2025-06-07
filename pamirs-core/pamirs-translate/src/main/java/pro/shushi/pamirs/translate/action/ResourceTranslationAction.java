package pro.shushi.pamirs.translate.action;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.translate.constant.TranslateConstants;
import pro.shushi.pamirs.translate.enmu.TranslateEnumerate;
import pro.shushi.pamirs.translate.service.TranslationItemProxyService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.translate.enmu.TranslateEnumerate.*;

/**
 * @author Adamancy Zhang
 * @date 2020-11-04 15:50
 */
@Slf4j
@Component
@Model.model(ResourceTranslation.MODEL_MODEL)
public class ResourceTranslationAction {

    @Autowired
    private TranslationItemProxyService translationItemProxyService;

    /**
     * 翻译文件规则
     * <p>
     * oss配置的文件路径+租户+translate+模块+语言文件
     * https://relx-kaiyang.oss-cn-zhangjiakou.aliyuncs.com/upload/libra00/pamirs/translate/libraCore/i18n_en.js
     *
     * @param data
     * @return
     */
    @Function(openLevel = FunctionOpenEnum.API, summary = "翻译资源构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public ResourceTranslation construct(ResourceTranslation data) {
        data = data.construct();
        data.setResLangCode(TranslateConstants.RES_LANG_CODE);
        data.fieldSave(ResourceTranslation::getResLang);
        return data;
    }

    @Function(openLevel = FunctionOpenEnum.API, summary = "翻译资源下拉触发")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public ResourceTranslation constructMirror(ResourceTranslation data) {
        ModelDefinition modelDefinition = data.getModelDefinition();
        DataDictionary dataDictionary = data.getDataDictionary();

        if (data.getIsDict() != null) {
            if (data.getIsDict()) {
                if (dataDictionary != null) {
                    dataDictionary = FetchUtil.fetchOne(dataDictionary);
                    data.setDataDictionary(dataDictionary);
                    data.setModel(Optional.ofNullable(dataDictionary).map(DataDictionary::getDictionary).orElse(""));
                } else {

                    data.setModel("");
                }
                return data;
            } else {
                if (modelDefinition != null) {
                    modelDefinition = FetchUtil.fetchOne(modelDefinition);
                    data.setModelDefinition(modelDefinition);
                    data.setModel(Optional.ofNullable(modelDefinition).map(ModelDefinition::getModel).orElse(""));
                } else {
                    data.setModel("");
                }
                return data;
            }
        } else {
            if (modelDefinition != null) {
                modelDefinition = FetchUtil.fetchOne(modelDefinition);
                data.setModelDefinition(modelDefinition);
                data.setModel(Optional.ofNullable(modelDefinition).map(ModelDefinition::getModel).orElse(""));
                return data;
            }
            if (dataDictionary != null) {
                dataDictionary = FetchUtil.fetchOne(dataDictionary);
                data.setDataDictionary(dataDictionary);
                data.setModel(Optional.ofNullable(dataDictionary).map(DataDictionary::getDictionary).orElse(""));
                return data;
            }
        }
        return data;
    }


    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public ResourceTranslation queryOne(ResourceTranslation query) {
        query = query.queryById();
        query.setTranslationItems(new ArrayList<>());
        return query;
    }


    @Transactional
    @Action.Advanced(name = FunctionConstants.create, managed = true, invisible = ExpConstants.idValueExist)
    @Action(displayName = "创建", label = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    public ResourceTranslation create(ResourceTranslation data) {
        if (data != null && StringUtils.isBlank(data.getModule())) {
            String moduleName = data.getModuleName();
            if (StringUtils.isNotBlank(moduleName)) {
                ModuleDefinition moduleDefinition = Optional.ofNullable(PamirsSession.getContext().getModuleCache().getByName(moduleName))
                        .orElseThrow(() -> PamirsException.construct(TRANSLATE_MODEL_IS_NULL_ERROR).errThrow());
                Optional.ofNullable(moduleDefinition.getModule()).ifPresent(data::setModule);
            }
        }
        validateTranslation(data);
        translationItemProxyService.create(data);
        return data;
    }


    @Action(displayName = "确定并刷新", bindingType = {ViewTypeEnum.FORM}, contextType = ActionContextTypeEnum.CONTEXT_FREE)
    @Action.Advanced(type = {FunctionTypeEnum.CREATE, FunctionTypeEnum.UPDATE})
    public ResourceTranslation createOrUpdateAndRefreshBatch(ResourceTranslation data) {
        if (data != null && StringUtils.isBlank(data.getModule())) {
            String moduleName = data.getModuleName();
            if (StringUtils.isNotBlank(moduleName)) {
                ModuleDefinition moduleDefinition = Optional.ofNullable(PamirsSession.getContext().getModuleCache().getByName(moduleName))
                        .orElseThrow(() -> PamirsException.construct(TRANSLATE_MODEL_IS_NULL_ERROR).errThrow());
                Optional.ofNullable(moduleDefinition.getModule()).ifPresent(data::setModule);
            }
        }
        String resLangCode = validateTranslation(data);
        translationItemProxyService.createOrUpdateAndRefreshBatch(data, resLangCode);
        return data;
    }

    private static String validateTranslation(ResourceTranslation data) {
        Optional.ofNullable(data.getModule()).map(item -> PamirsSession.getContext().getModule(item)).orElseThrow(() -> PamirsException.construct(TRANSLATE_MODEL_IS_NULL_ERROR).errThrow());
        Optional.ofNullable(data.getLangCode()).orElseThrow(() -> PamirsException.construct(TARGET_LANGUAGE_CANNOT_BE_EMPTY).errThrow());

        String resLangCode = data.getResLangCode();
        String langCode = data.getLangCode();
        if (StringUtils.isBlank(resLangCode)) {
            data.setResLangCode(TranslateConstants.RES_LANG_CODE);
        }
        if (data.getResLangCode().equals(data.getLangCode())) {
            throw PamirsException.construct(TARGET_LANGUAGE_EQUALS_SOURCE_LANGUAGE).errThrow();
        }
        List<ResourceLang> resourceLangList = new ResourceLang().queryList();
        List<String> resourceLangs = resourceLangList.stream().map(ResourceLang::getCode).collect(Collectors.toList());

        if (!(resourceLangs.contains(resLangCode))) {
            throw PamirsException.construct(TranslateEnumerate.TARGET_LANGUAGE_ENCODING_ERROR, Arrays.asList(resourceLangs)).errThrow();
        }
        if (!(resourceLangs.contains(langCode))) {
            throw PamirsException.construct(TranslateEnumerate.TARGET_LANGUAGE_ENCODING_ERROR, Arrays.asList(resourceLangs)).errThrow();
        }
        return resLangCode;
    }
}
