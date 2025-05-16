package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.extend;

import org.apache.ibatis.annotations.Options;
import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 可选项处理器
 * <p>
 * 2020/7/1 4:09 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class OptionsResolver {

    public static OptionsDTO resolve(Options options) {
        if (null == options) {
            return null;
        }
        return new OptionsDTO()
                .setUseGeneratedKeys(options.useGeneratedKeys())
                .setKeyProperty(options.keyProperty())
                .setKeyColumn(options.keyColumn());
    }

    @Data
    public static class OptionsDTO {

        private boolean useGeneratedKeys = false;

        private String keyProperty;

        private String keyColumn;

    }

}
