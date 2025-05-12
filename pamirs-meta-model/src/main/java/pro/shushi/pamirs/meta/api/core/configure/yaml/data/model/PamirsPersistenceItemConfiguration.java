package pro.shushi.pamirs.meta.api.core.configure.yaml.data.model;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * pamirs数据持久层配置信息
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 11:03 上午
 */
@Data
public class PamirsPersistenceItemConfiguration {

    /**
     * 是否自动创建数据库
     */
    private Boolean autoCreateDatabase = Boolean.TRUE;

    /**
     * 创建数据库超时时间；单位：秒
     */
    private int createDatabaseTimeout = 5;

    /**
     * 是否自动创建数据库表
     */
    private Boolean autoCreateTable = Boolean.TRUE;

    /**
     * 连接有效超时时间；单位：毫秒
     */
    private int connectionValidTimeout = 5000;

}
