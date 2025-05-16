package pro.shushi.pamirs.boot.base.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.Collections;
import java.util.Map;

/**
 * 分享页面
 *
 * @author Adamancy Zhang at 18:59 on 2024-04-11
 */
@Base
@Model.model(SharedPage.MODEL_MODEL)
@Model(displayName = "分享页面")
public class SharedPage extends TransientModel {

    private static final long serialVersionUID = 971864988792284272L;

    public static final String MODEL_MODEL = "base.SharedPage";

    public static final String SHARED_LOAD_FUN = "load";

    public static final String SHARED_VIEW_NAME = "shared";

    @Field(displayName = "Http Origin")
    private String origin;

    @Field(displayName = "分享动作模型")
    private String shareActionModel;

    @Field(displayName = "分享动作名称")
    private String shareActionName;

    @Field(displayName = "页面参数")
    private String parameters;

    @Field(displayName = "浏览器标题")
    private String browserTitle;

    @Field(displayName = "当前语言")
    private String language;

    @Field(displayName = "当前语言ISO编码")
    private String languageIsoCode;

    @Field(displayName = "分享码")
    private String sharedCode;

    @Field(displayName = "分享链接")
    private String url;

    @Field(displayName = "链接文本")
    private String linkText;

    private transient Map<String, Object> parameterObject;

    public Map<String, Object> parseParameters() {
        Map<String, Object> parameterObject = getParameterObject();
        if (parameterObject != null) {
            return parameterObject;
        }
        String parametersJSON = getParameters();
        if (StringUtils.isBlank(parametersJSON)) {
            parameterObject = Collections.emptyMap();
        } else {
            parameterObject = JSON.parseObject(parametersJSON, Feature.OrderedField);
        }
        setParameterObject(parameterObject);
        return parameterObject;
    }

    public Map<String, Object> getParameterObject() {
        return parameterObject;
    }

    public void setParameterObject(Map<String, Object> parameterObject) {
        this.parameterObject = parameterObject;
    }
}
