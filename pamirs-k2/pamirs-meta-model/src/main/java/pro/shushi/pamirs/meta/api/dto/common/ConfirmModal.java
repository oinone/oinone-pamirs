package pro.shushi.pamirs.meta.api.dto.common;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.Map;

/**
 * @author Adamancy Zhang at 10:21 on 2025-08-12
 */
@Base
@Model.model(ConfirmModal.MODEL_MODEL)
@Model(displayName = "二次确认弹窗")
public class ConfirmModal extends TransientModel {

    private static final long serialVersionUID = -3057812488967642965L;

    public static final String MODEL_MODEL = "base.ConfirmModal";

    @Field(displayName = "标题")
    private String title;

    @Field(displayName = "内容")
    private String content;

    @Field(displayName = "上下文")
    private Map<String, Object> context;
}
