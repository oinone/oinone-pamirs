package pro.shushi.pamirs.user.api.utils;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.UUID;

/**
 * @author shier
 * date 2020/4/7
 */
public class PasswordEncoder {
    private static final HashFunction SHA512 = Hashing.sha512();

    private static final Splitter SPLITTER = Splitter.on("&").trimResults();

    private static final Joiner JOINER = Joiner.on("&").skipNulls();

    private static final HashFunction MD5 = Hashing.md5();

    public static String encode(CharSequence rawPassword) {
        String salt = MD5.newHasher().putString(UUID.randomUUID().toString(), Charsets.UTF_8).putLong(System.currentTimeMillis()).hash()
                .toString().substring(0, 4);
        String realPassword = SHA512.hashString(rawPassword + salt, Charsets.UTF_8).toString().substring(0, 20);
        return JOINER.join(salt, realPassword);
    }

    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        Iterable<String> parts = SPLITTER.split(encodedPassword);
        String salt = Iterables.get(parts, 0, null);
        String realPassword = Iterables.get(parts, 1, null);
        if (salt == null || realPassword == null) {
            return false;
        }
        return Objects.equal(SHA512.hashString(rawPassword + salt, Charsets.UTF_8).toString().substring(0, 20), realPassword);
    }
}
