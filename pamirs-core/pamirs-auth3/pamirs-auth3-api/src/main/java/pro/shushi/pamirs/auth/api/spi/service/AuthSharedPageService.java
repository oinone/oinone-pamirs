package pro.shushi.pamirs.auth.api.spi.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.loader.PermissionNodeLoader;
import pro.shushi.pamirs.auth.api.model.AuthSharedPageRecord;
import pro.shushi.pamirs.auth.api.runtime.session.AuthSharedAuthorizationSession;
import pro.shushi.pamirs.auth.api.service.AuthSharedPageRecordService;
import pro.shushi.pamirs.boot.base.model.SharedPage;
import pro.shushi.pamirs.boot.base.model.SharedPageViewAction;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.enmu.SharedExpEnumerate;
import pro.shushi.pamirs.boot.web.loader.PageLoadAction;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.boot.web.service.SharedPageService;
import pro.shushi.pamirs.boot.web.service.impl.DefaultSharedPageServiceImpl;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.core.common.CopyHelper;
import pro.shushi.pamirs.core.common.DateHelper;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 分享页面服务
 *
 * @author Adamancy Zhang at 22:18 on 2024-04-11
 */
@Order(88)
@Service
@Fun(SharedPageService.FUN_NAMESPACE)
public class AuthSharedPageService extends DefaultSharedPageServiceImpl implements SharedPageService {

    @Autowired
    private PermissionNodeLoader permissionNodeLoader;

    @Autowired
    public PageLoadAction pageLoadAction;

    @Autowired
    private AuthSharedPageRecordService authSharedPageRecordService;

    @Function
    @Override
    public SharedPage init(SharedPage page) {
        return shared(page);
    }

    @Function
    @Override
    public SharedPage shared(SharedPage page) {
        return super.shared(page);
    }

    @Function
    @Override
    public SharedPageViewAction load(SharedPageViewAction page) {
        return super.load(page);
    }

    @Override
    protected SharedPage shared(SharedPage page, String sharedOrigin, ViewAction shareAction, ViewAction sharedViewAction, Map<String, Object> parameters) {
        SharedPage sharedPage = super.shared(page, sharedOrigin, shareAction, sharedViewAction, parameters);
        String sharedCode = sharedPage.getSharedCode();
        String sharedModule = sharedViewAction.getModule();
        String sharedModel = sharedViewAction.getModel();
        String sharedAction = sharedViewAction.getName();
        List<PermissionNode> nodes = permissionNodeLoader.getManagementLoader().buildNextPermissionsByViewAction(new PermissionNode(), sharedViewAction);
        Set<String> paths = new HashSet<>();
        AccessResourceInfo info = new AccessResourceInfo();
        info.setModel(sharedModel);
        info.setActionName(sharedAction);
        paths.add(ResourcePath.PATH_SPLIT + sharedModule);
        paths.add(info.toString());
        collectionPaths(paths, nodes);

        int timeout = 2;
        TimeUnit timeoutUnit = TimeUnit.HOURS;
        Date invalidTime = DateHelper.addValue(new Date(), timeoutUnit, timeout);
        String authorizationCode = EncryptHelper.shortCode(sharedCode);

        Tx.build().executeWithoutResult((status) -> {
            AuthSharedPageRecord record = new AuthSharedPageRecord();
            record.setSharedCode(sharedCode);
            record.setAuthorizationCode(authorizationCode);
            record.setSharedModule(sharedModule);
            record.setSharedModel(sharedModel);
            record.setSharedAction(sharedAction);
            record.setSharedParameters(sharedPage.getParameters());
            record.setBrowserTitle(sharedPage.getBrowserTitle());
            record.setLanguage(sharedPage.getLanguage());
            record.setLanguageIsoCode(sharedPage.getLanguageIsoCode());
            record.setTimeout(timeout);
            record.setTimeoutUnit(timeoutUnit.name());
            record.setInvalidTime(invalidTime);
            record.setUrl(sharedPage.getUrl());
            record.setLinkText(sharedPage.getLinkText());
            record.setPaths(JSON.toJSONString(paths));
            authSharedPageRecordService.create(record);

            AuthApiHolder.getAuthSharedCodeCacheService().set(sharedCode, authorizationCode, timeout, timeoutUnit);
            AuthApiHolder.getAuthSharedPageCacheService().set(authorizationCode, paths, timeout, timeoutUnit);
        });
        return sharedPage;
    }

    @Override
    protected SharedPageViewAction load(String sharedCode) {
        AuthSharedPageRecord record = authSharedPageRecordService.queryOneByWrapper(Pops.<AuthSharedPageRecord>lambdaQuery()
                .from(AuthSharedPageRecord.MODEL_MODEL)
                .eq(AuthSharedPageRecord::getSharedCode, sharedCode));
        if (record == null) {
            throw PamirsException.construct(SharedExpEnumerate.INVALID_SHARED_PAGE).errThrow();
        }

        String module = record.getSharedModule();
        String model = record.getSharedModel();
        String name = record.getSharedAction();
        AccessResourceInfo info = new AccessResourceInfo();
        info.setModule(module);
        info.setModel(model);
        info.setActionName(name);
        AccessResourceInfoSession.setInfo(info);
        AuthSharedAuthorizationSession.setSession(record.getSharedCode(), record.getAuthorizationCode());

        ViewAction viewAction = pageLoadAction.load(new ViewAction()
                .setModel(record.getSharedModel())
                .setName(record.getSharedAction())
                .setNeedCompileView(Boolean.TRUE));
        SharedPageViewAction sharedPage = CopyHelper.simpleReplace(viewAction, new SharedPageViewAction());
        sharedPage.setSharedCode(sharedCode);
        sharedPage.setAuthorizationCode(record.getAuthorizationCode());
        sharedPage.setSharedParameters(record.getSharedParameters());
        sharedPage.setBrowserTitle(record.getBrowserTitle());
        sharedPage.setLanguage(record.getLanguage());
        sharedPage.setLanguageIsoCode(record.getLanguageIsoCode());
        return sharedPage;
    }

    @Override
    protected String generatorSharedCode() {
        String sharedCode = UUIDUtil.getUUIDNumberString();
        while (authSharedPageRecordService.count(Pops.<AuthSharedPageRecord>lambdaQuery()
                .from(AuthSharedPageRecord.MODEL_MODEL)
                .eq(AuthSharedPageRecord::getSharedCode, sharedCode)).compareTo(1L) >= 0) {
            sharedCode = UUIDUtil.getUUIDNumberString();
        }
        return sharedCode;
    }

    /**
     * 收集权限节点路径
     *
     * @param paths 路径集合
     * @param nodes 权限节点集合
     */
    protected void collectionPaths(Set<String> paths, List<PermissionNode> nodes) {
        for (PermissionNode node : nodes) {
            if (node instanceof ActionPermissionNode) {
                ActionPermissionNode actionPermissionNode = (ActionPermissionNode) node;
                if ("SharedPageViewAction".equals(actionPermissionNode.getAction())) {
                    continue;
                }
            }
            String path = node.getPath();
            if (StringUtils.isNotBlank(path)) {
                paths.add(path);
            }
            List<PermissionNode> children = node.getNodes();
            if (CollectionUtils.isNotEmpty(children)) {
                collectionPaths(paths, children);
            }
        }
    }
}
