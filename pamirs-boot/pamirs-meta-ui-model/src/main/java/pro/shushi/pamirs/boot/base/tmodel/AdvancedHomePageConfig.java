package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * @author: Wuxin
 * @createTime: 2024/05/21 18:16
 */
@Model(displayName = "高级首页配置")
@Model.model(AdvancedHomePageConfig.MODEL_MODEL)
public class AdvancedHomePageConfig extends TransientModel {

    public static final String MODEL_MODEL = "base.AdvancedHomePageConfig";

    @Field(displayName = "支持高级首页配置")
    private Boolean state;

    @Field(displayName = "首页配置规则")
    private List<HomePageConfigRules> rules;
}
