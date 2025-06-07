import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TestDateFormat
 *
 * @author yakir <a href="yakirchen.github.io">yakirchen.github.io</a> on 2019/08/01 14:35.
 */
public class TestDateFormat {

    public static void main(String[] args) {

        LocalDateTime now = LocalDateTime.now();

        String date = "";

        date = DateTimeFormatter.ofPattern("yy").format(now);
        System.out.println("yy " + date);

        date = DateTimeFormatter.ofPattern("yyyy").format(now);
        System.out.println("yyyy " + date);

        date = DateTimeFormatter.ofPattern("MM").format(now);
        System.out.println("MM " + date);

        date = DateTimeFormatter.ofPattern("dd").format(now);
        System.out.println("dd " + date);

        date = DateTimeFormatter.ofPattern("yyyy_dd").format(now);
        System.out.println("yyyy_dd " + date);

        date = DateTimeFormatter.ofPattern("yyyy_F").format(now);
        System.out.println("yyyy_F " + date);

        date = DateTimeFormatter.ofPattern("E").format(now);
        System.out.println("E " + date);

        date = DateTimeFormatter.ofPattern("HH").format(now);
        System.out.println("HH " + date);

        date = DateTimeFormatter.ofPattern("hh").format(now);
        System.out.println("hh " + date);

        date = DateTimeFormatter.ofPattern("mm").format(now);
        System.out.println("mm " + date);

        date = DateTimeFormatter.ofPattern("ss").format(now);
        System.out.println("ss " + date);
    }
}
