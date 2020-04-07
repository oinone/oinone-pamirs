package pro.shushi.pamirs.meta.domain.fun;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.PackageConstants;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.meta.enmu.FunctionSceneEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionUsageEnum;
import pro.shushi.pamirs.meta.enumclass.FunctionCategoryEnumCls;
import pro.shushi.pamirs.meta.enumclass.FunctionSourceEnumCls;

import java.util.List;
import java.util.Optional;

/**
 * 函数定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaModel(priority = 10, core = true)
@Base
@Model.Advanced(unique = "namespace, name")
@Model.model("base.Function")
@Model(displayName = "函数", summary = "函数")
public class FunctionDefinition extends AbstractFunction {

    @Base
    @Field.String
    @Field(displayName = "显示名称", required = true)
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "函数命名空间", required = true, immutable = true)
    private String namespace;

    @Base
    @Field.String
    @Field(displayName = "函数编码", required = true, immutable = true)
    private String fun;

    @Base
    @Field.String
    @Field(displayName = "技术名称", required = true)
    private String name;

    @Base
    @Field.Enum
    @Field(displayName = "函数类型", summary = "代码类型", defaultValue = "DSL", required = true)
    private FunctionTypeEnum type;

    @Base
    @Field.Enum
    @Field(displayName = "函数用途", summary = "函数用途", defaultValue = "WRITE", required = true)
    private FunctionUsageEnum usage;

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
    private FunctionSourceEnumCls source;

    @Base
    @Field.Html
    @Field(displayName = "描述")
    private String description;

    @Base
    @Field.Enum
    @Field(displayName = "分类", summary = "分类", defaultValue = "OTHER")
    private FunctionCategoryEnumCls category;

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
    @Field(displayName = "是否支持long polling", summary = "是否支持long polling", defaultValue = "false", invisible = true)
    private Boolean isLongPolling;

    @Base
    @Field(displayName = "long polling上下文key", summary = "支持从上下文中获取字段作为key", defaultValue = "userId", invisible = true)
    private String longPollingKey;

    @Base
    @Field(displayName = "long polling超时时间", defaultValue = "1", invisible = true)
    private Integer longPollingTimeout;

    @Function
    public FunctionDefinition construct(FunctionDefinition function){
        String name = Function.class.getSimpleName() + CharacterConstants.SEPARATOR_UNDERLINE + UUIDUtil.getUUIDNumberString();
        function.setNamespace(Optional.of(function).map(v->v.getNamespace()).filter(s->StringUtils.isNotBlank(s)).orElse(PackageConstants.PAMIRS))
                .setName(Optional.of(function).map(v->v.getName()).filter(s->StringUtils.isNotBlank(s)).orElse(name))
                .setDisplayName(Optional.of(function).map(v->v.getDisplayName()).filter(s->StringUtils.isNotBlank(s)).orElse(name))
                .setFun(Optional.of(function).map(v->v.getFun()).filter(s->StringUtils.isNotBlank(s)).orElse(function.getName()))
        ;
        return function;
    }

    public String getSign(){
        return getNamespace() + CharacterConstants.SEPARATOR_DOT + getFun();
    }

}
