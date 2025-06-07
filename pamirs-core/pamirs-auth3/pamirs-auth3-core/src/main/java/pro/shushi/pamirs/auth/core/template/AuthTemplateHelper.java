package pro.shushi.pamirs.auth.core.template;

import com.alibaba.fastjson.JSON;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.file.api.builder.CellDefinitionBuilder;
import pro.shushi.pamirs.file.api.builder.HeaderDefinitionBuilder;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * 权限模板帮助类
 *
 * @author Adamancy Zhang at 12:11 on 2024-03-28
 */
public class AuthTemplateHelper {

    private AuthTemplateHelper() {
        // reject create object
    }

    public static void setActiveFormat(CellDefinitionBuilder<HeaderDefinitionBuilder> builder) {
        builder.setType(ExcelValueTypeEnum.BOOLEAN).setFormat(JSON.toJSONString(MapHelper.newInstance()
                .put("true", "激活")
                .put("false", "未激活")
                .build()));
    }

    public static void setResourceTypeFormat(CellDefinitionBuilder<HeaderDefinitionBuilder> builder) {
        builder.setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSON.toJSONString(Arrays.stream(ResourcePermissionTypeEnum.values())
                        .collect(Collectors.toMap(ResourcePermissionTypeEnum::value, ResourcePermissionTypeEnum::displayName, MapHelper.throwingMerger(), LinkedHashMap::new))
                ));
    }

    public static void setResourceSubtypeFormat(CellDefinitionBuilder<HeaderDefinitionBuilder> builder) {
        builder.setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSON.toJSONString(Arrays.stream(ResourcePermissionSubtypeEnum.values())
                        .collect(Collectors.toMap(ResourcePermissionSubtypeEnum::value, ResourcePermissionSubtypeEnum::displayName, MapHelper.throwingMerger(), LinkedHashMap::new))
                ));
    }

    public static void setSourceFormat(CellDefinitionBuilder<HeaderDefinitionBuilder> builder) {
        builder.setType(ExcelValueTypeEnum.ENUMERATION).setFormat(JSON.toJSONString(MapHelper.newInstance()
                .put(AuthorizationSourceEnum.SYSTEM.value(), AuthorizationSourceEnum.SYSTEM.displayName())
                .put(AuthorizationSourceEnum.MANUAL.value(), AuthorizationSourceEnum.MANUAL.displayName())
                .build()));
    }

    public static void setInheritFormat(CellDefinitionBuilder<HeaderDefinitionBuilder> builder) {
        builder.setType(ExcelValueTypeEnum.BOOLEAN).setFormat(JSON.toJSONString(MapHelper.newInstance()
                .put("true", "有效")
                .put("false", "无效")
                .build()));
    }

    public static String getSourceFilter() {
        return "source != '" + AuthorizationSourceEnum.BUILD_IN.value() + "'";
    }
}
