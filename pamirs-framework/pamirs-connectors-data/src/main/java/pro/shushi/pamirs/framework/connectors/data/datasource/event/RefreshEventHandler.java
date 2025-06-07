package pro.shushi.pamirs.framework.connectors.data.datasource.event;

/**
 * 配置刷新预处理类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 3:18 上午
 */
public interface RefreshEventHandler {

    boolean needHandle(String changeKey);

    void handle(RefreshContext context, String changeKey);

}
