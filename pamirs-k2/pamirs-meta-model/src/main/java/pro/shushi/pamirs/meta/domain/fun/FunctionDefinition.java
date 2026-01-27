package pro.shushi.pamirs.meta.domain.fun;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.PackageConstants;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.DiffUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static pro.shushi.pamirs.meta.domain.fun.FunctionDefinition.MODEL_MODEL;

/**
 * 函数定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(core = Method.class)
@Base
@Model.Advanced(unique = {"namespace, fun"}, index = {"namespace, name", "category"}, priority = 17)
@Model.model(MODEL_MODEL)
@Model(displayName = "函数", summary = "函数", labelFields = {"displayName", "name"})
public class FunctionDefinition extends AbstractFunction {

    public static final String MODEL_MODEL = "base.Function";

    private static final long serialVersionUID = 7483208537239843128L;

    @Base
    @Field.String
    @Field(displayName = "显示名称", translate = true, required = true)
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "模块", required = true)
    private String module;

    @Base
    @Field.String
    @Field(displayName = "函数命名空间", required = true, immutable = true)
    private String namespace;

    @Base
    @Field.String
    @Field(displayName = "函数编码", required = true)
    private String fun;

    @Base
    @XStreamAsAttribute
    @Field.String
    @Field(displayName = "api名称", required = true)
    private String name;

    @Base
    @Field.Enum
    @Field(displayName = "函数语言", summary = "代码语言", defaultValue = "DSL", required = true)
    private FunctionLanguageEnum language;

    @Base
    @Field.Enum
    @Field(displayName = "函数类型", summary = "函数类型", defaultValue = "4", required = true)
    private List<FunctionTypeEnum> type;

    @Base
    @Field.Boolean
    @Field(displayName = "数据管理器函数", defaultValue = "false")
    private Boolean dataManager;

    @Base
    @Field.String
    @Field(displayName = "bean名称", invisible = true)
    private String beanName;

    @Base
    @Field.Text
    @Field(displayName = "上下文引用", summary = "上下文引用")
    private String imports;

    @Base
    @Field.Text
    @Field(displayName = "上下文变量", summary = "上下文，变量")
    private String context;

    @Base
    @Field.Text
    @Field(displayName = "函数内容", summary = "代码内容")
    private String codes;

    @Base
    @Field.Enum
    @Field(displayName = "来源", summary = "来源", defaultValue = "ACTION", required = true)
    private FunctionSourceEnum source;

    @Base
    @Field.Enum
    @Field(displayName = "开放级别", summary = "开放级别", defaultValue = "3", required = true)
    private List<FunctionOpenEnum> openLevel;

    @Base
    @Field.Html
    @Field(displayName = "描述")
    private String description;

    @Base
    @Field.Enum
    @Field(displayName = "分类", summary = "分类", defaultValue = "OTHER")
    private FunctionCategoryEnum category;

    @Base
    @Field.Enum
    @Field(displayName = "场景", summary = "可用场景", multi = true)
    private List<FunctionSceneEnum> scene;

    @Base
    @Field.Boolean
    @Field(displayName = "是否内置函数", defaultValue = "false")
    private Boolean isBuiltin;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"namespace", "name"})
    @Field(displayName = "扩展点", invisible = true)
    private List<ExtPoint> extPointList;

    @Base
    @Field.many2one
    @Field.Relation(store = false)
    @Field.Advanced(columnDefinition = "varchar(128)")
    @Field(displayName = "事务配置", store = NullableBoolEnum.TRUE, invisible = true, serialize = Field.serialize.JSON, summary = "事务配置")
    private TransactionConfig transactionConfig;

    @Base
    @Field.String
    @Field(displayName = "系统分组", defaultValue = "pamirs")
    private String group;

    @Base
    @Field.String
    @Field(displayName = "系统版本", defaultValue = "1.0.0")
    private String version;

    @Base
    @Field.Integer
    @Field(displayName = "超时时间", defaultValue = "5000")
    private Integer timeout;

    @Base
    @Field.Integer
    @Field(displayName = "重试次数", defaultValue = "0")
    private Integer retries;

    @Base
    @Field(displayName = "是否支持long polling", summary = "是否支持long polling", defaultValue = "false", invisible = true)
    private Boolean isLongPolling;

    @Base
    @Field(displayName = "long polling上下文key", summary = "支持从上下文中获取字段作为key", defaultValue = "userId", invisible = true)
    private String longPollingKey;

    @Base
    @Field(displayName = "long polling超时时间", defaultValue = "1", invisible = true)
    private Integer longPollingTimeout;

    @Function.Advanced(displayName = "初始化数据", type = FunctionTypeEnum.QUERY)
    @Function
    public FunctionDefinition construct(FunctionDefinition data) {
        String name = Function.class.getSimpleName() + CharacterConstants.SEPARATOR_UNDERLINE + UUIDUtil.getUUIDNumberString();
        data.setNamespace(Optional.of(data).map(FunctionDefinition::getNamespace).filter(StringUtils::isNotBlank).orElse(PackageConstants.PAMIRS))
                .setFun(Optional.of(data).map(FunctionDefinition::getFun).filter(StringUtils::isNotBlank).orElse(name))
                .setName(Optional.of(data).map(FunctionDefinition::getName).filter(StringUtils::isNotBlank).orElse(data.getFun()))
                .setDisplayName(Optional.of(data).map(FunctionDefinition::getDisplayName).filter(StringUtils::isNotBlank).orElse(data.getName()))
                .setType(Optional.of(data).map(FunctionDefinition::getType).orElse(Lists.newArrayList(FunctionTypeEnum.UPDATE)))
        ;
        return data;
    }

    public static FunctionDefinition simplify(FunctionDefinition functionDefinition) {
        if (null == functionDefinition) {
            return null;
        }
        return (FunctionDefinition) new FunctionDefinition()
                .setName(functionDefinition.getName())
                .setType(functionDefinition.getType())
                .setArgumentList(simplify(functionDefinition.getArgumentList()))
                .setReturnType(simplify(functionDefinition.getReturnType()))
                .setSign(null);
    }

    public static List<Argument> simplify(List<Argument> argumentList) {
        if (CollectionUtils.isEmpty(argumentList)) {
            return null;
        }
        List<Argument> resultList = new ArrayList<>(argumentList.size());
        Argument result;
        for (Argument argument : argumentList) {
            result = new Argument();
            result.setName(argument.getName());
            result.setTtype(argument.getTtype());
            resultList.add(result);
        }
        return resultList;
    }

    public static Type simplify(Type type) {
        if (null == type) {
            return null;
        }
        Type result = new Argument();
        result.setTtype(type.getTtype());
        return result;
    }

    @JSONField(serialize = false)
    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this, "extPointList");
    }

    public static String sign(String namespace, String fun) {
        return namespace + CharacterConstants.SEPARATOR_DOT + fun;
    }

}
