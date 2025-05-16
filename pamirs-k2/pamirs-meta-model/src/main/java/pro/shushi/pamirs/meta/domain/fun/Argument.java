package pro.shushi.pamirs.meta.domain.fun;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 函数参数
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@XStreamAlias("argument")
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
@Model.model("base.Argument")
@Model(displayName = "函数参数", summary = "函数参数")
public class Argument extends Type {

    private static final long serialVersionUID = 5391563980436257955L;

    @Base
    @XStreamAsAttribute
    @Field.String(size = 64)
    @Field(displayName = "参数名称", required = true)
    private String name;

}
