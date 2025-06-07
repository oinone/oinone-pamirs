package pro.shushi.pamirs.message.utils;

import com.alibaba.fastjson.JSON;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * VerificationCodeUtils
 *
 * @author yakir on 2019/08/22 15:44.
 */
public class VerificationCodeUtils {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();


    public static String code() {
        StringBuilder smsCode = new StringBuilder(6);
        Random codeRandom = new Random();
        for (int i = 0; i < 6; i++) {
            smsCode.append(codeRandom.nextInt(9));
        }
        return smsCode.toString();
    }

    public static String code(Map<String, String> map) {
        return JSON.toJSONString(map);
    }

    public static String placeHolderExec(String template, String param) {
        return template.replace("#{code}", param);
    }

    public static Date plusSec(Integer seconds) {
        Instant instant = LocalDateTime.now().plusSeconds(Optional.ofNullable(seconds).orElse(300)).atZone(ZONE_ID).toInstant();
        return Date.from(instant);
    }

    public static String randomString() {
        int length = 32;
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
