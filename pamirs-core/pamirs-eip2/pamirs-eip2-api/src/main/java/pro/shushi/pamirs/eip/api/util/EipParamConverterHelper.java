package pro.shushi.pamirs.eip.api.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.camel.util.StringHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.client.RestTemplate;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConvertParam;
import pro.shushi.pamirs.eip.api.IEipParamConverterCallback;
import pro.shushi.pamirs.eip.api.enmu.ContextTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.framework.gateways.util.BooleanHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.util.DateUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * @author Adamancy Zhang on 2021-02-24 20:51
 */
@Slf4j
public class EipParamConverterHelper {

    public static final Pattern LIST_FLAG_PATTERN = Pattern.compile("\\[\\*]");

    private EipParamConverterHelper() {
        //reject create object
    }

    public static <T> Object getContextValue(ContextTypeEnum contextType, IEipContext<T> context, String key) {
        switch (contextType) {
            case EXECUTOR:
                return context.getExecutorContextValue(key);
            case INTERFACE:
                return context.getInterfaceContextValue(key);
            default:
                return null;
        }
    }

    public static <T> void putContextValue(ContextTypeEnum contextType, IEipContext<T> context, String key, Object value) {
        switch (contextType) {
            case EXECUTOR:
                context.putExecutorContextValue(key, value);
                break;
            case INTERFACE:
                context.putInterfaceContextValue(key, value);
                break;
        }
    }

    public static <T> Object convertValue(IEipConvertParam<T> convertParam, Object value) {
        if (value == null) {
            value = convertParam.getDefaultValue();
        }
        if (value == null) {
            return null;
        }
        ParamTypeEnum paramType = convertParam.getOutParamType();
        if (paramType == null) {
            return value;
        }
        switch (paramType) {
            case Boolean:
                value = BooleanHelper.isTrue(value);
                break;
            case Date:
                if (value instanceof String) {
                    try {
                        String dateString = (String) value;
                        if (dateString.length() == DateFormatEnum.DATETIME.value().length()) {
                            value = DateUtils.formatDate(dateString, DateFormatEnum.DATETIME.value());
                        } else if (dateString.length() == DateFormatEnum.DATE.value().length()) {
                            value = DateUtils.formatDate(dateString, DateFormatEnum.DATE.value());
                        } else if (dateString.length() == DateFormatEnum.TIME.value().length()) {
                            value = DateUtils.formatDate(dateString, DateFormatEnum.TIME.value());
                        } else if (dateString.length() == DateFormatEnum.YEAR.value().length()) {
                            value = DateUtils.formatDate(dateString, DateFormatEnum.YEAR.value());
                        }
                    } catch (Throwable e) {
                        log.error("date format error. value: {}", value);
                    }
                }
                break;
        }
        if (ParamTypeEnum.ENUMERATION.equals(convertParam.getInParamType()) && ParamTypeEnum.ENUMERATION.equals(paramType)) {
            String enumName = value.toString();
            String tempObject = convertParam.getConvertMapValue(enumName);
            if (tempObject != null) {
                value = tempObject;
            }
        } else if (ParamTypeEnum.File.equals(convertParam.getInParamType())) { // FIXME: zbh 20250717 in param type ?
            if (value instanceof String) {
                value = convertFileTypeValueByUrl(value);
            }
        }
        return value;
    }

    public static <T> Object callback(IEipParamConverterCallback<T> callback, IEipContext<T> context, IEipConvertParam<T> convertParam, List<AtomicInteger> inParamCounterList, Object object) {
        IEipParamConverterCallback<T> singleParamCallback = convertParam.getParamConverterCallback();
        if (singleParamCallback != null) {
            object = singleParamCallback.callback(context, convertParam, inParamCounterList, object);
        }
        if (callback != null) {
            object = callback.callback(context, convertParam, inParamCounterList, object);
        }
        return object;
    }

