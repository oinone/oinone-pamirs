package pro.shushi.pamirs.boot.common.api.command;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 生命周期管理可选项
 * <p>
 * 2021/2/25 1:50 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class AppLifecycleOptions implements Serializable {
    private static final long serialVersionUID = -3663016214644872844L;

    // 是否加载存储在数据库中的模块信息
    @JSONField(ordinal = 1)
    private boolean reloadModule = Boolean.FALSE;

    // 校验依赖模块是否安装
    @JSONField(ordinal = 2)
    private boolean checkModule = Boolean.FALSE;

    // 是否读取安装包中元数据
    @JSONField(ordinal = 3)
    private boolean loadMeta = Boolean.TRUE;

    // 是否加载数据库中元数据
    @JSONField(ordinal = 4)
    private boolean reloadMeta = Boolean.FALSE;

    // 是否重算元数据
    @JSONField(ordinal = 5)
    private boolean computeMeta = Boolean.TRUE;

    // 编辑元数据，是否支持编程式编辑元数据
    @JSONField(ordinal = 7)
    private boolean editMeta = Boolean.TRUE;

    // 差量减
    @JSONField(ordinal = 8)
    private boolean diffMeta = Boolean.FALSE;

    // 刷新元数据缓存
    @JSONField(ordinal = 9)
    private boolean refreshSessionMeta = Boolean.TRUE;

    // 刷新重建前后端协议
    @JSONField(ordinal = 10)
    private boolean rebuildHttpApi = Boolean.TRUE;

    // 差量追踪表结构变更
    @JSONField(ordinal = 11)
    private boolean diffTable = Boolean.FALSE;

    // 更新重建表结构
    @JSONField(ordinal = 12)
    private boolean rebuildTable = Boolean.TRUE;

    // 打印重建表结构DDL
    @JSONField(ordinal = 13)
    private boolean printDDL = Boolean.FALSE;

    // 发布服务，是否发布远程服务
    @JSONField(ordinal = 14)
    private boolean publishService = Boolean.TRUE;

    // 分布式模块管理
    @JSONField(ordinal = 15)
    private boolean updateModule = Boolean.FALSE;

    // 初始化与更新元数据，是否将元数据的变更写入数据库
    @JSONField(ordinal = 16)
    private boolean updateMeta = Boolean.FALSE;

    // 初始化与更新业务数据，是否将业务数据的变更写入数据库
    @JSONField(ordinal = 17)
    private boolean updateData = Boolean.TRUE;

    // Jar版本依赖校验，true时可降jar依赖
    @JSONField(ordinal = 18)
    private boolean goBack = Boolean.FALSE;

    // 扩展参数
    @JSONField(ordinal = 19)
    private Map<String, Object> params;

    public AppLifecycleOptions deepClone() {
        return ObjectUtils.clone(this);
    }

}
