package pro.shushi.pamirs.framework.connectors.data.serializer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import pro.shushi.pamirs.meta.api.prefix.KeyPrefixManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_COLON;

/**
 * Pamirs Redis 字符串序列化
 *
 * @author Adamancy Zhang at 11:18 on 2021-08-18
 */
public class PamirsStringRedisSerializer extends StringRedisSerializer {

    private final String prefix;

    private final int prefixLength;

    public PamirsStringRedisSerializer(String prefix) {
        this(StandardCharsets.UTF_8, prefix);
    }

    public PamirsStringRedisSerializer(Charset charset, String prefix) {
        super(charset);
        if (StringUtils.isBlank(prefix)) {
            prefix = CharacterConstants.SEPARATOR_EMPTY;
        }
        this.prefix = fixPrefix(prefix).trim();
        this.prefixLength = this.prefix.length();
    }

    @Override
    public byte[] serialize(String string) {
        return super.serialize(serializeString(string));
    }

    //jedis.pipelined 方式使用
    public String serializeString(String string) {
        if (string == null) {
            return null;
        }
        if (this.prefixLength != 0) {
            string = prefix + string;
        }
        return KeyPrefixManager.generate(SEPARATOR_COLON, SEPARATOR_COLON) + string;
    }

    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public String deserialize(byte[] bytes) {
        String key = super.deserialize(bytes);
        if (key == null) {
            return null;
        }
        String kp = KeyPrefixManager.generate(SEPARATOR_COLON, SEPARATOR_COLON);
        String p = kp + this.prefix;
        int len = kp.length() + this.prefixLength;
        if (len == 0) {
            return key;
        }
        if (key.startsWith(p)) {
            key = key.substring(len);
        }
        return key;
    }

    private String fixPrefix(String prefix) {
        if (StringUtils.isBlank(prefix)) {
            return prefix;
        }
        if (prefix.endsWith(SEPARATOR_COLON)) {
            return prefix;
        } else {
            return prefix + SEPARATOR_COLON;
        }
    }
}
