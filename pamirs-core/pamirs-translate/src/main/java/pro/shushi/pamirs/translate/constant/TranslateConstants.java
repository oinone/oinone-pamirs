package pro.shushi.pamirs.translate.constant;

import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;

import java.util.Arrays;
import java.util.List;

/**
 * TranslateConstants
 *
 * @author yakir on 2020/05/11 12:02.
 */
public class TranslateConstants {

    public static final String TRANSLATE_STATE = "pamirs.translate.state";
    public static final String TRANSLATE_OPEN = "open";

    public static final String DEFAULT_TRANSLATION_JSONP_FUNCTION_NAME = "i18nResolve";

    public static final String DEFAULT_TRANSLATION_RESOURCE_FILE_NAME_PREFIX = "i18n_";

    public static final String DEFAULT_TRANSLATION_RESOURCE_FILE_NAME_SUFFIX = ".js";

    public static final String TRANSLATE_PREFIX = "$t(";
    public static final String TRANSLATE_SUFFIX = ")";

    public static final String RES_LANG_CODE = DefaultResourceConstants.CHINESE_LANGUAGE_CODE;
    public static final String LANG_CODE = DefaultResourceConstants.ENGLISH_LANGUAGE.getCode();
    public static final String TRANSLATE_PATH = "translate";
    public static final String TRANSLATE_GLOBAL_PATH = "global";
    public static final String PUBLIC_RESOURCE = "common";
    public static final String PUBLIC_RESOURCE_NAME = "公共资源";

    //-------需要特殊处理的应用
    public static final String MODEL_DESIGNER = "model_designer";
    public static final String COMMON = "common";
    public static final String WORKFLOW_DESIGNER = "workflow_designer";
    public static final String MICROFLOW_DESIGNER = "microflow_designer";
    public static final String UI_DESIGNER = "ui_designer";
    public static final String UI_DESIGNER_DATA_WIDGET = "ui_designer_data_widget";
    public static final String UI_DESIGNER_BIZ_WIDGET = "ui_designer_biz_widget";
    public static final String DESIGNER_COMMON = "designer_common";
    public static final String EIP_DESIGNER = "eip_designer";
    public static final String WORKFLOW_DESIGNER_BASE = "workflow_designer_base";
    public static final String APPS = "apps";
    public static final String ERRORDEFINITION_MODEL = "base.ErrorDefinition";


    public static final List<String> MODULES_TO_EXCLUDE = Arrays.asList(TranslateConstants.MODEL_DESIGNER,
            TranslateConstants.COMMON,
            TranslateConstants.WORKFLOW_DESIGNER,
            TranslateConstants.MICROFLOW_DESIGNER,
            TranslateConstants.UI_DESIGNER,
            TranslateConstants.UI_DESIGNER_DATA_WIDGET,
            TranslateConstants.UI_DESIGNER_BIZ_WIDGET,
            TranslateConstants.DESIGNER_COMMON,
            TranslateConstants.EIP_DESIGNER,
            TranslateConstants.WORKFLOW_DESIGNER_BASE);

    public static final List<String> FIELD_TO_EXCLUDE = Arrays.asList(Boolean.FALSE.toString(), Boolean.TRUE.toString());


    // 登录页查询语言 js
    public static final String TRANSLATION_ONLY_GLOBAL = "translationOnlyGlobal";

}
