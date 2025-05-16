package pro.shushi.pamirs.resource.core.service;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.TranslateUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;
import pro.shushi.pamirs.resource.api.model.ResourceIcon;
import pro.shushi.pamirs.resource.api.model.ResourceIconGroup;
import pro.shushi.pamirs.resource.api.model.ResourceIconLib;
import pro.shushi.pamirs.resource.api.service.ResourceIconGroupService;
import pro.shushi.pamirs.resource.api.util.UnGroupData;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Fun(ResourceIconGroupService.FUN_NAMESPACE)
@Component
public class ResourceIconGroupServiceImpl implements ResourceIconGroupService {

    @Override
    @Function
    public List<ResourceIconGroup> queryAllGroupData() {
        List<ResourceIconGroup> groupList = Models.origin().queryListByWrapper(Pops.<ResourceIconGroup>lambdaQuery()
                .from(ResourceIconGroup.MODEL_MODEL)
                .orderByAsc(ResourceIconGroup::getWriteDate));
        List<ResourceIconGroup> resourceIconGroupList = groupList.stream().map(
                item -> {
                    if (item.getId() == 0) {
                        item.setName(TranslateUtils.translateValues(item.getName()));
                    }
                    int size = new ResourceIcon().queryList(Pops.<ResourceIcon>lambdaQuery()
                            .from(ResourceIcon.MODEL_MODEL)
                            .eq(ResourceIcon::getGroupId, item.getId())).size();
                    item.setIconNum((long) size);
                    return item;
                }
        ).collect(Collectors.toList());
        return resourceIconGroupList;
    }

    @Override
    @Function
    public List<ResourceIconGroup> queryGroupData() {
        List<ResourceIconGroup> groupList = Models.origin().queryListByWrapper(Pops.<ResourceIconGroup>lambdaQuery()
                .from(ResourceIconGroup.MODEL_MODEL)
                .orderByAsc(ResourceIconGroup::getWriteDate));
        return groupList;
    }

    @Override
    @Function
    public ResourceIconGroup create(ResourceIconGroup data) {
        ResourceIconGroup rightData = nameValidation(data);
        int count = data.queryList(Pops.<ResourceIconGroup>lambdaQuery()
                        .from(ResourceIconGroup.MODEL_MODEL)
                        .eq(ResourceIconGroup::getName, data.getName()))
                .size();
        if (count > 0) {
            throw PamirsException.construct(ExpEnumerate.GROUP_NAME_EXISTS).errThrow();
        }
        rightData.setSys(Boolean.FALSE);
        rightData.setBatchCode(0L);
        rightData.create();
        return rightData;
    }

    @Override
    @Function
    public ResourceIconGroup update(ResourceIconGroup data) {
        ResourceIconGroup rightData = nameValidation(data);
        rightData.updateById();
        return rightData;
    }

    @Override
    @Function
    public ResourceIconGroup deleteOne(ResourceIconGroup data) {
        //不能删除未分组
        if (Objects.equals(data.getName(), UnGroupData.NAME)) {
            throw PamirsException.construct(ExpEnumerate.UNGROUPED_GROUPS).errThrow();
        }
        //将该分组下面的图标改为未分组
        new ResourceIcon().updateByWrapper(new ResourceIcon().setGroupId(UnGroupData.ID),
                Pops.<ResourceIcon>lambdaUpdate()
                        .from(ResourceIcon.MODEL_MODEL)
                        .eq(ResourceIcon::getGroupId, data.getId()));
        new ResourceIconLib().updateByWrapper(new ResourceIconLib().setGroupId(UnGroupData.ID),
                Pops.<ResourceIconLib>lambdaUpdate()
                        .from(ResourceIconLib.MODEL_MODEL)
                        .eq(ResourceIconLib::getGroupId, data.getId()));
        new ResourceIconGroup().deleteById(data.getId());
        return data;
    }

    /*
      校验名称是否符合规范
     */
    private ResourceIconGroup nameValidation(ResourceIconGroup data) {
        if (data.getName() == null) {
            throw PamirsException.construct(ExpEnumerate.GROUP_NAME_IS_EMPTY).errThrow();
        }
        if (data.getName().length() > 100) {
            throw PamirsException.construct(ExpEnumerate.GROUP_NAME_TOO_LONG).errThrow();
        }
        return data;
    }
}
