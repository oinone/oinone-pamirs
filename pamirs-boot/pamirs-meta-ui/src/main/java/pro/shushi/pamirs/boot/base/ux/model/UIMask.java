package pro.shushi.pamirs.boot.base.ux.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * 母版
 * <p>
 * 2021/7/15 12:38 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@XStreamAlias(TemplateNodeConstants.NODE_MASK)
@Model(displayName = "母版")
public class UIMask extends UILayout {

    private static final long serialVersionUID = 411684453718475579L;

    /**
     * 标题
     */
    @XStreamAsAttribute
    @Field(displayName = "标题")
    private String title;

}
