package pro.shushi.pamirs.core.common;

import java.util.Random;

/**
 * 字母帮助类
 *
 * @author Adamancy Zhang at 13:55 on 2025-03-28
 */
public class LetterHelper {

    private static final Random RANDOM_SEED = new Random();

    private static final String[] LOWER = {"", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    private static final String[] UPPER = {"", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public static final String ALL_LOWER_LETTER = "abcdefghijklmnopqrstuvwxyz";

    public static final String ALL_UPPER_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String ALL_LETTER_LIST = ALL_LOWER_LETTER + ALL_UPPER_LETTER;

    private Integer currentLowerIndex = 1;
    private Integer currentUpperIndex = 1;

    public static String getLowerByIndex(Integer index) {
        return getByIndex(LOWER, index);
    }

    public static String getUpperByIndex(Integer index) {
        return getByIndex(UPPER, index);
    }

    public static Integer getIndex(String letters) {
        if (letters == null) {
            return -1;
        }
        char[] chars = letters.toCharArray();
        int s = 0, i = 0, l = chars.length - 1, k, z;
        for (char c : chars) {
            k = l - i++;
            z = c;
            if (z >= 65 && z <= 90) {
                z = c - 64;
            } else if (z >= 97 && z <= 122) {
                z = c - 97;
            } else {
                return -1;
            }
            if (k == 0) {
                return s + z;
            } else {
                s += z * 26;
            }
        }
        return s;
    }

    public static String getRandomString(int length) {
        return getRandomString(ALL_LETTER_LIST, length);
    }

    public static String getRandomString(String fromString, int length) {
        StringBuilder randomBuilder = new StringBuilder();
        int fromStringLength = fromString.length();
        for (int i = 0; i < length; i++) {
            randomBuilder.append(fromString.charAt(RANDOM_SEED.nextInt(fromStringLength)));
        }
        return randomBuilder.toString();
    }

    public String getCurrentLower() {
        return getLowerByIndex(currentLowerIndex);
    }

    public String getCurrentUpper() {
        return getUpperByIndex(currentUpperIndex);
    }

    public String resetCurrentLower() {
        String letter = getCurrentLower();
        currentLowerIndex = 1;
        return letter;
    }

    public String resetCurrentUpper() {
        String letter = getCurrentUpper();
        currentUpperIndex = 1;
        return letter;
    }

    public String getNextLower() {
        return getLowerByIndex(currentLowerIndex + 1);
    }

    public String getNextUpper() {
        return getUpperByIndex(currentUpperIndex + 1);
    }

    public String getPriorLower() {
        return getLowerByIndex(currentLowerIndex - 1);
    }

    public String getPriorUpper() {
        return getUpperByIndex(currentUpperIndex - 1);
    }

    public String getAndGoToNextLower() {
        return getLowerByIndex(currentLowerIndex++);
    }

    public String getAndGoToNextUpper() {
        return getUpperByIndex(currentUpperIndex++);
    }

    public String getAndGoToPriorLower() {
        return getLowerByIndex(currentLowerIndex--);
    }

    public String getAndGoToPriorUpper() {
        return getUpperByIndex(currentUpperIndex--);
    }

    private static String getByIndex(String[] letters, Integer index) {
        if (index <= 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        } else if (index <= 26) {
            return letters[index];
        } else {
            StringBuilder sb = new StringBuilder();
            while (index > 0) {
                Integer remainder = index % 26;
                if (remainder.equals(0)) {
                    remainder = 26;
                }
                sb.insert(0, letters[remainder]);
                index = (index - remainder) / 26;
            }
            return sb.toString();
        }
    }
}