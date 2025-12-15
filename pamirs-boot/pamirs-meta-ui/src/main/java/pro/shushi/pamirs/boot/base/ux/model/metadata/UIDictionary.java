package pro.shushi.pamirs.boot.base.ux.model.metadata;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.boot.base.ux.model.part.UIOption;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * 数据字典元数据
 *
 * @author Adamancy Zhang at 10:49 on 2025-12-09
 */
@Base
@XStreamAlias(TemplateNodeConstants.NODE_DICTIONARY)
@Model(displayName = "数据字典元数据")
public class UIDictionary extends TransientModel {

    private static final long serialVersionUID = 7028479506498572179L;

    @XStreamAsAttribute
    @Field(displayName = "数据字典编码")
    private String dictionary;

    @XStreamImplicit
    @Field(displayName = "可选项")
    private List<UIOption> options;
}
