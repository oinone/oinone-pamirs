package pro.shushi.pamirs.core.common.standard.action.impl;

import pro.shushi.pamirs.locale.utils.I18nUtils;
import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.enmu.CommonExpEnumerate;
import pro.shushi.pamirs.core.common.standard.action.StandardModelAction;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.ux.common.utils.WrapperHelper;

import java.util.Collections;
import java.util.List;

/**
 * 标准模型动作抽象实现
 *
 * @author Adamancy Zhang on 2021-05-18 11:21
 */
public abstract class AbstractStandardModelAction<T extends BaseModel, S extends StandardModelService<T>> implements StandardModelAction<T> {

    /**
     * 获取标准模型服务
     *
     * @return 标准模型服务
     */
    protected abstract S fetchService();

    @Override
    public T construct(T data) {
        return data.construct();
    }

    @Override
    public T constructMirror(T data) {
        return data;
    }

    @Override
    public T create(T data) {
        data = verificationAndSet(data, false);
        if (data == null) {
            return null;
        }
        data = fetchService().create(data);
        if (data == null) {
            PamirsSession.getMessageHub()
                    .msg(Message.init().setLevel(InformationLevelEnum.ERROR)
                            .setMessage(I18nUtils.getMessage("pamirs-core-common.AbstractStandardModelAction.creationFailed")));
        } else {
            PamirsSession.getMessageHub()
                    .msg(Message.init().setLevel(InformationLevelEnum.SUCCESS)
                            .setMessage(I18nUtils.getMessage("pamirs-core-common.AbstractStandardModelAction.createdSuccessfully")));
        }
        return data;
    }

    @Override
    public T update(T data) {
        data = verificationAndSet(data, true);
        if (data == null) {
            return null;
        }
        data = fetchService().update(data);
        if (data == null) {
            PamirsSession.getMessageHub()
                    .msg(Message.init().setLevel(InformationLevelEnum.ERROR)
                            .setMessage(I18nUtils.getMessage("pamirs-core-common.AbstractStandardModelAction.updateFailed")));
        } else {
            PamirsSession.getMessageHub()
                    .msg(Message.init().setLevel(InformationLevelEnum.SUCCESS)
                            .setMessage(I18nUtils.getMessage("pamirs-core-common.AbstractStandardModelAction.updateSuccessful")));
        }
        return data;
    }

    @Override
    public List<T> delete(List<T> list) {
        deleteBeforeVerification(list);
        int total = list.size();
        list = fetchService().delete(list);
        int deleteSize = list.size();
        if (total == deleteSize) {
            PamirsSession.getMessageHub()
                    .msg(Message.init().setLevel(InformationLevelEnum.SUCCESS)
                            .setMessage(I18nUtils.getMessage("pamirs-core-common.AbstractStandardModelAction.batchDeletionOfAllSuccessful")));
        } else {
            PamirsSession.getMessageHub()
                    .msg(Message.init().setLevel(InformationLevelEnum.SUCCESS)
                            .setMessage(I18nUtils.getMessage("pamirs-core-common.AbstractStandardModelAction.batchDeleteSuccess", total, deleteSize)));
        }
        return list;
    }

    @Override
    public T deleteOne(T data) {
        deleteBeforeVerification(Collections.singletonList(data));
        data = fetchService().deleteOne(data);
        if (data == null) {
            PamirsSession.getMessageHub()
                    .msg(Message.init().setLevel(InformationLevelEnum.ERROR)
                            .setMessage(I18nUtils.getMessage("pamirs-core-common.AbstractStandardModelAction.deletionFailed")));
        } else {
            PamirsSession.getMessageHub()
                    .msg(Message.init().setLevel(InformationLevelEnum.SUCCESS)
                            .setMessage(I18nUtils.getMessage("pamirs-core-common.AbstractStandardModelAction.deletedSuccessfully")));
        }
        return data;
    }

    @Override
    public Pagination<T> queryPage(Pagination<T> page, IWrapper<T> queryWrapper) {
        Pagination<T> pagination = fetchService().queryPage(page, WrapperHelper.lambda(queryWrapper));
        List<T> list = pagination.getContent();
        if (CollectionUtils.isNotEmpty(list)) {
            queryAfterProperties(list);
        }
        return pagination;
    }

    @Override
    public T queryOne(T query) {
        query = fetchService().queryOne(query);
        if (query == null) {
            PamirsSession.getMessageHub()
                    .msg(Message.init().setLevel(InformationLevelEnum.SUCCESS)
                            .setMessage(I18nUtils.getMessage("pamirs-core-common.AbstractStandardModelAction.theQueryRecordDoesNotExistPlea")));
        } else {
            queryAfterProperties(Collections.singletonList(query));
        }
        return query;
    }

    /**
     * 创建或更新时校验
     *
     * @param data     数据
     * @param isUpdate 是否更新 {@link AbstractStandardModelAction#create(T)} is false, {@link AbstractStandardModelAction#update(T)} is true
     */
    protected T verificationAndSet(T data, boolean isUpdate) {
        if (isUpdate) {
            T origin = FetchUtil.fetchOne(data);
            if (origin == null) {
                throw PamirsException.construct(CommonExpEnumerate.PLEASE_REFRESH_PAGE).errThrow();
            }
            data = updateBeforeVerification(origin, data);
        } else {
            data = createBeforeVerification(data);
        }
        return data;
    }

    protected T checkVerify(T data) {
        data = fetchService().queryOne(data);
        if (data == null) {
            throw PamirsException.construct(CommonExpEnumerate.PLEASE_REFRESH_PAGE).errThrow();
        }
        return data;
    }

    protected void checkExist(T data) {
        if (FetchUtil.consumerQueryWrapper(data, () -> 0L, wrapper -> fetchService().count(wrapper.lambda())) != 1) {
            throw PamirsException.construct(CommonExpEnumerate.PLEASE_REFRESH_PAGE).errThrow();
        }
    }

    /**
     * 创建前校验
     *
     * @param data 数据
     */
    protected T createBeforeVerification(T data) {
        return data;
    }

    /**
     * 更新前校验
     *
     * @param origin 数据库原始数据
     * @param data   数据
     */
    protected T updateBeforeVerification(T origin, T data) {
        return data;
    }

    /**
     * 查询后处理
     *
     * @param list 查询列表
     */
    protected void queryAfterProperties(List<T> list) {
    }

    protected void deleteBeforeVerification(List<T> list) {
    }
}
