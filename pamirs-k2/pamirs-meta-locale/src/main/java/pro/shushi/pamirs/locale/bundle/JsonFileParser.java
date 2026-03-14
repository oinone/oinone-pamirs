package pro.shushi.pamirs.locale.bundle;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * JSON File Parser
 *
 * @author Adamancy Zhang at 15:31 on 2026-03-13
 */
class JsonFileParser implements FileParser {

    @Override
    public String[] getFileExtensions() {
        return new String[]{"json"};
    }

    @Override
    public Map<String, Object> parse(InputStream inputStream) throws IOException {
        return JSON.parseObject(inputStream, Map.class);
    }
}
