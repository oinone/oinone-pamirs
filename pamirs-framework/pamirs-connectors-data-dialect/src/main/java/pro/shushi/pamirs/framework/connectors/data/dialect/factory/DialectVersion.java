package pro.shushi.pamirs.framework.connectors.data.dialect.factory;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.enmu.DataSourceEnum;

/**
 * 方言组件版本
 * <p>
 * 2020/8/8 12:25 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class DialectVersion {

    private String type;

    private String version;

    private String majorVersion;

    public DataSourceEnum type() {
        if (null == type) {
            return null;
        }
        return DataSourceEnum.valueOf(type);
    }

    public String getTypeAndVersion() {
        return this.getType() + this.getVersion();
    }

    public String getTypeAndMajorVersion() {
        return this.getType() + this.getMajorVersion();
    }

}
