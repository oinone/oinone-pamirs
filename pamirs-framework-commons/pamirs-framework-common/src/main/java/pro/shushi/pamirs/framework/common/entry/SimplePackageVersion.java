package pro.shushi.pamirs.framework.common.entry;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 包版本
 *
 * @author Adamancy Zhang at 22:52 on 2024-07-22
 */
public class SimplePackageVersion {

    private static final Map<String, String> pamirsPackageVersions = new HashMap<>();

    private final String implementationTitle;

    private final String implementationVersion;

    private final String implementationVendor;

    protected SimplePackageVersion(String implementationTitle, String implementationVersion, String implementationVendor) {
        this.implementationTitle = implementationTitle;
        this.implementationVersion = implementationVersion;
        this.implementationVendor = implementationVendor;
    }

    public String getImplementationTitle() {
        return implementationTitle;
    }

    public String getImplementationVersion() {
        return implementationVersion;
    }

    public String getImplementationVendor() {
        return implementationVendor;
    }

    public static SimplePackageVersion getPackageVersion(Class<?> clazz) {
        Package pkg = clazz.getPackage();
        if (pkg == null) {
            return null;
        }
        String title = pkg.getImplementationTitle();
        String version = pkg.getImplementationVersion();
        if (StringUtils.isAnyBlank(title, version)) {
            return null;
        }
        return new SimplePackageVersion(title, version, pkg.getImplementationVendor());
    }

    public static void collect(Class<?> clazz) {
        SimplePackageVersion pamirsPackageVersion = getPackageVersion(clazz);
        if (pamirsPackageVersion != null &&
                pamirsPackageVersion.getImplementationTitle().contains(NamespaceConstants.pamirs)) {
            String version = pamirsPackageVersion.getImplementationVersion();
            String oldVersion = SimplePackageVersion.pamirsPackageVersions.putIfAbsent(pamirsPackageVersion.getImplementationTitle(), version);
            if (!version.equals(oldVersion)) {
                throw new UnsupportedOperationException("There are multiple versions of Pamirs package. version1: " + oldVersion + ", version2: " + version);
            }
        }
    }

    public static Map<String, String> getPamirsPackageVersions() {
        return Collections.unmodifiableMap(pamirsPackageVersions);
    }

    public static void clear() {
        SimplePackageVersion.pamirsPackageVersions.clear();
    }
}
