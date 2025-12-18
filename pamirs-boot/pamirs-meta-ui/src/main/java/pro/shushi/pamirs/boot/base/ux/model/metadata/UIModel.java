package pro.shushi.pamirs.boot.base.ux.model.metadata;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.boot.base.ux.model.view.UIField;
import pro.shushi.pamirs.boot.base.ux.model.view.UIVirtualAction;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * 模型元数据
 *
 * @author Adamancy Zhang at 11:34 on 2025-04-14
 */
@Base
@XStreamAlias(TemplateNodeConstants.NODE_MODEL)
@Model(displayName = "模型元数据")
public class UIModel extends TransientModel {

    private static final long serialVersionUID = -3276836096734406315L;

    @XStreamAsAttribute
    @Field(displayName = "模型编码")
    private String model;

    @XStreamImplicit
    @Field(displayName = "模型字段")
    private List<UIField> field;

    @XStreamImplicit
    @Field(displayName = "模型动作")
    private List<UIVirtualAction> action;
}
