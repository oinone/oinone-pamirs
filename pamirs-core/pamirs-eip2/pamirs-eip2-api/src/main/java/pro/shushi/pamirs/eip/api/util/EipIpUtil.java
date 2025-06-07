package pro.shushi.pamirs.eip.api.util;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author yeshenyue on 2025/4/11 20:52.
 */
@Slf4j
public class EipIpUtil {

    /**
     * 判断IP是否在白名单中
     *
     * @param ipAddress   IP地址
     * @param ipWhiteList 白名单列表
     * @return true表示在白名单中，false表示不在白名单中
     * @throws UnknownHostException 解析IP地址时发生错误
     */
    public static boolean isIpAllowed(String ipAddress, String[] ipWhiteList) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(ipAddress);
        byte[] addressBytes = address.getAddress();

        for (String allowedIp : ipWhiteList) {
            if (StringUtils.isBlank(allowedIp)) {
                continue;
            }

            // 处理普通IP地址
            if (ipAddress.equals(allowedIp)) {
                return true;
            }

            // 处理CIDR格式
            if (allowedIp.contains("/") && isInRange(addressBytes, allowedIp)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInRange(byte[] address, String cidr) {
        String[] parts = cidr.split("/");
        if (parts.length != 2) {
            throw PamirsException.construct(EipExpEnumerate.EIP_IP_CIDR_ILLEGAL).errThrow();
        }

        String ipPart = parts[0];
        int prefixLength = Integer.parseInt(parts[1]);

        try {
            InetAddress rangeAddress = InetAddress.getByName(ipPart);
            byte[] rangeBytes = rangeAddress.getAddress();

            int byteIndex = prefixLength / 8;
            int bitIndex = prefixLength % 8;

            for (int i = 0; i < byteIndex; i++) {
                if (address[i] != rangeBytes[i]) {
                    return false;
                }
            }

            if (bitIndex != 0) {
                int mask = -(1 << (8 - bitIndex));
                return (address[byteIndex] & mask) == (rangeBytes[byteIndex] & mask);
            }

            return true;
        } catch (UnknownHostException e) {
            log.error("解析IP地址时发生错误", e);
            return false;
        }
    }

    /**
     * 校验IP地址列表是否合法
     * 仅支持IPV4和CIDR
     */
    public static void validateIps(String[] ipWhiteList) {
        if (ipWhiteList == null) {
            return;
        }
        for (String ipOrCidr : ipWhiteList) {
            if (!isValidIP(ipOrCidr) && !isValidCIDR(ipOrCidr)) {
                throw PamirsException.construct(EipExpEnumerate.EIP_IP_CIDR_ILLEGAL)
                        .appendMsg(ipOrCidr)
                        .errThrow();
            }
        }
    }

    private static boolean isValidIP(String ip) {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }
            if (ip.contains("/")) {
                return false;
            }

            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if (i < 0 || i > 255) {
                    return false;
                }
            }
            return !ip.endsWith(".");
        } catch (NumberFormatException nfe) {
            log.error("解析IP地址时发生错误:{}", ip, nfe);
            return false;
        }
    }

    public static boolean isValidCIDR(String cidr) {
        try {
            if (cidr == null || !cidr.contains("/")) {
                return false;
            }
            String[] parts = cidr.split("/");
            if (parts.length != 2) {
                return false;
            }

            String ip = parts[0];
            int prefix = Integer.parseInt(parts[1]);

            return isValidIP(ip) && prefix >= 0 && prefix <= 32;
        } catch (NumberFormatException e) {
            log.error("解析CIDR时发生错误:{}", cidr, e);
            return false;
        }
    }
}
