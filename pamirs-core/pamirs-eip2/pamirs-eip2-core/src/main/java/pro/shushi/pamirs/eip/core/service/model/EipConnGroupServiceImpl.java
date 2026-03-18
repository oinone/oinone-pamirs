package pro.shushi.pamirs.eip.core.service.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.model.EipConnGroup;
import pro.shushi.pamirs.eip.api.model.EipIntegrate;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.service.model.EipConnGroupService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.List;

@Component
public class EipConnGroupServiceImpl implements EipConnGroupService {

    @Override
    public <T extends EipConnGroup> T create(T data) {
        if (data == null) {
            throw PamirsException.construct(EipExpEnumerate.CONN_GROUP_CHECK_ERROR).errThrow();
        }
        if (StringUtils.isEmpty(data.getName())) {
            throw PamirsException.construct(EipExpEnumerate.CONN_GROUP_CHECK_ERROR).appendMsg(I18nUtils.getMessage("pamirs.eip.connGroup.name.empty")).errThrow();
        }
        if (Models.origin().count(
                Pops.<EipConnGroup>lambdaQuery()
                        .from(EipConnGroup.MODEL_MODEL)
                        .eq(EipConnGroup::getName, data.getName())
        ) > 0) {
            throw PamirsException.construct(EipExpEnumerate.CONN_GROUP_CHECK_ERROR).appendMsg(I18nUtils.getMessage("pamirs.eip.connGroup.name.duplicate")).errThrow();
        }
        return data.create();
    }

    @Override
    public <T extends EipConnGroup> List<T> delete(List<T> list) {
        // 不支持删除
        throw PamirsException.construct(EipExpEnumerate.SYSTEM_ERROR).errThrow();
    }

    @Override
    public <T extends EipConnGroup> T deleteOne(T data) {
        if (data == null) {
            throw PamirsException.construct(EipExpEnumerate.CONN_GROUP_CHECK_ERROR).errThrow();
        }
        EipConnGroup exist = data.queryById();
        if (exist == null) {
            throw PamirsException.construct(EipExpEnumerate.CONN_GROUP_CHECK_ERROR).appendMsg(I18nUtils.getMessage("pamirs.eip.connGroup.notExist")).errThrow();
        }

        // 校验引用
        if (Models.origin().count(
                Pops.<EipOpenInterface>lambdaQuery()
                        .from(EipOpenInterface.MODEL_MODEL)
                        .eq(EipOpenInterface::getConnGroupCode, exist.getCode())
        ) > 0) {
            throw PamirsException.construct(EipExpEnumerate.CONN_GROUP_DELETE_RELATION).errThrow();
        }

        if (Models.origin().count(
                Pops.<EipIntegrationInterface>lambdaQuery()
                        .from(EipIntegrationInterface.MODEL_MODEL)
                        .eq(EipIntegrationInterface::getConnGroupCode, exist.getCode())
        ) > 0) {
            throw PamirsException.construct(EipExpEnumerate.CONN_GROUP_DELETE_RELATION).errThrow();
        }

        if (Models.origin().count(
                Pops.<EipIntegrate>lambdaQuery()
                        .from(EipIntegrate.MODEL_MODEL)
                        .eq(EipIntegrate::getGroupCode, exist.getCode())
        ) > 0) {
            throw PamirsException.construct(EipExpEnumerate.CONN_GROUP_DELETE_RELATION).errThrow();
        }

        exist.deleteById();
        return data;
    }

}
