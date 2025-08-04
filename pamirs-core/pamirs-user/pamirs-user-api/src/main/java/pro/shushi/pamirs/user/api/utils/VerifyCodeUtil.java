package pro.shushi.pamirs.user.api.utils;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class VerifyCodeUtil {

    private static final String[] fontNames = {"宋体", "华文楷体", "黑体", "Georgia", "微软雅黑", "楷体_GB2312"};


    public static String drawImage(HttpServletResponse response) {
        String verifyCode = randomChar() + "";
        drawImage(response, verifyCode);
        return verifyCode;
    }

    public static String drawImage(HttpServletResponse response, String verifyCode) {
        int width = 50;
        int height = 25;

        //创建图片缓冲区
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D g = bi.createGraphics();

        //设置背景颜色
        g.setBackground(new Color(255, 255, 255));
        g.clearRect(0, 0, width, height);

        char[] chars = verifyCode.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            float x = i * 1.0F * width / 4;   //定义字符的x坐标
            g.setFont(randomFont());           //设置字体，随机
            g.setColor(randomColor());         //设置颜色，随机
            g.drawString(chars[i] + "", x, height - 5);
        }

        //定义干扰线
        //定义干扰线的数量（3-5条）int num = random.nextInt(max)%(max-min+1) + min;
        Random random = ThreadLocalRandom.current();
        int num = random.nextInt(5) % 3 + 3;
        Graphics2D graphics = (Graphics2D) bi.getGraphics();
        for (int i = 0; i < num; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            graphics.setColor(randomColor());
            graphics.drawLine(x1, y1, x2, y2);
        }
        // 释放图形上下文
        g.dispose();
        try {
            ImageIO.write(bi, "jpg", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return verifyCode;//为了方便取值，直接返回code，
    }

    //随机字体
    private static Font randomFont() {
        Random random = ThreadLocalRandom.current();
        int index = random.nextInt(fontNames.length);
        String fontName = fontNames[index];
        int style = random.nextInt(4);         //随机获取4种字体的样式
        int size = random.nextInt(20) % 6 + 15;    //随机获取字体的大小(10-20之间的值)
        return new Font(fontName, style, size);
    }

    //随机颜色
    private static Color randomColor() {
        Random random = ThreadLocalRandom.current();
        int r = random.nextInt(225);
        int g = random.nextInt(225);
        int b = random.nextInt(225);
        return new Color(r, g, b);
    }


    //随机字符
    public static char randomChar() {
        //A-Z,a-z,0-9,可剔除一些难辨认的字母与数字
        String str = "0123456789ABCdefghiDEFGHIJopPQRVWXYZabcjklSTUmnqrstKLMNOvuwxyz";
        return str.charAt(ThreadLocalRandom.current().nextInt(str.length()));
    }

    public static String randomStr(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(randomChar());
        }
        return stringBuilder.toString();
    }
}