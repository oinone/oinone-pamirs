package pro.shushi.pamirs.framework.connectors.data.dialect;

import pro.shushi.pamirs.framework.connectors.data.dialect.factory.DialectComponentFactory;

/**
 * 方言管理器
 * <p>
 * 2020/8/8 12:43 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class Dialects {

    public static <T> T component(Class<T> componentClass, String dsKey) {
        return DialectComponentFactory.component(componentClass, dsKey);
    }

}
