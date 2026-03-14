package pro.shushi.pamirs.locale.bundle;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * YAML File Parser
 *
 * @author Adamancy Zhang at 15:32 on 2026-03-13
 */
class YamlFileParser implements FileParser {

    @Override
    public String[] getFileExtensions() {
        return new String[]{"yml", "yaml"};
    }

    @Override
    public Map<String, Object> parse(InputStream inputStream) {
        return new Yaml().load(inputStream);
    }
}
