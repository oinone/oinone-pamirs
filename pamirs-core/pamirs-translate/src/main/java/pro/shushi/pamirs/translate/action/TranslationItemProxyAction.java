package pro.shushi.pamirs.translate.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.core.common.function.FunctionConstant;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.translate.constant.TranslateConstants;
import pro.shushi.pamirs.translate.enmu.TranslateEnumerate;
import pro.shushi.pamirs.translate.proxy.TranslationItemProxy;
import pro.shushi.pamirs.translate.service.TranslationItemProxyService;

import java.util.*;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.translate.enmu.TranslateEnumerate.*;

@Slf4j
@Component
@Model.model(TranslationItemProxy.MODEL_MODEL)
public class TranslationItemProxyAction {
    @Autowired
    private TranslationItemProxyService translationItemProxyService;

    @Function(openLevel = FunctionOpenEnum.API, summary = "翻译资源构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public TranslationItemProxy construct(TranslationItemProxy data) {
        return data.construct();
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public TranslationItemProxy queryOne(TranslationItemProxy query) {
        query = query.queryById();
        query.fieldQuery(TranslationItemProxy::getModuleDefinition);
        query.fieldQuery(TranslationItemProxy::getResLang);
        query.fieldQuery(TranslationItemProxy::getLang);
        return query;
    }


    @Action(displayName = "刷新远程资源", bindingType = {ViewTypeEnum.TABLE}, contextType = ActionContextTypeEnum.CONTEXT_FREE)
    @Action.Advanced(type = FunctionTypeEnum.QUERY)
    public TranslationItemProxy refreshRemoteResource(TranslationItemProxy data) {
        translationItemProxyService.refreshRemoteResource();
        // 缓存后端翻译
        // translateCoreManager.initTranslateFromDbToRedis();
        return data;
    }


    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<TranslationItemProxy> queryPage(Pagination<TranslationItemProxy> page, IWrapper<TranslationItemProxy> queryWrapper) {
        LambdaQueryWrapper<TranslationItemProxy> qw = WrapperHelper.lambda(queryWrapper)
                .orderByDesc(TranslationItemProxy::getWriteDate)
                .isNotNull(TranslationItemProxy::getTarget);

        page = new TranslationItemProxy().queryPage(page, qw);
        List<TranslationItemProxy> itemProxies = page.getContent();
        if (CollectionUtils.isNotEmpty(itemProxies)) {
            new TranslationItemProxy().listFieldQuery(itemProxies, TranslationItemProxy::getModuleDefinition);
            new TranslationItemProxy().listFieldQuery(itemProxies, TranslationItemProxy::getResLang);
            new TranslationItemProxy().listFieldQuery(itemProxies, TranslationItemProxy::getLang);
        }
        return page;
    }


    @Transactional
    @Action.Advanced(name = FunctionConstant.delete)
    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Function(name = FunctionConstant.delete)
    @Function.fun(FunctionConstant.deleteWithFieldBatch)
    public List<TranslationItemProxy> delete(List<TranslationItemProxy> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        ArrayList<TranslationItemProxy> systemTranslateItem = new ArrayList<>();
        ArrayList<TranslationItemProxy> translationItemProxies = new ArrayList<>();
        for (TranslationItemProxy itemProxy : list) {
            if (Optional.ofNullable(itemProxy.getSystem()).orElse(Boolean.FALSE)) {
                itemProxy.setTarget(null);
                itemProxy.setState(Boolean.FALSE);
                systemTranslateItem.add(itemProxy);
            } else {
                translationItemProxies.add(itemProxy);
            }
        }
        if (CollectionUtils.isNotEmpty(systemTranslateItem)) {
            new TranslationItemProxy().updateBatch(systemTranslateItem);
        }
        if (CollectionUtils.isNotEmpty(translationItemProxies)) {
            new TranslationItemProxy().deleteByPks(translationItemProxies);
        }

        return list;
    }

    @Action(displayName = "激活", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    public List<TranslationItemProxy> active(List<TranslationItemProxy> list) {
        Set<Long> ids = list.stream().map(TranslationItemProxy::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (!ids.isEmpty()) {
            TranslationItemProxy updateData = new TranslationItemProxy();
            updateData.setState(Boolean.TRUE);
            Models.origin().updateByWrapper(updateData, Pops.<TranslationItemProxy>lambdaUpdate()
                    .from(TranslationItemProxy.MODEL_MODEL)
                    .in(TranslationItemProxy::getId, ids));
        }
        return list;
    }

    @Function.Advanced(displayName = "工具箱添加翻译打开显示", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_LIST)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public List<TranslationItemProxy> queryByInsertPage(TranslationItemProxy data) {
        String moduleName = data.getModuleName();
        Optional.ofNullable(moduleName).orElseThrow(() -> PamirsException.construct(TranslateEnumerate.TRANSLATE_MODEL_IS_NULL_ERROR).errThrow());
        data.setModule(PStringUtils.fieldName2Column(data.getModuleName()));
        String langCode = Optional.ofNullable(data.getLangCode()).orElse(TranslateConstants.LANG_CODE);
        String resLangCode = Optional.ofNullable(data.getResLangCode()).orElse(TranslateConstants.RES_LANG_CODE);
        Optional.ofNullable(data.getModel()).orElseThrow(() -> PamirsException.construct(TranslateEnumerate.MODEL_DOES_NOT_EXIST).errThrow());
        Optional.ofNullable(data.getAction()).orElseThrow(() -> PamirsException.construct(TranslateEnumerate.ACTION_DOES_NOT_EXIST).errThrow());
        String searchOrigin = Optional.ofNullable(data.getOrigin()).orElse("");

        return translationItemProxyService.queryByInsertPage(data, langCode, resLangCode, searchOrigin);
    }

    @Function.Advanced(displayName = "工具箱更改翻译打开显示", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_LIST)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public List<TranslationItemProxy> queryByChangePage(TranslationItemProxy data) {
        String moduleName = data.getModuleName();
        Optional.ofNullable(moduleName).orElseThrow(() -> PamirsException.construct(TranslateEnumerate.TRANSLATE_MODEL_IS_NULL_ERROR).errThrow());
        data.setModule(PStringUtils.fieldName2Column(data.getModuleName()));
        String langCode = Optional.ofNullable(data.getLangCode()).orElse(TranslateConstants.LANG_CODE);
        String resLangCode = Optional.ofNullable(data.getResLangCode()).orElse(TranslateConstants.RES_LANG_CODE);
        Optional.ofNullable(data.getModel()).orElseThrow(() -> PamirsException.construct(TranslateEnumerate.MODEL_DOES_NOT_EXIST).errThrow());
        Optional.ofNullable(data.getAction()).orElseThrow(() -> PamirsException.construct(TranslateEnumerate.ACTION_DOES_NOT_EXIST).errThrow());
        String searchTarget = Optional.ofNullable(data.getTarget()).orElse("");

        return translationItemProxyService.queryByChangePage(data, langCode, resLangCode, searchTarget);
    }


    @Transactional
    @Action.Advanced(name = FunctionConstants.create, invisible = ExpConstants.idValueExist)
    @Action(displayName = "创建", label = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    public TranslationItemProxy create(TranslationItemProxy data) {
        String module = data.getModule();
        Optional.ofNullable(module).map(item -> PamirsSession.getContext().getModule(item)).orElseThrow(() -> PamirsException.construct(TRANSLATE_MODEL_IS_NULL_ERROR).errThrow());
        String resLangCode = Optional.ofNullable(data.getResLangCode()).orElse(TranslateConstants.RES_LANG_CODE);
        Optional.ofNullable(data.getLangCode()).orElseThrow(() -> PamirsException.construct(TARGET_LANGUAGE_CANNOT_BE_EMPTY).errThrow());
        Optional.ofNullable(data.getTarget()).orElseThrow(() -> PamirsException.construct(TRANSLATION_VALUE_CANNOT_BE_EMPTY).errThrow());
        Optional.ofNullable(data.getOrigin()).orElseThrow(() -> PamirsException.construct(SOURCE_LANGUAGE_CANNOT_BE_EMPTY).errThrow());

        if (resLangCode.equals(data.getLangCode())) {
            throw PamirsException.construct(TARGET_LANGUAGE_EQUALS_SOURCE_LANGUAGE).errThrow();
        }
        translationItemProxyService.createTranslationResourceItem(data, resLangCode);
        return data;
    }

    @Action.Advanced(name = FunctionConstants.update, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "更新", label = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.update)
    @Function.fun(FunctionConstants.update)
    public TranslationItemProxy update(TranslationItemProxy data) {
        Optional.ofNullable(data.getModule()).orElseThrow(() -> PamirsException.construct(TRANSLATE_MODEL_IS_NULL_ERROR).errThrow());
        ModuleDefinition moduleDefinition = PamirsSession.getContext().getModule(data.getModule());
        Optional.ofNullable(moduleDefinition).orElseThrow(() -> PamirsException.construct(TRANSLATE_MODEL_IS_NULL_ERROR).errThrow());
        Optional.ofNullable(data.getTarget()).orElseThrow(() -> PamirsException.construct(TRANSLATION_VALUE_CANNOT_BE_EMPTY).errThrow());
        Optional.ofNullable(data.getOrigin()).orElseThrow(() -> PamirsException.construct(SOURCE_LANGUAGE_CANNOT_BE_EMPTY).errThrow());
        String resLangCode = Optional.ofNullable(data.getResLangCode()).orElse(TranslateConstants.RES_LANG_CODE);

        if (resLangCode.equals(data.getLangCode())) {
            throw PamirsException.construct(TARGET_LANGUAGE_EQUALS_SOURCE_LANGUAGE).errThrow();
        }
        translationItemProxyService.updateTranslationResourceItem(data, resLangCode);
        return data;
    }
}
