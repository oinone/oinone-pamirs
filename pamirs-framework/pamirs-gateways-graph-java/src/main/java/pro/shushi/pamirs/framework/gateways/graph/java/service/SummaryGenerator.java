package pro.shushi.pamirs.framework.gateways.graph.java.service;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.util.TypeUtils;

/**
 * 文档生成器
 * <p>
 * 2020/10/20 10:42 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class SummaryGenerator {

    public static void generate(StringBuilder graphQLTypeSb, String displayName, String type, String summary, Boolean invisible) {
        graphQLTypeSb.append(CharacterConstants.SEPARATOR_OCTOTHORPE)
                .append(I18nUtils.getMessage("pamirs-gateways-graph-java.SummaryGenerator.display_name"))
                .append(TypeUtils.stringNullableValueOf(displayName));
        if (StringUtils.isNotBlank(type)) {
            graphQLTypeSb.append(I18nUtils.getMessage("pamirs-gateways-graph-java.SummaryGenerator.type")).append(TypeUtils.stringNullableValueOf(type));
        }
        if (null != invisible) {
            graphQLTypeSb.append(I18nUtils.getMessage("pamirs-gateways-graph-java.SummaryGenerator.page_visible")).append(!invisible);
        }
        if (StringUtils.isNotBlank(summary)) {
            graphQLTypeSb.append(I18nUtils.getMessage("pamirs-gateways-graph-java.SummaryGenerator.description")).append(TypeUtils.stringNullableValueOf(summary));
        }
        graphQLTypeSb.append(StringUtils.LF);
    }

}
