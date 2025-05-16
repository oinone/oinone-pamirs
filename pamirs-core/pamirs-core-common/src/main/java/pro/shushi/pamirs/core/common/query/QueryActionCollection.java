package pro.shushi.pamirs.core.common.query;

import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;

import java.util.List;

/**
 * 通过模型和动作批量查询全部类型动作
 *
 * @author Adamancy Zhang at 20:46 on 2024-02-28
 */
public class QueryActionCollection {

    private final QueryActions<ServerAction> queryServerActions = new QueryActions<>(ActionTypeEnum.SERVER);

    private final QueryActions<ViewAction> queryViewActions = new QueryActions<>(ActionTypeEnum.VIEW);

    private final QueryActions<UrlAction> queryUrlActions = new QueryActions<>(ActionTypeEnum.URL);

    private final QueryActions<ClientAction> queryClientActions = new QueryActions<>(ActionTypeEnum.CLIENT);

    private List<ServerAction> serverActions;

    private List<ViewAction> viewActions;

    private List<UrlAction> urlActions;

    private List<ClientAction> clientActions;

    private MemoryListSearchCache<String, ServerAction> serverActionCache;

    private MemoryListSearchCache<String, ViewAction> viewActionCache;

    private MemoryListSearchCache<String, UrlAction> urlActionCache;

    private MemoryListSearchCache<String, ClientAction> clientActionCache;

    public void add(String model, String name, ActionTypeEnum actionType) {
        switch (actionType) {
            case SERVER:
                queryServerActions.add(model, name);
                break;
            case VIEW:
                queryViewActions.add(model, name);
                break;
            case URL:
                queryUrlActions.add(model, name);
                break;
            case CLIENT:
                queryClientActions.add(model, name);
                break;
            default:
                throw new IllegalArgumentException("Invalid action type.");
        }
    }

    public void fill() {
        serverActions = queryServerActions.query();
        viewActions = queryViewActions.query();
        urlActions = queryUrlActions.query();
        clientActions = queryClientActions.query();
        serverActionCache = new MemoryListSearchCache<>(serverActions, v -> Action.sign(v.getModel(), v.getName()));
        viewActionCache = new MemoryListSearchCache<>(viewActions, v -> Action.sign(v.getModel(), v.getName()));
        urlActionCache = new MemoryListSearchCache<>(urlActions, v -> Action.sign(v.getModel(), v.getName()));
        clientActionCache = new MemoryListSearchCache<>(clientActions, v -> Action.sign(v.getModel(), v.getName()));
    }

    /**
     * PS: 获取时可根据ActionType类型进行强制转换
     */
    @SuppressWarnings("unchecked")
    public <T extends Action> T get(String model, String name, ActionTypeEnum actionType) {
        String sign = Action.sign(model, name);
        switch (actionType) {
            case SERVER:
                return (T) serverActionCache.get(sign);
            case VIEW:
                return (T) viewActionCache.get(sign);
            case URL:
                return (T) urlActionCache.get(sign);
            case CLIENT:
                return (T) clientActionCache.get(sign);
            default:
                throw new IllegalArgumentException("Invalid action type.");
        }
    }

    public List<ServerAction> getServerActions() {
        return serverActions;
    }

    public List<ViewAction> getViewActions() {
        return viewActions;
    }

    public List<UrlAction> getUrlActions() {
        return urlActions;
    }

    public List<ClientAction> getClientActions() {
        return clientActions;
    }
}