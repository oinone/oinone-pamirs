package pro.shushi.pamirs.boot.standard.entity;

import org.springframework.boot.ApplicationArguments;
import org.springframework.core.env.Environment;
import pro.shushi.pamirs.boot.standard.utils.ShortCodeHelper;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 启动环境信息
 *
 * @author Gesi at 14:24 on 2024/11/22
 */
public class StartupEnvironmentInfo implements Serializable {

    private static final long serialVersionUID = -586263388690590514L;

    private static volatile String CURRENT_STARTUP_CODE;

    private static volatile StartupEnvironmentInfo CURRENT_STARTUP_ENVIRONMENT_INFO;

    private String startupCode;

    private String hostname;
    private String hostAddress;
    private String[] networkInterfaces;

    private String osName;
    private String osVersion;
    private String osArch;

    private String javaVersion;
    private String javaVendor;
    private String javaHome;
    private String javaClassVersion;
    private String javaRuntimeName;
    private String javaRuntimeVersion;
    private String javaSpecificationName;
    private String javaSpecificationVendor;
    private String javaSpecificationVersion;

    private String vmName;
    private String vmInfo;
    private String vmVersion;
    private String vmVendor;
    private String vmSpecName;
    private String vmSpecVersion;
    private String vmSpecVendor;
    private String[] vmOptions;

    private String[] optionArgs;

    private String startTime;

    private String fileEncoding;

    private String pid;

    private String userHome;
    private String userName;
    private String userCountry;
    private String userLanguage;
    private String userTimezone;
    private String userDir;

    public StartupEnvironmentInfo() {
    }

    public static String getCurrentStartupCode() {
        if (CURRENT_STARTUP_CODE == null) {
            throw new IllegalStateException("Startup environment info not initialized!");
        }
        return CURRENT_STARTUP_CODE;
    }

    public static String getCurrentStartupCode(Environment environment, ApplicationArguments arguments) {
        if (CURRENT_STARTUP_CODE == null) {
            getStartupEnvironmentInfo(environment, arguments);
        }
        return CURRENT_STARTUP_CODE;
    }

    public static StartupEnvironmentInfo getStartupEnvironmentInfo(Environment environment, ApplicationArguments arguments) {
        if (CURRENT_STARTUP_ENVIRONMENT_INFO == null) {
            CURRENT_STARTUP_ENVIRONMENT_INFO = new StartupEnvironmentInfo(environment, arguments);
        }
        return CURRENT_STARTUP_ENVIRONMENT_INFO;
    }

    private StartupEnvironmentInfo(Environment environment, ApplicationArguments arguments) {
        try {
            // ip 地址和 主机名
            InetAddress localHost = InetAddress.getLocalHost();
            this.hostname = localHost.getHostName();
            this.hostAddress = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            this.hostname = "-";
            this.hostAddress = "-";
        }

        String[] macAddress;
        try {
            // 网卡 mac 地址和对应 ip 地址
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            List<String> networkInterfacesRecord = new ArrayList<>();
            List<String> macAddresses = new ArrayList<>();

            while (networkInterfaces.hasMoreElements()) {
                StringBuilder networkInterfaceStr = new StringBuilder();
                try {
                    NetworkInterface networkInterface = networkInterfaces.nextElement();
                    // 跳过无效或虚拟接口
                    if (networkInterface == null || !networkInterface.isUp() || networkInterface.isLoopback()) {
                        continue;
                    }

                    networkInterfaceStr.append(networkInterface.getName());

                    byte[] macBytes = networkInterface.getHardwareAddress();
                    if (macBytes != null) {
                        StringBuilder macAddressStr = new StringBuilder();
                        for (int i = 0; i < macBytes.length; i++) {
                            macAddressStr.append(String.format("%02X", macBytes[i]));
                            if (i < macBytes.length - 1) {
                                macAddressStr.append("-");
                            }
                        }
                        networkInterfaceStr.append(" ").append(macAddressStr);
                        macAddresses.add(macAddressStr.toString());
                    } else {
                        networkInterfaceStr.append("  - ");
                    }

                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        networkInterfaceStr.append(" ").append(inetAddress.getHostAddress());
                    }
                    networkInterfacesRecord.add(networkInterfaceStr.toString());
                } catch (SocketException e) {
                    // do nothing
                }
            }
            this.networkInterfaces = networkInterfacesRecord.toArray(new String[0]);
            macAddress = macAddresses.toArray(new String[0]);
        } catch (SocketException e) {
            this.networkInterfaces = new String[0];
            macAddress = new String[0];
        }
        Arrays.sort(this.networkInterfaces);
        Arrays.sort(macAddress);

