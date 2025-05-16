package pro.shushi.pamirs.user.view.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.helper.AuthHelper;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.core.common.VerificationHelper;
import pro.shushi.pamirs.core.common.enmu.TopBarActionType;
import pro.shushi.pamirs.core.common.entry.TopBarAction;
import pro.shushi.pamirs.core.common.path.ResourcePathParser;
import pro.shushi.pamirs.core.common.query.QueryActionCollection;
import pro.shushi.pamirs.core.common.spi.TopBarActionExtendApi;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.user.api.login.UserInfoCache;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.TopBarActionGroup;
import pro.shushi.pamirs.user.api.model.tmodel.TopBarUserBlock;
import pro.shushi.pamirs.user.api.spi.TopBarUserBlockDataApi;

import java.util.*;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.*;

/**
 * 顶部栏展示
 *
 * @author Adamancy Zhang on 2021-03-23 21:14
 */
@Base
@Component
@Model.model(TopBarUserBlock.MODEL_MODEL)
public class TopBarUserBlockAction {

    private static final String ICON_PROPERTY = "icon";

    @Autowired
    private ResourcePathParser resourcePathParser;

    @Function(openLevel = {LOCAL, REMOTE, API})
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public TopBarUserBlock construct(TopBarUserBlock data) {

        setPamirsUser(data);

        fillTopBarActions(data);

        data = Spider.getDefaultExtension(TopBarUserBlockDataApi.class).extendData(data);

        return data;
    }

    private void setPamirsUser(TopBarUserBlock data) {
        PamirsUser user = data.getPamirsUser();
        if (user == null) {
            if (null == PamirsSession.getUserId()) {
                return;
            }
            user = UserInfoCache.queryUserById(PamirsSession.getUserId());
            if (user != null) {
                user = user.fieldQuery(PamirsUser::getAvatarBig);
            } else {
                user = new PamirsUser();
            }
            data.setPamirsUser(user);
        }
    }

    private void fillTopBarActions(TopBarUserBlock data) {
        TopBarActionExtendApi extendApi = Spider.getDefaultExtension(TopBarActionExtendApi.class);
        List<TopBarAction> topBarActions = TopBarAction.getDefaultActions();
        extendApi.edit(topBarActions);

        TopBarAction userAvatar = null;
        Map<String, TopBarAction> queryActionMap = new LinkedHashMap<>(topBarActions.size());
        Map<String, TopBarAction> queryAlternateActionMap = new LinkedHashMap<>(topBarActions.size());
        for (TopBarAction action : topBarActions) {
            verificationAndSet(action);
            String actionSign = getActionSign(action);
            if (TopBarActionType.USER_AVATAR.equals(action.getType())) {
                userAvatar = action;
            } else {
                assertNull(action, TopBarAction::getGroupOrder);
                queryActionMap.put(actionSign, action);
            }
            Optional.ofNullable(action.getAlternates())
                    .map(Collection::stream)
                    .ifPresent(v -> v.forEach(alternateAction -> {
                        verificationAndSet(alternateAction);
                        String alternateActionSign = getActionSign(alternateAction);
                        queryAlternateActionMap.put(alternateActionSign, alternateAction);
                    }));
        }

        QueryActionCollection queryCollection = new QueryActionCollection();

        AuthApi authApi = AuthApi.get();

        if (userAvatar != null && filterAction(authApi, userAvatar)) {
            addAction(queryCollection, userAvatar);
        }

        queryActionMap.values().forEach(topBarAction -> {
            if (filterAction(authApi, topBarAction)) {
                addAction(queryCollection, topBarAction);
            }
        });
        queryAlternateActionMap.values().forEach(topBarAction -> {
            if (filterAction(authApi, topBarAction)) {
                addAction(queryCollection, topBarAction);
            }
        });

        queryCollection.fill();

        if (userAvatar != null) {
            Action action = getAction(queryCollection, userAvatar);
            if (action == null) {
                action = getAlternateAction(queryCollection, userAvatar);
            }
            if (action != null) {
                fillValue(extendApi, action, userAvatar);
                data.setUserAvatarAction(action);
            }
        }

        Map<Integer, TopBarActionGroup> groups = new HashMap<>(4);
        for (TopBarAction topBarAction : queryActionMap.values()) {
            if (!TopBarActionType.NORMAL.equals(topBarAction.getType())) {
                continue;
            }
            Action action = getAction(queryCollection, topBarAction);
            if (action == null) {
                action = getAlternateAction(queryCollection, topBarAction);
                if (action == null) {
                    continue;
                }
            }
            fillValue(extendApi, action, topBarAction);
            groups.computeIfAbsent(topBarAction.getGroupOrder(), k -> {
                TopBarActionGroup newGroup = new TopBarActionGroup();
                newGroup.setActions(new ArrayList<>(4));
                newGroup.setPriority(k);
                return newGroup;
            }).getActions().add(action);
        }
        data.setActionGroups(groups.values().stream().sorted(Comparator.comparing(TopBarActionGroup::getPriority)).collect(Collectors.toList()));
    }

