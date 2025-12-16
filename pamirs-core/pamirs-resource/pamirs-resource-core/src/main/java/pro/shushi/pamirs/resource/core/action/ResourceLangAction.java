package pro.shushi.pamirs.resource.core.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.function.FunctionConstant;
import pro.shushi.pamirs.framework.connectors.data.api.orm.BatchSizeHintApi;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.resource.api.enmu.ResourceEnumerate;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.resource.api.tmodel.lang.ResourceDateFormat;
import pro.shushi.pamirs.resource.api.tmodel.lang.ResourceTimeFormat;

import java.util.List;

@Base
@Component
@Model.model(ResourceLang.MODEL_MODEL)
public class ResourceLangAction {

    @Function(openLevel = {FunctionOpenEnum.API, FunctionOpenEnum.REMOTE}, summary = "查询语言")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public List<ResourceLang> queryListByEntity(ResourceLang query) {
        try (BatchSizeHintApi batchSize = BatchSizeHintApi.use(-1)) {
            List<ResourceLang> result = query.queryList();
            if (CollectionUtils.isNotEmpty(result)) {
                String userLang = PamirsSession.getLang();
                for (ResourceLang resourceLang : result) {
                    if (resourceLang.getCode().equals(userLang)) {
                        resourceLang.setUserCurrentLang(true);
                    }
                }
            }
            return result;
        }
    }

    @Function.Advanced(displayName = "登录页查询语言", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public List<ResourceLang> queryLoginLanguage() {
        return Models.origin().queryListByWrapper(Pops.<ResourceLang>lambdaQuery()
                .from(ResourceLang.MODEL_MODEL)
                .select(ResourceLang::getId, ResourceLang::getCode, ResourceLang::getName, ResourceLang::getIsoCode));
    }


    @Action.Advanced(type = FunctionTypeEnum.CREATE, managed = true, invisible = ExpConstants.idValueExist)
    @Action(displayName = "创建", label = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    public ResourceLang create(ResourceLang data) {
        data.setInstallState(Boolean.TRUE);
        ResourceDateFormat resourceDateFormat = data.getResourceDateFormat();
        ResourceTimeFormat resourceTimeFormat = data.getResourceTimeFormat();
        verifyDateFormat(resourceDateFormat);
        verifyTimeFormat(resourceTimeFormat);
        return data.create();
    }


    @Function.Advanced(displayName = "查询语言", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_ONE, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public ResourceLang queryOne(ResourceLang query) {
        return query.queryOne();
    }


    @Action.Advanced(type = FunctionTypeEnum.UPDATE, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "更新", label = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    public ResourceLang update(ResourceLang data) {
        ResourceDateFormat resourceDateFormat = data.getResourceDateFormat();
        ResourceTimeFormat resourceTimeFormat = data.getResourceTimeFormat();
        verifyDateFormat(resourceDateFormat);
        verifyTimeFormat(resourceTimeFormat);
        data.updateById();
        return data;
    }


    @Action.Advanced(type = FunctionTypeEnum.DELETE, managed = true, priority = 66)
    @Action(displayName = "删除", label = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Function.fun(FunctionConstant.deleteWithFieldBatch)
    public List<ResourceLang> delete(List<ResourceLang> dataList) {
        if (CollectionUtils.isNotEmpty(dataList)) {
            Long count = new ResourceLang().count();
            if (count.compareTo((long) dataList.size()) > 0) {
                new ResourceLang().deleteByPks(dataList);
            } else {
                throw PamirsException.construct(ResourceEnumerate.KEEP_LEAST_ONE_LANGUAGE).errThrow();
            }
        }
        return dataList;
    }


    private void verifyDateFormat(ResourceDateFormat resourceDateFormat) {
        String chineseYearMonth = resourceDateFormat.getChineseYearMonth();
        String hyphenYearMonth = resourceDateFormat.getHyphenYearMonth();
        String slashYearMonth = resourceDateFormat.getSlashYearMonth();

        String chinese = resourceDateFormat.getChinese();
        String hyphen = resourceDateFormat.getHyphen();
        String slash = resourceDateFormat.getSlash();

        if (StringUtils.isEmpty(chineseYearMonth)) {
            throw PamirsException.construct(ResourceEnumerate.CHINESE_YEAR_MONTH_FORMAT_DATE_EMPTY).errThrow();
        }
        if (StringUtils.isEmpty(hyphenYearMonth)) {
            throw PamirsException.construct(ResourceEnumerate.HYPHEN_YEAR_MONTH_FORMAT_EMPTY).errThrow();
        }
        if (StringUtils.isEmpty(slashYearMonth)) {
            throw PamirsException.construct(ResourceEnumerate.SLASH_YEAR_MONTH_FORMAT_EMPTY).errThrow();
        }

        if (StringUtils.isEmpty(chinese)) {
            throw PamirsException.construct(ResourceEnumerate.CHINESE_FORMAT_DATE_EMPTY).errThrow();
        }
        if (StringUtils.isEmpty(hyphen)) {
            throw PamirsException.construct(ResourceEnumerate.HYPHEN_FORMAT_DATE_EMPTY).errThrow();
        }
        if (StringUtils.isEmpty(slash)) {
            throw PamirsException.construct(ResourceEnumerate.SLASH_FORMAT_DATE_EMPTY).errThrow();
        }
    }

    private void verifyTimeFormat(ResourceTimeFormat resourceTimeFormat) {
        String apColonNormal = resourceTimeFormat.getApColonNormal();
        String apColonNormalSss = resourceTimeFormat.getApColonNormalSss();
        String apColonShort = resourceTimeFormat.getApColonShort();
        String colonNormal = resourceTimeFormat.getColonNormal();
        String colonNormalSss = resourceTimeFormat.getColonNormalSss();
        String colonShort = resourceTimeFormat.getColonShort();

        if (StringUtils.isEmpty(apColonNormal)) {
            throw PamirsException.construct(ResourceEnumerate.AP_COLON_NORMAL_IS_EMPTY).errThrow();
        }
        if (StringUtils.isEmpty(apColonNormalSss)) {
            throw PamirsException.construct(ResourceEnumerate.AP_COLON_NORMAL_SSS_IS_EMPTY).errThrow();
        }
        if (StringUtils.isEmpty(apColonShort)) {
            throw PamirsException.construct(ResourceEnumerate.AP_COLON_SHORT_IS_EMPTY).errThrow();
        }

        if (StringUtils.isEmpty(colonNormal)) {
            throw PamirsException.construct(ResourceEnumerate.COLON_NORMAL_IS_EMPTY).errThrow();
        }
        if (StringUtils.isEmpty(colonNormalSss)) {
            throw PamirsException.construct(ResourceEnumerate.COLON_NORMAL_SSS_IS_EMPTY).errThrow();
        }
        if (StringUtils.isEmpty(colonShort)) {
            throw PamirsException.construct(ResourceEnumerate.COLON_SHORT_IS_EMPTY).errThrow();
        }
    }
}