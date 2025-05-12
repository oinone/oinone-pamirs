package pro.shushi.pamirs.meta.api.prefix;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableConfig;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据前缀帮助类
 * <p>
 * 2021/9/16 9:47 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class DataPrefixManager {

    /**
     * 获取数据库名前缀
     *
     * @param module 模块
     * @param model  模型
     * @param key    键
     * @return 前缀
     */
    public static String dsPrefix(String module, String model, Object key) {
        if (null == key) {
            return null;
        }
        Map<String, Object> context = new HashMap<>(4);
        context.put(KeyContextConstants.KEY_TYPE, KeyPrefixTypeEnum.dsKey);
        context.put(KeyContextConstants.MODULE, module);
        context.put(KeyContextConstants.MODEL, model);
        String keyPrefix = KeyPrefixManager.generate(context, CharacterConstants.SEPARATOR_UNDERLINE, CharacterConstants.SEPARATOR_UNDERLINE);
        return keyPrefix + key;
    }

    /**
     * 获取数据表名前缀
     *
     * @param module 模块
     * @param model  模型
     * @param key    键
     * @return 前缀
     */
    public static String tablePrefix(String module, String model, String key) {
        if (null == key) {
            return null;
        }
        Map<String, Object> context = new HashMap<>(4);
        context.put(KeyContextConstants.KEY_TYPE, KeyPrefixTypeEnum.table);
        context.put(KeyContextConstants.MODULE, module);
        context.put(KeyContextConstants.MODEL, model);
        String keyPrefix = KeyPrefixManager.generate(context, CharacterConstants.SEPARATOR_UNDERLINE, CharacterConstants.SEPARATOR_UNDERLINE);
        String table = keyPrefix + key;
        if (StringUtils.isNotBlank(model)) {
            PamirsTableConfig pamirsTableConfig = PamirsTableInfo.fetchPamirsTableConfig(model);
            if (pamirsTableConfig.getTableNameCaseSensitive()) {
                Boolean capitalMode = pamirsTableConfig.getCapitalMode();
                if (null != capitalMode && capitalMode) {
                    table = table.toUpperCase();
                }
                return table;
            }
        }
        return table.toLowerCase();
    }

    /**
     * 获取数据key前缀
     *
     * @param module 模块
     * @param model  模型
     * @param key    键
     * @return 前缀
     */
    public static String keyPrefix(String module, String model, String key) {
        if (null == key) {
            return null;
        }
        Map<String, Object> context = new HashMap<>(4);
        context.put(KeyContextConstants.KEY_TYPE, KeyPrefixTypeEnum.key);
        context.put(KeyContextConstants.MODULE, module);
        context.put(KeyContextConstants.MODEL, model);
        String keyPrefix = KeyPrefixManager.generate(context, CharacterConstants.SEPARATOR_UNDERLINE, CharacterConstants.SEPARATOR_UNDERLINE);
        return keyPrefix + key;
    }

}