    private void verificationAndSet(TopBarAction topBarAction) {
        assertNotBlank(topBarAction, TopBarAction::getModule);
        assertNull(topBarAction, TopBarAction::getActionType);
        VerificationHelper.setDefaultValue(topBarAction, TopBarAction::getType, TopBarAction::setType, TopBarActionType.NORMAL);

        String sessionPath = AuthHelper.generatorTopBarActionPath(topBarAction);
        topBarAction.setSessionPath(sessionPath);
        topBarAction.setInfo(resourcePathParser.parseAccessInfo(sessionPath));
    }

    private boolean filterAction(AuthApi authApi, TopBarAction action) {
        AccessResourceInfo info = action.getInfo();
        boolean isActionPath = false;
        if (info == null) {
            info = new AccessResourceInfo();
            info.setModule(action.getModule());
            info.setModel(action.getModel());
            info.setActionName(action.getName());
            info.setPath(action.getSessionPath());
            info.setIsFixed(true);
            isActionPath = true;
        } else if (info.isActionPath()) {
            isActionPath = info.isActionPath();
        }
        if (isActionPath) {
            AccessResourceInfo oldInfo = AccessResourceInfoSession.getInfo();
            try {
                AccessResourceInfoSession.setInfo(info);
                return authApi.canAccessAction(action.getModel(), action.getName()).getSuccess();
            } finally {
                AccessResourceInfoSession.setInfo(oldInfo);
            }
        }
        String sessionPath = action.getSessionPath();
        AccessResourceInfo oldInfo = AccessResourceInfoSession.getInfo();
        try {
            AccessResourceInfoSession.setInfo(info);
            return authApi.canAccessAction(sessionPath).getSuccess();
        } finally {
            AccessResourceInfoSession.setInfo(oldInfo);
        }
    }

    private void fillValue(TopBarActionExtendApi extendApi, Action action, TopBarAction topBarAction) {
        String displayName = topBarAction.getDisplayName();
        if (StringUtils.isNotBlank(displayName)) {
            action.setDisplayName(displayName);
        }
        String icon = topBarAction.getIcon();
        if (StringUtils.isNotBlank(icon)) {
            Map<String, Object> attributes = Optional.ofNullable(action.getAttributes()).orElse(new HashMap<>(2));
            attributes.put(ICON_PROPERTY, icon);
            action.setAttributes(attributes);
        }
        String sessionPath = topBarAction.getSessionPath();
        if (StringUtils.isNotBlank(sessionPath)) {
            action.setSessionPath(sessionPath);
        }
        String model = action.getModel();
        if (StringUtils.isNotBlank(model)) {
            ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
            if (modelConfig != null) {
                ModelDefinition modelDefinition = new ModelDefinition();
                modelDefinition.setDisplayName(modelConfig.getDisplayName());
                modelDefinition.setModel(model);
                modelDefinition.setName(modelConfig.getName());
                action.setModelDefinition(modelDefinition);
            }
        }
        extendApi.fill(action, topBarAction);
    }

    private String getActionSign(TopBarAction action) {
        return Action.sign(assertNotBlank(action, TopBarAction::getModel), assertNotBlank(action, TopBarAction::getName));
    }

    private <T, R> void assertNull(T data, Getter<T, R> getter) {
        R value = getter.apply(data);
        if (value == null) {
            throw new IllegalArgumentException("Invalid parameter. field: " + LambdaUtil.fetchFieldName(getter));
        }
    }

    private <T> String assertNotBlank(T data, Getter<T, String> getter) {
        String value = getter.apply(data);
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Invalid parameter. field: " + LambdaUtil.fetchFieldName(getter));
        }
        return value;
    }

    private void addAction(QueryActionCollection queryCollection, TopBarAction topBarAction) {
        queryCollection.add(topBarAction.getModel(), topBarAction.getName(), topBarAction.getActionType());
    }

    private <T extends Action> T getAction(QueryActionCollection queryCollection, TopBarAction topBarAction) {
        return queryCollection.get(topBarAction.getModel(), topBarAction.getName(), topBarAction.getActionType());
    }

    private <T extends Action> T getAlternateAction(QueryActionCollection queryCollection, TopBarAction topBarAction) {
        List<TopBarAction> alternateActions = topBarAction.getAlternates();
        if (CollectionUtils.isEmpty(alternateActions)) {
            return null;
        }
        for (TopBarAction alternateAction : alternateActions) {
            T action = getAction(queryCollection, alternateAction);
            if (action != null) {
                return action;
            }
        }
        return null;
    }
}