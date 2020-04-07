package pro.shushi.pamirs.meta.common.util;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 4:23 下午
 */
public class PStringUtils extends StringUtils {

    public static String column2FieldName(String column){
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (column == null || column.isEmpty()) {
            // 没必要转换
            return "";
        } else if (!column.contains("_")) {
            // 不含下划线，仅将首字母小写
            return column.substring(0, 1).toLowerCase() + column.substring(1);
        }
        // 用下划线将原始字符串分割
        String camels[] = column.split("_");
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 处理真正的驼峰片段
            if (result.length() == 0) {
                // 第一个驼峰片段，全部字母都小写
                result.append(camel.toLowerCase());
            } else {
                // 其他的驼峰片段，首字母大写
                result.append(camel.substring(0, 1).toUpperCase());
                result.append(camel.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    public static String fieldName2Column(String field){
        if(null == field){
            return null;
        }

        String fieldName = column2FieldName(field);

        String[] ss = org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase(fieldName);
        List<String> fieldNameL = new ArrayList<>();
        for(String s:ss){
            fieldNameL.add(org.apache.commons.lang3.StringUtils.uncapitalize(s));
        }
        return org.apache.commons.lang3.StringUtils.join(fieldNameL,"_");
    }

    public static String camelCaseFromModel(String model){
        if(null == model){
            return null;
        }
        String name = model;
        if(model.contains(CharacterConstants.SEPARATOR_DOT)){
            name = org.apache.commons.lang3.StringUtils.substringAfterLast(model, CharacterConstants.SEPARATOR_DOT);
        }

        return org.apache.commons.lang3.StringUtils.uncapitalize(name);
    }

    public static List<String> trim(String[] array){
        if(null == array){
            return null;
        }
        List<String> list = new ArrayList<>();
        for(String item : array){
            list.add(item.trim());
        }
        return list;
    }

}
