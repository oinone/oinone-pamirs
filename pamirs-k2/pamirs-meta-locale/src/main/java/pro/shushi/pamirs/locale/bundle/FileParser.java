package pro.shushi.pamirs.locale.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * File Parser
 *
 * @author Adamancy Zhang at 15:29 on 2026-03-13
 */
interface FileParser {

    String[] getFileExtensions();

    Map<String, Object> parse(InputStream inputStream) throws IOException;

}
