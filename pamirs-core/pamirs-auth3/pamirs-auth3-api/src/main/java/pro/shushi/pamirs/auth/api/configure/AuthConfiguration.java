package pro.shushi.pamirs.auth.api.configure;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 权限配置
 *
 * @author Adamancy Zhang at 16:28 on 2024-01-06
 */
@Configuration
@ConfigurationProperties(prefix = ConfigureConstants.PAMIRS_AUTH_CONFIG_PREFIX)
@Validated
@RefreshScope
public class AuthConfiguration {

    private LoaderProperties loader = new LoaderProperties();

    private AccessProperties access = new AccessProperties();

    private ManagementProperties management = new ManagementProperties();

    private DataProperties data = new DataProperties();

    private List<FunFilter> funFilter = new ArrayList<>();

    private List<FunFilter> funFilterOnlyLogin = new ArrayList<>();

    public LoaderProperties getLoader() {
        return loader;
    }

    public void setLoader(LoaderProperties loader) {
        this.loader = loader;
    }

    public AccessProperties getAccess() {
        return access;
    }

    public void setAccess(AccessProperties access) {
        this.access = access;
    }

    public ManagementProperties getManagement() {
        return management;
    }

    public void setManagement(ManagementProperties management) {
        this.management = management;
    }

    public DataProperties getData() {
        return data;
    }

    public void setData(DataProperties data) {
        this.data = data;
    }

    public List<FunFilter> getFunFilter() {
        return funFilter;
    }

    public void setFunFilter(List<FunFilter> funFilter) {
        this.funFilter = funFilter;
    }

    public List<FunFilter> getFunFilterOnlyLogin() {
        return funFilterOnlyLogin;
    }

    public void setFunFilterOnlyLogin(List<FunFilter> funFilterOnlyLogin) {
        this.funFilterOnlyLogin = funFilterOnlyLogin;
    }

    public static class FunFilter {

        public FunFilter() {
        }

        public FunFilter(@NotBlank String namespace, @NotBlank String fun) {
            this.namespace = namespace;
            this.fun = fun;
        }

        @NotBlank
        private String namespace;

        @NotBlank
        private String fun;

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getFun() {
            return fun;
        }

        public void setFun(String fun) {
            this.fun = fun;
        }
    }

    // region loader properties

    public static class LoaderProperties {

        private String nodeConverter;

        private String pathGenerator;

        private boolean usingLoadCache = true;

        public String getNodeConverter() {
            return nodeConverter;
        }

        public void setNodeConverter(String nodeConverter) {
            this.nodeConverter = nodeConverter;
        }

        public String getPathGenerator() {
            return pathGenerator;
        }

        public void setPathGenerator(String pathGenerator) {
            this.pathGenerator = pathGenerator;
        }

        public boolean getUsingLoadCache() {
            return usingLoadCache;
        }

        public void setUsingLoadCache(boolean usingLoadCache) {
            this.usingLoadCache = usingLoadCache;
        }
    }

    // endregion

    // region access permission properties

    public static class AccessProperties {

        private Set<AccessResource> resources;

        private AccessStrategy strategy;

        private boolean moduleMatch = true;

        public Set<AccessResource> getResources() {
            return resources;
        }

        public void setResources(Set<AccessResource> resources) {
            this.resources = resources;
        }

        public AccessStrategy getStrategy() {
            return strategy;
        }

        public void setStrategy(AccessStrategy strategy) {
            this.strategy = strategy;
        }

        public boolean getModuleMatch() {
            return moduleMatch;
        }

        public void setModuleMatch(boolean moduleMatch) {
            this.moduleMatch = moduleMatch;
        }
    }

    public static class AccessStrategy {

        private String loader;

        public String getLoader() {
            return loader;
        }

        public void setLoader(String loader) {
            this.loader = loader;
        }
    }

    public enum AccessResource {
        MODULE,
        HOMEPAGE,
        MENU,
        VIEW,
        ACTION,
        VIEW_ACTION,
        SERVER_ACTION,
        URL_ACTION,
        CLIENT_ACTION
    }

    // endregion

    // region management permission properties

    public static class ManagementProperties {

        private Set<ManagementResource> resources;

        private ManagementStrategy strategy;

        private boolean optimize = true;

        public Set<ManagementResource> getResources() {
            return resources;
        }

        public void setResources(Set<ManagementResource> resources) {
            this.resources = resources;
        }

        public ManagementStrategy getStrategy() {
            return strategy;
        }

        public void setStrategy(ManagementStrategy strategy) {
            this.strategy = strategy;
        }

        public boolean getOptimize() {
            return optimize;
        }

        public void setOptimize(boolean optimize) {
            this.optimize = optimize;
        }
    }

    public static class ManagementStrategy {

        private String loader;

        private String cacheLoader;

        private ManagementFieldStrategy field;

        public String getLoader() {
            return loader;
        }

        public void setLoader(String loader) {
            this.loader = loader;
        }

        public String getCacheLoader() {
            return cacheLoader;
        }

        public void setCacheLoader(String cacheLoader) {
            this.cacheLoader = cacheLoader;
        }

        public ManagementFieldStrategy getField() {
            return field;
        }

        public void setField(ManagementFieldStrategy field) {
            this.field = field;
        }
    }

    public static class ManagementFieldStrategy {

        private boolean showAll = true;

        public boolean getShowAll() {
            return showAll;
        }

        public void setShowAll(boolean showAll) {
            this.showAll = showAll;
        }
    }

    public enum ManagementResource {
        MODULE,
        HOMEPAGE,
        MENU
    }

    // endregion

    // region data permission properties

    public static class DataProperties {

        private Set<DataResource> resources;

        private DataStrategy strategy;

        public Set<DataResource> getResources() {
            return resources;
        }

        public void setResources(Set<DataResource> resources) {
            this.resources = resources;
        }

        public DataStrategy getStrategy() {
            return strategy;
        }

        public void setStrategy(DataStrategy strategy) {
            this.strategy = strategy;
        }
    }

    public static class DataStrategy {

        private String beanName;

        public String getBeanName() {
            return beanName;
        }

        public void setBeanName(String beanName) {
            this.beanName = beanName;
        }
    }

    public enum DataResource {
        MODEL,
        FIELD,
        ROW
    }

    // endregion
}
