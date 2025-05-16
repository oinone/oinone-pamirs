package pro.shushi.pamirs.resource.core.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLHelper;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlParseHelper;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlSearchOperation;
import pro.shushi.pamirs.framework.gateways.rsql.connector.RSQLNodeConnector;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;
import pro.shushi.pamirs.resource.api.model.ResourceIcon;
import pro.shushi.pamirs.resource.api.model.ResourceIconGroup;
import pro.shushi.pamirs.resource.api.model.ResourceIconLib;
import pro.shushi.pamirs.resource.api.service.ResourceIconService;
import pro.shushi.pamirs.resource.api.util.UnGroupData;

import java.util.Objects;

@Fun(ResourceIconService.FUN_NAMESPACE)
@Component
public class ResourceIconServiceImpl implements ResourceIconService {

    @Override
    @Function
    public Pagination<ResourceIcon> queryPage(Pagination<ResourceIcon> page, IWrapper<ResourceIcon> queryWrapper) {

        String rsql = RSQLHelper.toTargetString(Objects.requireNonNull(RSQLHelper.parse(queryWrapper.getModel(), queryWrapper.getOriginRsql())), new RSQLNodeConnector() {
            @Override
            public String comparisonConnector(RSQLNodeInfo nodeInfo) {
                String field = nodeInfo.getField();
                if ("displayName".equals(field)) {
                    String concatenatedSql = "(" + nodeInfo.getField() + RsqlSearchOperation.LIKE.getOperator() + nodeInfo.getArguments().get(0) + " or fontClass " + RsqlSearchOperation.LIKE.getOperator() + nodeInfo.getArguments().get(0) + ")";
                    if (StringUtils.isNotEmpty(concatenatedSql)) {
                        return concatenatedSql;
                    }
                }
                return super.comparisonConnector(nodeInfo);
            }
        });

        QueryWrapper<ResourceIcon> wrapper = Pops.<ResourceIcon>query().from(queryWrapper.getModel());
        String sql = RsqlParseHelper.parseRsql2Sql(wrapper.getModel(), rsql);
        if (StringUtils.isNotEmpty(sql)) {
            wrapper.apply(sql);
        }
        return new ResourceIcon().queryPage(page, wrapper);
    }

    @Override
    @Function
    public ResourceIcon update(ResourceIcon data) {
        ResourceIcon rightData = nameValidation(data);
        if (data.getGroupId() == null) {
            rightData.setGroupId(UnGroupData.ID);
        }
        rightData.updateById();
        return rightData;
    }

    @Override
    @Function
    public ResourceIcon deleteOne(ResourceIcon data) {
        new ResourceIcon().deleteById(data.getId());
        //如果图标库下图标为空，则删除图标库
        Long count = new ResourceIcon().count(Pops.<ResourceIcon>lambdaQuery()
                        .from(ResourceIcon.MODEL_MODEL)
                        .eq(ResourceIcon::getLibId,data.getLibId()));
        if (count == 0) {
            new ResourceIconLib().deleteByWrapper(Pops.<ResourceIconLib>lambdaQuery()
                    .from(ResourceIconLib.MODEL_MODEL)
                    .eq(ResourceIconLib::getId, data.getLibId()));
        }
        return data;
    }

    @Override
    @Function
    public ResourceIcon active(ResourceIcon data) {
        Long count = data.queryById().count();
        if (count == 0) {
            throw PamirsException.construct(ExpEnumerate.ICON_QUERY_ERROR).errThrow();
        }
        data.setShow(Boolean.TRUE);
        data.updateById();
        return data;
    }

    @Override
    @Function
    public ResourceIcon disabled(ResourceIcon data) {
        Long count = data.queryById().count();
        if (count == 0) {
            throw PamirsException.construct(ExpEnumerate.ICON_QUERY_ERROR).errThrow();
        }
        data.setShow(Boolean.FALSE);
        data.updateById();
        return data;
    }

    @Override
    @Function
    public ResourceIcon queryIcon(String fullFontClass) {
        ResourceIcon resourceIcon = new ResourceIcon().queryOneByWrapper(Pops.<ResourceIcon>lambdaQuery()
                .from(ResourceIcon.MODEL_MODEL)
                .eq(ResourceIcon::getFullFontClass, fullFontClass));
        if (resourceIcon == null) {
            return new ResourceIcon();
        }
        resourceIcon.fieldQuery(ResourceIconGroup::getId);
        return resourceIcon;
    }

    /**
     * 校验名称是否符合规范
     */
    private ResourceIcon nameValidation(ResourceIcon data) {
        if (data.getDisplayName() == null) {
            throw PamirsException.construct(ExpEnumerate.NAME_IS_EMPTY).errThrow();
        }
        if (data.getDisplayName().length() > 100) {
            throw PamirsException.construct(ExpEnumerate.ICON_NAME_TOO_LONG).errThrow();
        }
        if (data.getRemark() != null && data.getRemark().length() > 500) {
            throw PamirsException.construct(ExpEnumerate.REMARK_NAME_TOO_LONG).errThrow();
        }
        return data;
    }
}