        try {
            // 系统信息
            this.osName = environment.getProperty("os.name");
            this.osVersion = environment.getProperty("os.version");
            this.osArch = environment.getProperty("os.arch");
        } catch (SecurityException e) {
            // do nothing
        }

        try {
            // java信息
            this.javaVersion = environment.getProperty("java.version");
            this.javaVendor = environment.getProperty("java.vendor");
            this.javaHome = environment.getProperty("java.home");
            this.javaClassVersion = environment.getProperty("java.class.version");
            this.javaRuntimeName = environment.getProperty("java.runtime.name");
            this.javaRuntimeVersion = environment.getProperty("java.runtime.version");
            this.javaSpecificationName = environment.getProperty("java.specification.name");
            this.javaSpecificationVendor = environment.getProperty("java.specification.vendor");
            this.javaSpecificationVersion = environment.getProperty("java.specification.version");
        } catch (SecurityException e) {
            // do nothing
        }

        try {
            // jvm 信息
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            this.vmName = runtimeMXBean.getVmName();
            this.vmInfo = runtimeMXBean.getName();
            this.vmVersion = runtimeMXBean.getVmVersion();
            this.vmVendor = runtimeMXBean.getVmVendor();
            this.vmSpecName = runtimeMXBean.getSpecName();
            this.vmSpecVersion = runtimeMXBean.getSpecVersion();
            this.vmSpecVendor = runtimeMXBean.getSpecVendor();
            this.startTime = runtimeMXBean.getStartTime() + "";
        } catch (SecurityException e) {
            // do nothing
        }

        try {
            // jvm参数
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            this.vmOptions = runtimeMXBean.getInputArguments().toArray(new String[0]);
        } catch (SecurityException e) {
            // do nothing
        }

        try {
            // 启动参数
            this.optionArgs = arguments.getSourceArgs();
        } catch (SecurityException e) {
            // do nothing
        }

        try {
            // 用户信息
            this.userHome = environment.getProperty("user.home");
            this.userName = environment.getProperty("user.name");
            this.userCountry = environment.getProperty("user.country");
            this.userLanguage = environment.getProperty("user.language");
            this.userTimezone = environment.getProperty("user.timezone");
            this.userDir = environment.getProperty("user.dir");
        } catch (SecurityException e) {
            // do nothing
        }

        try {
            // 其他参数
            this.fileEncoding = Charset.defaultCharset().displayName();
        } catch (SecurityException e) {
            // do nothing
        }

        try {
            this.pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        } catch (Exception e) {
            this.pid = ManagementFactory.getRuntimeMXBean().getName();
        }

