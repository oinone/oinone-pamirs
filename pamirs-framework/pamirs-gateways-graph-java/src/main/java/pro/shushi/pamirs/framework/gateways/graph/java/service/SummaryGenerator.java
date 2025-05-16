package pro.shushi.pamirs.framework.gateways.graph.java.service;

import org.apache.commons.lang3.StringUtils;
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
                .append("显示名称：")
                .append(TypeUtils.stringNullableValueOf(displayName));
        if (StringUtils.isNotBlank(type)) {
            graphQLTypeSb.append("，类型：").append(TypeUtils.stringNullableValueOf(type));
        }
        if (null != invisible) {
            graphQLTypeSb.append("，页面可见：").append(!invisible);
        }
        if (StringUtils.isNotBlank(summary)) {
            graphQLTypeSb.append("，说明：").append(TypeUtils.stringNullableValueOf(summary));
        }
        graphQLTypeSb.append(StringUtils.LF);
    }

}
