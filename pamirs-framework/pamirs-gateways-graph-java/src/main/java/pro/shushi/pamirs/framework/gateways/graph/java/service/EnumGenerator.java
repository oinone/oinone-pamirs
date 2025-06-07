package pro.shushi.pamirs.framework.gateways.graph.java.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.gateways.graph.java.build.GraphQLVerifyContext;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.GraphQLSdlConstants;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;

/**
 * 枚举构造器
 * <p>
 * 2020/10/20 10:44 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class EnumGenerator implements GraphQLSdlConstants {

    /**
     * 生成枚举GQL类型定义
     */
    public static void generate(GraphQLVerifyContext verifyContext, StringBuilder graphQLTypeSb, DataDictionary dataDictionary) {
        String name = generate(graphQLTypeSb, dataDictionary);
        if (name == null) {
            return;
        }
        verifyContext.verifyDataDictionary(dataDictionary, name);
    }

    /**
     * 生成枚举GQL类型定义
     */
    public static String generate(StringBuilder graphQLTypeSb, DataDictionary dataDictionary) {
        if (CollectionUtils.isEmpty(dataDictionary.getOptions())) {
            return null;
        }
        SummaryGenerator.generate(graphQLTypeSb, dataDictionary.getDisplayName(), ENUM_DISPLAY_NAME, dataDictionary.getSummary(), null);
        String capitalizeName = StringUtils.capitalize(dataDictionary.getName());
        graphQLTypeSb.append(ENUM).append(StringUtils.SPACE).append(capitalizeName);
        graphQLTypeSb.append(StringUtils.SPACE).append("{").append(StringUtils.LF);
        for (DataDictionaryItem item : dataDictionary.getOptions()) {
            graphQLTypeSb.append(StringUtils.SPACE);
            SummaryGenerator.generate(graphQLTypeSb, item.getDisplayName(), ENUM_ITEM_DISPLAY_NAME, item.getHelp(), null);
            graphQLTypeSb.append(StringUtils.SPACE).append(item.getName()).append(StringUtils.LF);
        }
        graphQLTypeSb.append("}").append(StringUtils.LF);
        return capitalizeName;
    }
}
