package pro.shushi.pamirs.boot.base.ux.model.metadata;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * 元数据集
 *
 * @author Adamancy Zhang at 11:32 on 2025-04-14
 */
@Base
@XStreamAlias(TemplateNodeConstants.NODE_METADATA)
@Model(displayName = "元数据集")
public class UIMetadata extends TransientModel {

    private static final long serialVersionUID = -962829213343684878L;

    @XStreamImplicit
    @Field(displayName = "模型")
    private List<UIModel> model;
}
