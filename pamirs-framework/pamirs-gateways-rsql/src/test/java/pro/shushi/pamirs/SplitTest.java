package pro.shushi.pamirs;

import org.junit.jupiter.api.Test;

/**
 * @author Adamancy Zhang at 21:05 on 2026-08-31
 */
public class SplitTest {

    protected static final String[] PRECISE_SEARCH_CHARACTERS = new String[]{"_", "%"};

    protected static final String[] FUZZY_SEARCH_CHARACTERS = new String[]{};

    @Test
    public void test1() {
        assert "cs''5".equals(SplitTest.serializableValue("cs\\'5", PRECISE_SEARCH_CHARACTERS));
        assert "''".equals(SplitTest.serializableValue("\\'", PRECISE_SEARCH_CHARACTERS));
        assert "''''".equals(SplitTest.serializableValue("\\'\\'", PRECISE_SEARCH_CHARACTERS));

        assert "cs_5".equals(SplitTest.serializableValue("cs\\_5", PRECISE_SEARCH_CHARACTERS));
        assert "_".equals(SplitTest.serializableValue("\\_", PRECISE_SEARCH_CHARACTERS));
        assert "__".equals(SplitTest.serializableValue("\\_\\_", PRECISE_SEARCH_CHARACTERS));

        assert "cs%5".equals(SplitTest.serializableValue("cs\\%5", PRECISE_SEARCH_CHARACTERS));
        assert "%".equals(SplitTest.serializableValue("\\%", PRECISE_SEARCH_CHARACTERS));
        assert "%%".equals(SplitTest.serializableValue("\\%\\%", PRECISE_SEARCH_CHARACTERS));
    }

    @Test
    public void testBoundary() {
        assert "''cs".equals(SplitTest.serializableValue("\\'cs", PRECISE_SEARCH_CHARACTERS));
        assert "cs''".equals(SplitTest.serializableValue("cs\\'", PRECISE_SEARCH_CHARACTERS));
        assert "cs_".equals(SplitTest.serializableValue("cs\\_", PRECISE_SEARCH_CHARACTERS));

        assert "cs''5_6%7".equals(SplitTest.serializableValue("cs\\'5\\_6\\%7", PRECISE_SEARCH_CHARACTERS));

        assert "cs_5%6".equals(SplitTest.serializableValue("cs_5%6", PRECISE_SEARCH_CHARACTERS));
        assert "".equals(SplitTest.serializableValue("", PRECISE_SEARCH_CHARACTERS));
    }

    @Test
    public void testFuzzy() {
        assert "cs''5".equals(SplitTest.serializableValue("cs\\'5", FUZZY_SEARCH_CHARACTERS));
        assert "cs\\_5".equals(SplitTest.serializableValue("cs\\_5", FUZZY_SEARCH_CHARACTERS));
        // 模糊搜索的转义清单里没有 %：\% 原样保留
        assert "cs\\%5".equals(SplitTest.serializableValue("cs\\%5", FUZZY_SEARCH_CHARACTERS));
    }

    private static String serializableValue(String value, String[] characters) {
        value = String.join("''", value.split("\\\\'", -1));
        for (String character : characters) {
            value = String.join(String.format("%s", character), value.split(String.format("\\\\%s", character), -1));
        }
        return value;
    }
}
