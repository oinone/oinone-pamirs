package pro.shushi.pamirs.framework.connectors.data.plugin;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;

import java.util.List;

/**
 * 自定义sql注入器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/13 10:57 下午
 */
public class MyLogicSqlInjector extends DefaultSqlInjector {

    /**
     * 如果只需增加方法，保留MP自带方法
     * 可以super.getMethodList() 再add
     *
     * @return 返回注入方法
     */
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        // 这里可以给methodList添加更多SQL注入器
        return super.getMethodList(mapperClass);
    }

}
