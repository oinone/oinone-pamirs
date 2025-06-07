package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;

import java.util.List;

/**
 * 事务配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(21)
@Base
@Model.Advanced(unique = {"namespace,fun"}, priority = 18)
@Model.model(TransactionConfig.MODEL_MODEL)
@Model(displayName = "事务配置", summary = "事务配置")
public class TransactionConfig extends MetaBaseModel {

    private static final long serialVersionUID = 4965481019645832875L;

    public static final String MODEL_MODEL = "base.Transaction";

    @Base
    @Field(displayName = "生效", defaultValue = "true")
    private Boolean active;

    @Base
    @Field(displayName = "分布式事务", defaultValue = "false")
    private Boolean enableXa;

    @Base
    @Field(displayName = "事务管理器")
    private String transactionManager;

    @Base
    @Field(displayName = "函数命名空间", required = true)
    private String namespace;

    @Base
    @Field(displayName = "函数编码", required = true)
    private String fun;

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

    @Base
    @Field(displayName = "回滚异常编码")
    private List<String> rollbackForExpCode;

    @Base
    @Field(displayName = "忽略异常编码")
    private List<String> noRollbackForExpCode;

    @SuppressWarnings("unchecked")
    private Class<? extends Throwable>[] rollbackFor = new Class[]{};

    private String[] rollbackForClassName = new String[]{};

    @SuppressWarnings("unchecked")
    private Class<? extends Throwable>[] noRollbackFor = new Class[]{};

    private String[] noRollbackForClassName = new String[]{};

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

}