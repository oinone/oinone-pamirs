package com.taobao.pamirs.schedule.zk;

import pro.shushi.pamirs.locale.utils.I18nUtils;

/**
 * 配置信息
 *
 * @author gjavac@gmail.com
 * @version 1.0
 * @since 2012-2-12
 */
public class ConfigNode {

    private String rootPath;

    private String configType;

    private String name;

    private String value;

    public ConfigNode() {

    }

    public ConfigNode(String rootPath, String configType, String name) {
        this.rootPath = rootPath;
        this.configType = configType;
        this.name = name;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(I18nUtils.getMessage("pamirs-middleware-tbschedule.ConfigNode.root_path")).append(rootPath).append("\n");
        buffer.append(I18nUtils.getMessage("pamirs-middleware-tbschedule.ConfigNode.config_type")).append(configType).append("\n");
        buffer.append(I18nUtils.getMessage("pamirs-middleware-tbschedule.ConfigNode.task_name")).append(name).append("\n");
        buffer.append(I18nUtils.getMessage("pamirs-middleware-tbschedule.ConfigNode.config_value")).append(value).append("\n");
        return buffer.toString();
    }
}
