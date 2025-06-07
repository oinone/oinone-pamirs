package pro.shushi.pamirs.auth.api.loader;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.configure.AuthConfiguration;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.Optional;
import java.util.Set;

/**
 * <h>权限节点加载器帮助类</h>
 * <p>
 * 此加载器仅提供内部使用，不直接向外暴露。
 * <br>
 * 使用节点相关操作，请参考{@link pro.shushi.pamirs.auth.api.helper.AuthNodeConvertHelper}
 * <br>
 * 使用权限路径相关操作，请参考{@link pro.shushi.pamirs.auth.api.helper.AuthPathGenerateHelper}
 * </p>
 *
 * @author Adamancy Zhang at 10:47 on 2024-01-15
 */
@Component
public class PermissionNodeLoader {

    @Autowired
    private AuthConfiguration authConfiguration;

    public ResourcePermissionNodeConverter getNodeConverter() {
        String beanName = Optional.ofNullable(getLoaderProperties())
                .map(AuthConfiguration.LoaderProperties::getNodeConverter)
                .filter(StringUtils::isNotBlank)
                .orElse(AuthConstants.RESOURCE_PERMISSION_NODE_CONVERTER_BEAN_NAME);
        return BeanDefinitionUtils.getBean(beanName, ResourcePermissionNodeConverter.class);
    }

    public ResourcePermissionNodePathGenerator getPathGenerator() {
        String beanName = Optional.ofNullable(getLoaderProperties())
                .map(AuthConfiguration.LoaderProperties::getPathGenerator)
                .filter(StringUtils::isNotBlank)
                .orElse(AuthConstants.RESOURCE_PERMISSION_PATH_GENERATOR_BEAN_NAME);
        return BeanDefinitionUtils.getBean(beanName, ResourcePermissionNodePathGenerator.class);
    }

    public ResourcePermissionNodeLoader getAccessLoader() {
        String beanName = Optional.ofNullable(getAccessProperties())
                .map(AuthConfiguration.AccessProperties::getStrategy)
                .map(AuthConfiguration.AccessStrategy::getLoader)
                .filter(StringUtils::isNotBlank)
                .orElse(AuthConstants.ACCESS_LOADER_BEAN_NAME);
        return BeanDefinitionUtils.getBean(beanName, ResourcePermissionNodeLoader.class);
    }

    public ResourcePermissionNodeLoader getManagementLoader() {
        String beanName = Optional.ofNullable(getManagementProperties())
                .map(AuthConfiguration.ManagementProperties::getStrategy)
                .map(AuthConfiguration.ManagementStrategy::getLoader)
                .filter(StringUtils::isNotBlank)
                .orElse(AuthConstants.MANAGEMENT_LOADER_BEAN_NAME);
        return BeanDefinitionUtils.getBean(beanName, ResourcePermissionNodeLoader.class);
    }

    public ResourcePermissionNodeLoader getManagementCacheLoader() {
        String beanName = Optional.ofNullable(getManagementProperties())
                .map(AuthConfiguration.ManagementProperties::getStrategy)
                .map(AuthConfiguration.ManagementStrategy::getCacheLoader)
                .filter(StringUtils::isNotBlank)
                .orElse(AuthConstants.MANAGEMENT_CACHE_LOADER_BEAN_NAME);
        return BeanDefinitionUtils.getBean(beanName, ResourcePermissionNodeLoader.class);
    }

    public boolean isUsingLoadCache() {
        return Optional.ofNullable(getLoaderProperties())
                .map(AuthConfiguration.LoaderProperties::getUsingLoadCache)
                .orElse(true);
    }

    public boolean isLoadAllField() {
        return Optional.ofNullable(getManagementProperties())
                .map(AuthConfiguration.ManagementProperties::getStrategy)
                .map(AuthConfiguration.ManagementStrategy::getField)
                .map(AuthConfiguration.ManagementFieldStrategy::getShowAll)
                .orElse(true);
    }

    public boolean isLoadAccessModuleNode() {
        return isContains(getAccessProperties().getResources(), AuthConfiguration.AccessResource.MODULE);
    }

    public boolean isLoadAccessHomepageNode() {
        return isContains(getAccessProperties().getResources(), AuthConfiguration.AccessResource.HOMEPAGE);
    }

    public boolean isLoadAccessMenuNode() {
        return isContains(getAccessProperties().getResources(), AuthConfiguration.AccessResource.MENU);
    }

    public boolean isLoadDataModelNode() {
        return isContains(getDataProperties().getResources(), AuthConfiguration.DataResource.MODEL);
    }

    public boolean isLoadDataFieldNode() {
        return isContains(getDataProperties().getResources(), AuthConfiguration.DataResource.FIELD);
    }

    public boolean isLoadDataRowNode() {
        return isContains(getDataProperties().getResources(), AuthConfiguration.DataResource.ROW);
    }

    public boolean isLoadManagementModuleNode() {
        return isContains(getManagementProperties().getResources(), AuthConfiguration.ManagementResource.MODULE);
    }

    public boolean isLoadManagementHomepageNode() {
        return isContains(getManagementProperties().getResources(), AuthConfiguration.ManagementResource.HOMEPAGE);
    }

    public boolean isLoadManagementMenuNode() {
        return isContains(getManagementProperties().getResources(), AuthConfiguration.ManagementResource.MENU);
    }

    public boolean isOptimizeManagementNode() {
        return getManagementProperties().getOptimize();
    }

    private AuthConfiguration.LoaderProperties getLoaderProperties() {
        return authConfiguration.getLoader();
    }

    private AuthConfiguration.AccessProperties getAccessProperties() {
        return authConfiguration.getAccess();
    }

    private AuthConfiguration.ManagementProperties getManagementProperties() {
        return authConfiguration.getManagement();
    }

    private AuthConfiguration.DataProperties getDataProperties() {
        return authConfiguration.getData();
    }

    private static <T> boolean isContains(Set<T> properties, T value) {
        if (CollectionUtils.isEmpty(properties)) {
            return true;
        }
        return properties.contains(value);
    }
}
