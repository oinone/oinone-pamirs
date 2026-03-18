package pro.shushi.pamirs.translate.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.ux.constants.DslNodeConstants;
import pro.shushi.pamirs.framework.orm.json.PamirsJsonUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author xzf 2022/12/16 21:49
 **/
@Component
@Slf4j
public class ViewTranslateServiceImpl implements TranslateMetaBaseService<View> {

    List<Getter<View, String>> getters = Lists.newArrayList(View::getTitle);

    public static final String labelP = "(?<=label=\").*?(?=\")";
    public static final String titleP = "(?<=title=\").*?(?=\")";
    public static final String displayNameP = "(?<=displayName=\").*?(?=\")";
    public static final String placeholderP = "(?<=placeholder=\").*?(?=\")";
    public static final String cancelTextP = "(?<=cancelText=\").*?(?=\")";
    public static final String confirmP = "(?<=confirm=\").*?(?=\")";

    private static final List<String> patterns = Lists.newArrayList(labelP, titleP, displayNameP, placeholderP, cancelTextP, confirmP);
    private static final Pattern pattern = Pattern.compile("(" + String.join("|", patterns) + ")");

    public static String FIELD_DSL_NODE_TYPE = "dslNodeType";
    public static String FIELD_MODEL = "model";
    public static String FIELD_DATA = "data";
    public static String FIELD_WIDGETS = "widgets";
    public static String FIELD_OPTIONS = "options";
    public static String FIELD_DISPLAY_NAME = "displayName";
    public static String FIELD_LABEL = "label";

    Function<View, Set<String>> itemsFunction = (data) -> {
        Set<String> result = new HashSet<>();
        String model = data.getModel();
        String module = getModule(model);

        Set<String> originSet = getters.stream().map(getter -> getter.apply(data)).collect(Collectors.toSet()); //去重

        Set<String> uniqueSet = originSet.stream().map((origin) -> TranslateMetaBaseService.buildDefaultItemUnique(module, model, RES_LANG_CODE, LANG_CODE, origin)).collect(Collectors.toSet());

        Set<String> xmlOriginSet = xmlMatcher(pattern, data.getTemplate());
        Set<String> xmlUniqueSet = xmlOriginSet.stream().map((origin) -> TranslateMetaBaseService.buildDefaultItemUnique(module, model, RES_LANG_CODE, LANG_CODE, origin)).collect(Collectors.toSet());

        result.addAll(uniqueSet);
        result.addAll(xmlUniqueSet);

        return result;
    };

    public static Set<String> xmlMatcher(Pattern pattern, String xml) {
        Set<String> result = new HashSet<>();

        if (null == pattern || null == xml) {
            return result;
        }

        Matcher matcher = pattern.matcher(xml);
        while (matcher.find()) {
            String value = matcher.group();
            result.add(value);
        }
        return result;
    }

    @Override
    public Set<String> modelType() {
        return Sets.newHashSet(

        );
    }

    @Override
    public String initModelType() {
        return View.MODEL_MODEL;
    }

    private void templateConvert(String langCode, View view) {
        String template = view.getTemplate();
        try {
            JSONObject templateObj = JSONObject.parseObject(template);
            JSONArray widgetArray = templateObj.getJSONArray(FIELD_WIDGETS);
            List<JSONObject> jsonObjectList = widgetArray.toJavaList(JSONObject.class);
            compileWidget(langCode, jsonObjectList);

            templateObj.put(FIELD_WIDGETS, jsonObjectList);

            view.setTemplate(PamirsJsonUtils.toJSONString(templateObj));
        } catch (Exception e) {
//            String subTemplate = "";
//            if (!StringUtils.isBlank(template)) {
//                if (template.length() > 20) {
//                    subTemplate.substring(0, 20);
//                } else {
//                    subTemplate.substring(0, template.length());
//                }
//            }
//            log.warn("templateConvert exception, template parsing failed, view.id:{}, truncated data view.template:{}", view.getId(), subTemplate);
            log.warn("templateConvert exception, template parsing failed, view.id:{}", view.getId());
        }
    }


