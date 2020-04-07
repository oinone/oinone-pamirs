package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * 事务配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.Transaction")
@Model(displayName = "事务配置", summary = "事务配置")
public class TransactionConfig extends IdModel {

    @Base
    @Field(displayName = "生效", defaultValue = "true")
    private Boolean active;

    @Base
    @Field(displayName = "函数命名空间", required = true)
    private String namespace;

    @Base
    @Field(displayName = "函数编码", required = true)
    private String fun;

    @Base
    @Field(displayName = "数据源名称", required = true)
    private String ds;

    @Base
    @Field(displayName = "事务隔离级别", defaultValue = "-1")
    private Integer isolation;

    @Base
    @Field(displayName = "事务传递类型", defaultValue = "0")
    private Integer propagation;

    @Base
    @Field(displayName = "过期时间", defaultValue = "-1")
    private Integer timeout;

    @Base
    @Field(displayName = "只读", defaultValue = "false")
    private Boolean readOnly;

}