        String identifiers = Arrays.stream(macAddress)
                .map(it -> it + "-" + this.userName)
                .map(ShortCodeHelper::md5)
                .collect(Collectors.joining("_"));
        // 生成唯一启动码
        this.startupCode =
                UUID.randomUUID().toString().replace("-", "")
                        + "_" + new SimpleDateFormat("yyyyMMdd-HHmmsssss").format(new Date()).replace("-", "")
                        + "_" + identifiers;
        this.startupCode = this.startupCode.length() > 256 ? this.startupCode.substring(0, 256) : this.startupCode;
        CURRENT_STARTUP_CODE = this.startupCode;
    }

    public String getStartupCode() {
        return startupCode;
    }

    public void setStartupCode(String startupCode) {
        this.startupCode = startupCode;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String[] getNetworkInterfaces() {
        return networkInterfaces;
    }

    public void setNetworkInterfaces(String[] networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getJavaVendor() {
        return javaVendor;
    }

    public void setJavaVendor(String javaVendor) {
        this.javaVendor = javaVendor;
    }

    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public String getJavaClassVersion() {
        return javaClassVersion;
    }

    public void setJavaClassVersion(String javaClassVersion) {
        this.javaClassVersion = javaClassVersion;
    }

    public String getJavaRuntimeName() {
        return javaRuntimeName;
    }

    public void setJavaRuntimeName(String javaRuntimeName) {
        this.javaRuntimeName = javaRuntimeName;
    }

    public String getJavaRuntimeVersion() {
        return javaRuntimeVersion;
    }

    public void setJavaRuntimeVersion(String javaRuntimeVersion) {
        this.javaRuntimeVersion = javaRuntimeVersion;
    }

    public String getJavaSpecificationName() {
        return javaSpecificationName;
    }

    public void setJavaSpecificationName(String javaSpecificationName) {
        this.javaSpecificationName = javaSpecificationName;
    }

    public String getJavaSpecificationVendor() {
        return javaSpecificationVendor;
    }

    public void setJavaSpecificationVendor(String javaSpecificationVendor) {
        this.javaSpecificationVendor = javaSpecificationVendor;
    }

    public String getJavaSpecificationVersion() {
        return javaSpecificationVersion;
    }

    public void setJavaSpecificationVersion(String javaSpecificationVersion) {
        this.javaSpecificationVersion = javaSpecificationVersion;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public String getVmInfo() {
        return vmInfo;
    }

    public void setVmInfo(String vmInfo) {
        this.vmInfo = vmInfo;
    }

    public String getVmVersion() {
        return vmVersion;
    }

    public void setVmVersion(String vmVersion) {
        this.vmVersion = vmVersion;
    }

    public String getVmVendor() {
        return vmVendor;
    }

    public void setVmVendor(String vmVendor) {
        this.vmVendor = vmVendor;
    }

    public String getVmSpecName() {
        return vmSpecName;
    }

    public void setVmSpecName(String vmSpecName) {
        this.vmSpecName = vmSpecName;
    }

    public String getVmSpecVersion() {
        return vmSpecVersion;
    }

    public void setVmSpecVersion(String vmSpecVersion) {
        this.vmSpecVersion = vmSpecVersion;
    }

    public String getVmSpecVendor() {
        return vmSpecVendor;
    }

    public void setVmSpecVendor(String vmSpecVendor) {
        this.vmSpecVendor = vmSpecVendor;
    }

    public String[] getVmOptions() {
        return vmOptions;
    }

    public void setVmOptions(String[] vmOptions) {
        this.vmOptions = vmOptions;
    }

    public String[] getOptionArgs() {
        return optionArgs;
    }

    public void setOptionArgs(String[] optionArgs) {
        this.optionArgs = optionArgs;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFileEncoding() {
        return fileEncoding;
    }

    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUserHome() {
        return userHome;
    }

    public void setUserHome(String userHome) {
        this.userHome = userHome;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    public String getUserLanguage() {
        return userLanguage;
    }

    public void setUserLanguage(String userLanguage) {
        this.userLanguage = userLanguage;
    }

    public String getUserTimezone() {
        return userTimezone;
    }

    public void setUserTimezone(String userTimezone) {
        this.userTimezone = userTimezone;
    }

    public String getUserDir() {
        return userDir;
    }

    public void setUserDir(String userDir) {
        this.userDir = userDir;
    }

    @Override
    public String toString() {
        return "StartupEnvironmentInfo{" +
                "startupCode='" + startupCode + '\'' +
                ", hostname='" + hostname + '\'' +
                ", hostAddress='" + hostAddress + '\'' +
                ", networkInterfaces=" + Arrays.toString(networkInterfaces) +
                ", osName='" + osName + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", osArch='" + osArch + '\'' +
                ", javaVersion='" + javaVersion + '\'' +
                ", javaVendor='" + javaVendor + '\'' +
                ", javaHome='" + javaHome + '\'' +
                ", javaClassVersion='" + javaClassVersion + '\'' +
                ", javaRuntimeName='" + javaRuntimeName + '\'' +
                ", javaRuntimeVersion='" + javaRuntimeVersion + '\'' +
                ", javaSpecificationName='" + javaSpecificationName + '\'' +
                ", javaSpecificationVendor='" + javaSpecificationVendor + '\'' +
                ", javaSpecificationVersion='" + javaSpecificationVersion + '\'' +
                ", vmName='" + vmName + '\'' +
                ", vmInfo='" + vmInfo + '\'' +
                ", vmVersion='" + vmVersion + '\'' +
                ", vmVendor='" + vmVendor + '\'' +
                ", vmSpecName='" + vmSpecName + '\'' +
                ", vmSpecVersion='" + vmSpecVersion + '\'' +
                ", vmSpecVendor='" + vmSpecVendor + '\'' +
                ", vmOptions=" + Arrays.toString(vmOptions) +
                ", optionArgs=" + Arrays.toString(optionArgs) +
                ", startTime='" + startTime + '\'' +
                ", fileEncoding='" + fileEncoding + '\'' +
                ", pid='" + pid + '\'' +
                ", userHome='" + userHome + '\'' +
                ", userName='" + userName + '\'' +
                ", userCountry='" + userCountry + '\'' +
                ", userLanguage='" + userLanguage + '\'' +
                ", userTimezone='" + userTimezone + '\'' +
                ", userDir='" + userDir + '\'' +
                '}';
    }
}
