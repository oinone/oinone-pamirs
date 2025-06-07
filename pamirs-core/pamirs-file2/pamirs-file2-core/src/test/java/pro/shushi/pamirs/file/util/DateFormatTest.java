package pro.shushi.pamirs.file.util;

import com.alibaba.excel.util.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.core.common.DateHelper;

import java.util.Date;

/**
 * @author Adamancy Zhang at 12:15 on 2024-07-01
 */
public class DateFormatTest {

    @Test
    public void test() {
        Date date1 = DateUtils.getJavaDate(0.0f, false);
        Date date2 = DateUtil.getJavaDate(0.0f, false);
        System.out.println(DateHelper.format(date1));
        System.out.println(DateHelper.format(date2));
    }
}
