package pro.shushi.pamirs.file.api.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import pro.shushi.pamirs.core.common.constant.CommonConstants;
import pro.shushi.pamirs.file.api.config.FileConstant;
import pro.shushi.pamirs.framework.connectors.cdn.client.FileClient;
import pro.shushi.pamirs.framework.connectors.cdn.client.LocalFileClient;
import pro.shushi.pamirs.framework.connectors.cdn.constant.FileConstants;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@Slf4j
public class FileUtil {

    private final static List<String> PICTURE_EXT_NAMES = Arrays.asList("GIF", "JPG", "JPEG", "BMP", "DIP", "JFIF"
            , "PNG", "TIF", "TIFF", "ICO");


    public static BufferedInputStream getRemoteBufferedInputStream(String fileUrl) throws IOException {
        if (fileUrl.startsWith(FileConstants.LOCAL_PREFIX)) {
            FileClient client = FileClientFactory.getClient();
            if (client instanceof LocalFileClient) {
                return new BufferedInputStream(client.getDownloadStream(fileUrl));
            }
        }
        URL url;
        if (fileUrl.startsWith(CommonConstants.CLASSPATH_PROTOCOL)) {
            url = ResourceUtils.getURL(fileUrl);
        } else {
            url = new URL(fileUrl);
        }
        URLConnection connection = url.openConnection();
        if (connection instanceof HttpURLConnection) {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setInstanceFollowRedirects(false);
            int statusCode = httpConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_MOVED_PERM || statusCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                // 解决请求被重定向到新地址的情况，如:nginx配置了http自动重定向到https
                String redirectUrl = httpConnection.getHeaderField("Location");
                url = new URL(redirectUrl);
            }
            return new BufferedInputStream(url.openStream());
        }
        return new BufferedInputStream(connection.getInputStream());
    }

    public static Boolean isPicture(String fileKey) {
        //获取文件扩展名
        String extName = StringUtils.upperCase(fileKey.substring(fileKey.lastIndexOf(".") + 1));
        return PICTURE_EXT_NAMES.contains(extName);
    }

    public static Boolean isEnhanceModel(ModelConfig model) {
        if (null == model) {
            return Boolean.FALSE;
        }

        return model.getSuperModels().contains("EnhanceModel");
    }

    public static ModelFieldConfig convert2Field(String fieldStr, ModelConfig model) {
        if (fieldStr.contains(CharacterConstants.SEPARATOR_SLASH)) {
            String fStr = fieldStr.substring(0, fieldStr.indexOf(CharacterConstants.SEPARATOR_SLASH));
            String lStr = fieldStr.substring(fieldStr.indexOf(CharacterConstants.SEPARATOR_SLASH) + 1);
            ModelFieldConfig fField = getModelField(fStr, model.getModelFieldConfigList());
            if (fField != null) {
                return convert2Field(lStr, PamirsSession.getContext().getModelConfig(fField.getReferences()));
            }
        } else {
            List<ModelFieldConfig> modelFields = model.getModelFieldConfigList();
            return getModelField(fieldStr, modelFields);
        }
        return null;
    }

    public static List<ModelFieldConfig> convert2Field(String[] fieldArray, ModelConfig model) {
        List<ModelFieldConfig> modelFieldConfigList = new ArrayList<>();
        for (String field : fieldArray) {
            ModelFieldConfig modelFieldConfig = convert2Field(field, model);
            if (null != modelFieldConfig) {
                modelFieldConfigList.add(modelFieldConfig);
            }
        }
        return modelFieldConfigList;
    }

    private static ModelFieldConfig getModelField(String fieldName, List<ModelFieldConfig> modelFields) {
        for (ModelFieldConfig modelField : modelFields) {
            if (modelField.getName().equals(fieldName)) {
                return modelField;
            }
        }
        return null;
    }

    /**
     * 递归设置map
     *
     * @param map
     * @param fieldStr
     * @param fieldValue
     * @return
     */
    public static void convert2Map(Map<String, Object> map, String fieldStr, Object fieldValue) {
        if (fieldStr.contains(CharacterConstants.SEPARATOR_SLASH)) {
            String fStr = fieldStr.substring(0, fieldStr.indexOf(CharacterConstants.SEPARATOR_SLASH));
            String lStr = fieldStr.substring(fieldStr.indexOf(CharacterConstants.SEPARATOR_SLASH) + 1);
            Object object = map.get(fStr);
            if (ObjectUtils.isEmpty(object)) {
                Map<String, Object> dataMap = new HashMap<>();
                map.put(fStr, dataMap);
                convert2Map(dataMap, lStr, fieldValue);
            } else {
                convert2Map((Map<String, Object>) object, lStr, fieldValue);
            }
        } else {
            map.put(fieldStr, fieldValue);
        }
    }

    public static void getFieldValueBySplit(List<String> result, String fieldStr, Object data, String split) {
        if (fieldStr.contains(split)) {
            String fStr = fieldStr.substring(0, fieldStr.indexOf(split));
            String lStr = fieldStr.substring(fieldStr.indexOf(split) + 1);
            if (data instanceof ArrayList) {
                ((ArrayList) data).forEach(it -> {
                    getFieldValue(result, lStr, ((Map) it).get(fStr));
                });
            }
            if (data instanceof Map) {
                getFieldValue(result, lStr, ((Map) data).get(fStr));
            }
        } else {
            if (data instanceof Map) {
                if (((Map) data).containsKey(fieldStr) && null != ((Map) data).get(fieldStr)) {
                    result.add(String.valueOf(((Map) data).get(fieldStr)));
                }
            }
            if (data instanceof ArrayList) {
                ((ArrayList) data).forEach(it -> {
                    if (it instanceof Map && null != ((Map) it).get(fieldStr)) {
                        result.add(String.valueOf(((Map) it).get(fieldStr)));
                    }
                });
            }
        }
    }

    public static void getFieldValue(List<String> result, String fieldStr, Object data) {
        getFieldValueBySplit(result, fieldStr, data, FileConstant.POINT_CHARACTER);
    }


    /**
     * 随机生成字母(大小写) + 数字
     */
    public static String getRandomString(Integer length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int r = new Random().nextInt(3); // r = { 0 | 1 | 2 }
            if (r % 3 == 0) {
                builder.append((char) (48 + new Random().nextInt(10)));
            } else if (r % 3 == 1) {
                builder.append((char) (65 + new Random().nextInt(26)));
            } else {
                builder.append((char) (97 + new Random().nextInt(26)));
            }
        }
        return builder.toString();
    }


    /**
     * 获取数据字典
     */
    public static DataDictionary getDictByClassPath(String classPath) {
        try {
            Class<?> aClass = Class.forName(classPath);
            if (aClass.isEnum() || null != aClass.getAnnotation(Dict.class)) {
                Dict dict = aClass.getAnnotation(Dict.class);
                return PamirsSession.getContext().getDictionary(dict.dictionary());
            } else {
                log.error("【{}】不是一个有效的枚举类,或者没有实现【@Dict】注解", classPath);
                return null;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取枚举
     */
    public static Enum[] getEnumByClassPath(String classPath) {
        try {
            Class<?> aClass = Class.forName(classPath);
            if (aClass.isEnum()) {
                Method values = aClass.getMethod("values");
                return (Enum[]) values.invoke(aClass);
            } else {
                log.error("【{}】不是一个有效的枚举类,或者没有实现【@Dict】注解", classPath);
                return null;
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 枚举方法，将value -> displayName
     *
     * @param modelFieldConfig 枚举字段
     * @param value            字段数据 value中对应的是 enum中的key
     * @return displayName  对应key的displayName
     * 如果多枚举对应key1,key2  ->  displayName1,displayName2
     */
    public static String convertEnumValueToDisplayName(ModelFieldConfig modelFieldConfig, String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        if (!TtypeEnum.ENUM.value().equals(modelFieldConfig.getTtype())) {
            //该字段不是枚举类型
            return value;
        }
        String[] keys = value.split(CharacterConstants.SEPARATOR_COMMA);
        String result = "";
        DataDictionary dataDictionary = getDictByClassPath(modelFieldConfig.getLtype());
        if (null != dataDictionary) {
            //单选枚举，去第一个key就好了
            for (DataDictionaryItem dictionaryItem : dataDictionary.getOptions()) {
                if (dictionaryItem.getValue().equals(keys[0])) {
                    result = dictionaryItem.getDisplayName();
                    break;
                }
            }
        } else {
            //如果是多选枚举, List<Enum>不会走上面的逻辑
            dataDictionary = getDictByClassPath(modelFieldConfig.getLtypeT());
            if (null != dataDictionary) {
                for (String key : keys) { //多选枚举有多个key，要逐一替换
                    for (DataDictionaryItem dictionaryItem : dataDictionary.getOptions()) {
                        if (dictionaryItem.getValue().equals(key)) {
                            if (StringUtils.isBlank(result)) {
                                result = dictionaryItem.getDisplayName();
                            } else {
                                result = result + CharacterConstants.SEPARATOR_COMMA + dictionaryItem.getDisplayName();
                            }
                            break;
                        }
                    }
                }
            }
        }
        return StringUtils.isBlank(result) ? value : result;
    }

}
