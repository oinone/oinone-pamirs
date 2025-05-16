package pro.shushi.pamirs.file.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Adamancy Zhang at 12:19 on 2024-06-03
 */
public class RequestGetUrlTest {

    private static final String request = "http://127.0.0.1:8091/pamirs/file";

    public static void main(String[] args) throws UnsupportedEncodingException {
        String query = "query {\n" +
                "\texcelWorkbookDefinitionQuery {\n" +
                "\t\tdownloadImportTemplate(\n" +
                "\t\t\tdata: {\n" +
                "\t\t\t\tmodel: \"file.ExcelWorkbookDefinition\"\n" +
                "\t\t\t\tname: \"excelLocationTemplate\"\n" +
                "\t\t\t}\n" +
                "\t\t) {\n" +
                "\t\t\tname\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}\n";
        String variables = "{\n" +
                "\t\"lang\": \"en-US\"\n" +
                "}";
        System.out.printf("%s?query=%s&variables=%s%n", request, encode(query), encode(variables));
    }

    private static String encode(String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s.replaceAll("\\s", ""), StandardCharsets.UTF_8.name());
    }
}
