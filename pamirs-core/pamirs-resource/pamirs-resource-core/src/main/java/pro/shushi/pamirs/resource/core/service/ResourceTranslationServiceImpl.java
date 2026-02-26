package pro.shushi.pamirs.resource.core.service;

import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.standard.service.impl.AbstractStandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.WriteWithFieldApi;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.resource.api.service.ResourceTranslationService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link ResourceTranslationService}实现
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(ResourceTranslationService.FUN_NAMESPACE)
public class ResourceTranslationServiceImpl extends AbstractStandardModelService<ResourceTranslation> implements ResourceTranslationService {

    @Resource
    private WriteWithFieldApi defaultWriteWithFieldApi;

    @Function
    @Override
    public ResourceTranslation create(ResourceTranslation data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<ResourceTranslation> createBatch(List<ResourceTranslation> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public ResourceTranslation update(ResourceTranslation data) {
        return super.update(data);
    }

    @Function
    @Override
    public ResourceTranslation createOrUpdate(ResourceTranslation data) {
        return super.createOrUpdate(data);
    }

    @Override
    protected ResourceTranslation verificationAndSet(ResourceTranslation data, boolean isUpdate) {
        if (data == null) {
            return data;
        }
        if (CollectionUtils.isNotEmpty(data.getTranslationItems())) {
            for (ResourceTranslationItem translationItem : data.getTranslationItems()) {
                translationItem.initOriginCode();
            }
        }
        return data;
    }

    @Function
    @Override
    public List<ResourceTranslation> delete(List<ResourceTranslation> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public ResourceTranslation deleteOne(ResourceTranslation data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<ResourceTranslation> wrapper) {
        return super.deleteByWrapper(wrapper);
    }

    @Function
    @Override
    public Pagination<ResourceTranslation> queryPage(Pagination<ResourceTranslation> page, LambdaQueryWrapper<ResourceTranslation> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public ResourceTranslation queryOne(ResourceTranslation query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public ResourceTranslation queryOneByWrapper(LambdaQueryWrapper<ResourceTranslation> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<ResourceTranslation> queryListByWrapper(LambdaQueryWrapper<ResourceTranslation> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<ResourceTranslation> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected void afterProperties(ResourceTranslation data, boolean isUpdate) {
        List<ResourceTranslationItem> translationItems = data.getTranslationItems();
        if (CollectionUtils.isNotEmpty(translationItems)) {
            List<ResourceTranslationItem> existItems = data.fieldQuery(ResourceTranslation::getTranslationItems).getTranslationItems();
            if (CollectionUtils.isNotEmpty(existItems)) {
                List<Long> unDeleteIds = translationItems.stream().map(ResourceTranslationItem::getId).filter(Objects::nonNull).collect(Collectors.toList());

                existItems = existItems.stream().filter(i -> !unDeleteIds.contains(i.getId())).collect(Collectors.toList());
                Models.origin().deleteByPks(existItems);
            }
            Models.origin().createOrUpdateBatch(translationItems);
        } else {
            Models.origin().deleteByWrapper(Pops.<ResourceTranslationItem>lambdaQuery()
                    .from(ResourceTranslationItem.MODEL_MODEL)
                    .eq(ResourceTranslationItem::getResLangCode, data.getResLangCode())
                    .eq(ResourceTranslationItem::getLangCode, data.getLangCode())
                    .eq(ResourceTranslationItem::getModule, data.getModule())
                    .eq(ResourceTranslationItem::getModel, data.getModel()));
        }
    }

    @Override
    protected void deleteAfterProperties(List<ResourceTranslation> list) {
        for (ResourceTranslation item : list) {
            Models.origin().deleteByWrapper(Pops.<ResourceTranslationItem>lambdaQuery()
                    .from(ResourceTranslationItem.MODEL_MODEL)
                    .eq(ResourceTranslationItem::getResLangCode, item.getResLangCode())
                    .eq(ResourceTranslationItem::getLangCode, item.getLangCode())
                    .eq(ResourceTranslationItem::getModule, item.getModule())
                    .eq(ResourceTranslationItem::getModel, item.getModel()));
        }
    }
}
