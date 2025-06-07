package pro.shushi.pamirs.user.api.utils;

import java.util.Random;

/**
 * @author shier
 * date  2022/6/29 下午5:09
 */
public class RandomPassword {
    private static final int max_len = 256;

    private static final int min_len = 8;

    private static final char[] PASS_SEED = "suC6vwxB7lmnyD5zAo4PQpqrIXYZ0abJKGef2LM89cH1dgF3RSTUVhiEkNOjWt".toCharArray();

    public static String genRandomNum() {
        return genRandomNum(8);
    }

    /**
     * 生成随机密码
     *
     * @param pwdLen 生成的密码的总长度(大于8的值)
     * @return 密码的字符串
     */
    public static String genRandomNum(int pwdLen) {
        int i; // 生成的随机数
        int count = 0; // 生成的密码的长度
        int pwd_len = Math.abs(pwdLen);
        if (pwd_len < min_len) {
            pwd_len = max_len / min_len;// 最大长度的1/8
        } else if (pwd_len > max_len) {
            pwd_len = max_len;
        }
        int maxNum = PASS_SEED.length;
        StringBuffer pwd = new StringBuffer();
        Random r = new Random();
        while (count < pwd_len) {
            // 生成随机数，取绝对值，防止生成负数，
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 0 && i < maxNum) {
                pwd.append(PASS_SEED[i]);
                count++;
            }
        }

        return pwd.toString();
    }

}