    /**
     * File类型参数为文件地址时，转换成文件流
     */
    private static Object convertFileTypeValueByUrl(Object value) {
        // 去除单双引号
        String fileUrl = StringHelper.removeQuotes((String) value);
        if (StringUtils.isNotBlank(fileUrl)) {
            RestTemplate restTemplate = new RestTemplate();
            byte[] imageBytes = restTemplate.getForObject(fileUrl, byte[].class);
            if (imageBytes == null) {
                throw PamirsException.construct(EipExpEnumerate.PARAM_FILE_TYPE_DATA_NULL_ERROR).appendMsg("url=" + fileUrl).errThrow();
            }
            value = new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    // filename必须设置，否则无法发送数据
                    return extractFileNameByUrl(fileUrl, "default");
                }
            };
        }
        return value;
    }

    public static String getFinalParameter(String parameter, List<AtomicInteger> inParamCounterList) {
        if (inParamCounterList == null) {
            return parameter;
        }
        return getFinalParameter(parameter, inParamCounterList, 0);
    }

    public static String getFinalParameter(String parameter, List<AtomicInteger> inParamCounterList, int beginIndex) {
        int i = beginIndex;
        while (parameter.contains(IEipContext.DEFAULT_LIST_FLAG_KEY)) {
            parameter = LIST_FLAG_PATTERN.matcher(parameter).replaceFirst("[" + inParamCounterList.get(i).get() + "]");
            i++;
        }
        return parameter;
    }

    /**
     * 从url提取文件名称
     *
     * @param defaultFilename url提取名称失败时的默认名称
     */
    private static String extractFileNameByUrl(String url, String defaultFilename) {
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex == -1) return defaultFilename;

        String fileNameWithRandomOrExtension = url.substring(lastSlashIndex + 1);
        int underscoreIndex = fileNameWithRandomOrExtension.lastIndexOf('_');
        int lastDotIndex = fileNameWithRandomOrExtension.lastIndexOf('.');

        String fileName;
        if (underscoreIndex != -1) {
            fileName = fileNameWithRandomOrExtension.substring(0, underscoreIndex);
        } else if (lastDotIndex != -1) {
            fileName = fileNameWithRandomOrExtension.substring(0, lastDotIndex);
        } else {
            fileName = fileNameWithRandomOrExtension;
        }
        fileName = StringUtils.isBlank(fileName) ? defaultFilename : fileName;

        String extension = lastDotIndex > -1 ? fileNameWithRandomOrExtension.substring(lastDotIndex) : "";
        return fileName + extension;
    }

    /**
     * 解析接口返回的数据
     *
     * @param jsonData 接口返回的数据
     * @param valueExp 表达式，支持解析数组
     */
    public static Object extractData(JSON jsonData, String valueExp) {
        if (jsonData instanceof JSONObject) {
            return extractObject((JSONObject) jsonData, valueExp);
        }
        return extractArray((JSONArray) jsonData, valueExp);
    }

    public static Object extractObject(JSONObject jsonObject, String valueExp) {
        if (!valueExp.contains(IEipContext.DEFAULT_LIST_FLAG_KEY)) {
            return MapHelper.getIteration(jsonObject, valueExp);
        }
        String[] parts = valueExp.split("\\.");
        return extract(jsonObject, parts, 0);
    }

    public static Object extractArray(JSONArray jsonArray, String valueExp) {
        if (IEipContext.DEFAULT_LIST_FLAG_KEY.equals(valueExp)) {
            return jsonArray;
        }
        String[] parts = valueExp.split("\\.");
        if (!IEipContext.DEFAULT_LIST_FLAG_KEY.equals(parts[0])) {
            return null;
        }
        List<Object> resultList = new ArrayList<>();
        for (Object item : jsonArray) {
            if (item instanceof JSONObject) {
                Object result = extract((JSONObject) item, parts, 1);
                if (result instanceof List) {
                    resultList.addAll((Collection<?>) result);
                } else {
                    resultList.add(result);
                }
            } else {
                log.error("Parameter conversion failed, unknown type: {}", item != null ? item.getClass().getName() : "null");
            }
        }
        return resultList;
    }

    private static Object extract(JSONObject jsonObject, String[] parts, int index) {
        if (index >= parts.length) {
            return null;
        }

        String part = parts[index];
        if (part.endsWith(IEipContext.DEFAULT_LIST_FLAG_KEY)) {
            String valueExp = part.substring(0, part.length() - IEipContext.DEFAULT_LIST_FLAG_KEY.length());
            JSONArray jsonArray = jsonObject.getJSONArray(valueExp);
            if (jsonArray == null) {
                log.warn("Data parsing value is null");
                return null;
            }
            if (index == parts.length - 1) {
                return jsonArray;
            }
            List<Object> resultList = new ArrayList<>(jsonArray.size());
            for (Object item : jsonArray) {
                if (item instanceof JSONObject) {
                    Object data = extract((JSONObject) item, parts, index + 1);
                    if (data instanceof List) {
                        resultList.addAll((Collection<?>) data);
                    } else {
                        resultList.add(data);
                    }
                } else {
                    resultList.add(item);
                }
            }
            return resultList;
        } else {
            Object value = jsonObject.get(part);
            if (value instanceof JSONObject) {
                return extract((JSONObject) value, parts, index + 1);
            }
            return value;
        }
    }
}

