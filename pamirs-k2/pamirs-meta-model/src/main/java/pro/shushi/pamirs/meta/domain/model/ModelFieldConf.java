package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.BinaryTransferTypeEnum;
import pro.shushi.pamirs.meta.enmu.MimeTypeEnum;

/**
 * 字段配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.ModelFieldConfig")
@Model(displayName = "字段配置", summary = "字段配置")
public class ModelFieldConf extends TransientModel {

    private static final long serialVersionUID = -5459477023127248704L;

    @Base
    @Field(displayName = "组件")
    private String widget;

    @Base
    @Field(displayName = "传输类型")
    private BinaryTransferTypeEnum transferType;

    @Base
    @Field(displayName = "媒体类型")
    private MimeTypeEnum mimeType;

}