    private void compileWidget(String langCode, List<JSONObject> jsonObjectList) {
        if (CollectionUtils.isEmpty(jsonObjectList)) {
            return;
        }

        for (JSONObject jsonObject : jsonObjectList) {

            Object dslNodeTypeObj = jsonObject.get(FIELD_DSL_NODE_TYPE);

            if (Objects.isNull(dslNodeTypeObj)) {
                continue;
            }
            String dslNodeType = String.valueOf(dslNodeTypeObj);

            if (DslNodeConstants.NODE_ACTION.equals(dslNodeType)) {
                compile(langCode, jsonObject);
            } else if (DslNodeConstants.NODE_FIELD.equals(dslNodeType)) {
                Object modelObj = jsonObject.get(FIELD_MODEL);
                Object dataObj = jsonObject.get(FIELD_DATA);
                String model = String.valueOf(modelObj);
                String data = String.valueOf(dataObj);
                if (Objects.isNull(model) || Objects.isNull(data)) {
                    continue;
                }

                ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model, data);
                if (null != modelFieldConfig) {
                    ModelField modelField = modelFieldConfig.getModelField();
                    compile(langCode, jsonObject);
                    fillOptions(langCode, modelField, jsonObject);
                }
            } else if (DslNodeConstants.NODE_TEMPLATE.equals(dslNodeType)) {
            } else if (DslNodeConstants.NODE_PACK.equals(dslNodeType)) {
            } else if (DslNodeConstants.NODE_SLOT.equals(dslNodeType)) {
            } else if (DslNodeConstants.NODE_XSLOT.equals(dslNodeType)) {
            }

            JSONArray subWidgets = jsonObject.getJSONArray(FIELD_WIDGETS);
            if (Objects.isNull(subWidgets)) {
                continue;
            }
            List<JSONObject> subWidgetsList = subWidgets.toJavaList(JSONObject.class);

            compileWidget(langCode, subWidgetsList);
        }

    }

    public void fillOptions(String langCode, ModelField modelField, JSONObject jsonObject) {
        TtypeEnum ttype = modelField.getExactTtype();
        if (TtypeEnum.ENUM.equals(ttype)) {
            JSONArray optionsArray = jsonObject.getJSONArray(FIELD_OPTIONS);
            if (CollectionUtils.isEmpty(optionsArray)) {
                return;
            }
            String dictionary = modelField.getDictionary();
            DataDictionary dataDictionary = PamirsSession.getContext().getDictionary(dictionary);
            String model = dictionary;
            String module = dataDictionary.getModule();
            List<JSONObject> jsonOptionList = optionsArray.toJavaList(JSONObject.class);
            for (JSONObject jsonOption : jsonOptionList) {
                Object displayNameObj = jsonOption.get(FIELD_DISPLAY_NAME);
                if (Objects.isNull(displayNameObj)) {
                    continue;
                }
                String target = TranslateMetaBaseService.calcTarget(module, model, RES_LANG_CODE, langCode, String.valueOf(displayNameObj));
                log.debug("Enum/Data dictionary option attribute value translation: module:{},model:{},res_lang_code:{},lang_code:{},origin:{},target:{}", module, model, RES_LANG_CODE, langCode, displayNameObj, target);
                if (StringUtils.isNotBlank(target)) {
                    jsonOption.put(FIELD_DISPLAY_NAME, target);
                    jsonOption.put(FIELD_LABEL, target);
                }
            }
            jsonObject.put(FIELD_OPTIONS, jsonOptionList);
        }
    }


    public void compile(String langCode, JSONObject jsonObject) {
        Object labelObj = jsonObject.get(FIELD_LABEL);
        Object modelObj = jsonObject.get(FIELD_MODEL);

        if (Objects.isNull(labelObj) || Objects.isNull(modelObj)) {
            return;
        }
        String label = String.valueOf(labelObj);
        String model = String.valueOf(modelObj);
        String module = getModule(model);
        String target = TranslateMetaBaseService.calcTarget(module, model, RES_LANG_CODE, langCode, label);
        if (StringUtils.isNotBlank(target)) {
            jsonObject.put(FIELD_LABEL, target);
        }
    }

}
