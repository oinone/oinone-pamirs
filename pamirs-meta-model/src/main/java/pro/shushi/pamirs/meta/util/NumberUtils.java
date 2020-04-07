package pro.shushi.pamirs.meta.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class NumberUtils {
	private static Logger logger = LoggerFactory.getLogger(NumberUtils.class);

	// 默认除法运算精度
	private static final int DEF_DIV_SCALE = 10;

	private NumberUtils() {
	}

	/**
	 * 获取Integer对象的数值
	 * @param value Integer对象
	 * @return value不为空返回value，否则返回0
	 */
	public static Integer valueOf(Integer value) {
		return value != null ? value : 0;
	}

	/**
	 * 获取Long对象的数值
	 * @param value Long对象
	 * @return value不为空返回value，否则返回0
	 */
	public static Long valueOf(Long value) {
		return value != null ? value : 0L;
	}

	/**
	 * 获取Double对象的数值
	 * @param value Double对象
	 * @return value不为空返回value，否则返回0
	 */
	public static Double valueOf(Double value) {
		return value != null ? value : 0D;
	}

	/**
	 * 获取Float对象的数值
	 * @param value Float对象
	 * @return value不为空返回value，否则返回0
	 */
	public static Float valueOf(Float value) {
		return value != null ? value : 0F;
	}

	/**
	 * 获取BigDecimal对象的数值
	 * @param value BigDecimal对象
	 * @return value不为空返回value，否则返回0
	 */
	public static BigDecimal valueOf(BigDecimal value) {
		return value != null ? value : new BigDecimal(0);
	}

	/**
	 * 将字符串转换为Integer类型对象
	 * @param str 数字字符串
	 * @return 成功返回字符串对应的Integer对象，失败返回null
	 */
	public static Integer intValue(String str) {
		if (StringUtils.isNotBlank(str)) {
			try {
				return Integer.parseInt(str);
			} catch (NumberFormatException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	/**
	 * 将字符串转换为Long类型对象
	 * @param str 数字字符串
	 * @return 成功返回字符串对应的Long对象，失败返回null
	 */
	public static Long longValue(String str) {
		if (StringUtils.isNotBlank(str)) {
			try {
				return Long.parseLong(str);
			} catch (NumberFormatException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	/**
	 * 将字符串转换为Double类型对象
	 * @param str 数字字符串
	 * @return 成功返回字符串对应的Double对象，失败返回null
	 */
	public static Double doubleValue(String str) {
		if (StringUtils.isNotBlank(str)) {
			try {
				return Double.parseDouble(str);
			} catch (NumberFormatException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	/**
	 * 将字符串转换为Float类型对象
	 * @param str 数字字符串
	 * @return 成功返回字符串对应的Float对象，失败返回null
	 */
	public static Float floatValue(String str) {
		if (StringUtils.isNotBlank(str)) {
			try {
				return Float.parseFloat(str);
			} catch (NumberFormatException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	/**
	 * 判断两个数字是否相等
	 * @param value1 Integer对象1
	 * @param value2 Integer对象2
	 * @return 相等返回true，否则返回false
	 */
	public static boolean isEqual(Integer value1, Integer value2) {
		return value1 != null && value2 != null && value1.equals(value2);
	}

	public static boolean notEqual(Integer value1, Integer value2) {
		return !isEqual(value1, value2);
	}

	/**
	 * 判断两个数字是否相等
	 * @param value1 Long对象1
	 * @param value2 Long对象2
	 * @return 相等返回true，否则返回false
	 */
	public static boolean isEqual(Long value1, Long value2) {
		return value1 != null && value2 != null && value1.equals(value2);
	}

	public static boolean notEqual(Long value1, Long value2) {
		return !isEqual(value1, value2);
	}

	/**
	 * 判断两个数字是否相等
	 * @param value1 Double对象1
	 * @param value2 Double对象2
	 * @return 相等返回true，否则返回false
	 */
	public static boolean isEqual(Double value1, Double value2) {
		return value1 != null && value2 != null && value1.equals(value2);
	}

	public static boolean notEqual(Double value1, Double value2) {
		return !isEqual(value1, value2);
	}

	/**
	 * 判断两个数字是否相等
	 * @param value1 BigDecimal对象1
	 * @param value2 Number对象2
	 * @return 相等返回true，否则返回false
	 */
	public static boolean isEqual(BigDecimal value1, Number value2) {
		return value1 != null && value2 != null && value1.compareTo(new BigDecimal(value2.toString())) == 0;
	}

	public static boolean notEqual(BigDecimal value1, Number value2) {
		return !isEqual(value1, value2);
	}

	/**
	 * 获取min到max的一个随机整数x，x>=min && x<=max
	 * @param min 随机数最小值
	 * @param max 随机数最大值
	 * @return 随机整数
	 */
	public static int random(int min, int max) {
		return (int) ((max - min + 1) * Math.random()) + min;
	}

	public static double random(double min, double max) {
		return (max - min) * Math.random() + min;
	}

	public static long random(long min, long max) {
		return (long) ((max - min + 1) * Math.random()) + min;
	}

	public static Integer parseInt(String s) {
		if (s != null && s.length() > 0) {
			try {
				return Integer.parseInt(s);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public static Long parseLong(String s) {
		if (s != null && s.length() > 0) {
			try {
				return Long.parseLong(s);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public static Double parseDouble(String s) {
		if (s != null && s.length() > 0) {
			try {
				return Double.parseDouble(s);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public static Float parseFloat(String s) {
		if (s != null && s.length() > 0) {
			try {
				return Float.parseFloat(s);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public static BigDecimal parseBigDecimal(String s) {
		if (s != null && s.length() > 0) {
			try {
				return new BigDecimal(s);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	/**
	 * 获取一个列表（或数组）指定数量的随机索引
	 * @param size 列表（或数组）的大小
	 * @param indexCount 需要生产的随机索引的数量
	 */
	public static List<Integer> getRandomIndexList(int size, int indexCount) {
		List<Integer> indexList = new LinkedList<>();
		List<Integer> allIndexList = new LinkedList<>();
		for (int i=0;i<size;i++) {
			allIndexList.add(i);
		}

		for (int i=0;i<indexCount;i++) {
			int allIndexSize = allIndexList.size();
			if (allIndexSize == 0) {
				break;
			}
			indexList.add(allIndexList.remove(random(0, allIndexSize-1)));
		}
		return indexList;
	}

	/**
	 * 从一个数字的数组中根据每个数字所占全部数字的比例实现按比例选中某一项所在的数组下标并返回(一定会返回一个正确的数组下标)
	 * @param chances 多个数字的数组
	 * @return 选中的数组下标
	 */
	public static int getChanceIndex(double[] chances) {
		return getChanceIndex(0, chances);
	}

	/**
	 * 从一个数字的数组中根据每个数字所占全部数字的比例实现按比例选中某一项所在的数组下标并返回(没有击中任何数字则返回-1)
	 * @param total 概率总和，如果total<=0，表示取数组中全部数字的总和<br/>
	 *              不指定total，则一定会返回一个数组下标<br/>
	 *              指定total，则如果没有击中任何数字则返回-1
	 * @param chances 多个数字的数组
	 * @return 选中的数组下标
	 */
	public static int getChanceIndex(int total, double[] chances) {
		//中奖概率精度在0.01 小于0.01的概率将被忽略
		int temp = total;
		if (temp <= 0) {
			for (int i = 0; i < chances.length; i++) {
				temp += chances[i] * 100;
			}
		} else {
			temp *= 100;
		}

		Random random = new Random();
		for (int i = 0; i < chances.length; i++) {
			double chance = chances[i] * 100;
			if(random.nextInt(temp) < chance) {//击中
				return i;
			} else {
				temp -= chance;
			}
		}
		return -1;//impossible
	}

	/**
	 * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精 确的浮点数运算，包括加减乘除和四舍五入。
	 */

	/**
	 * 提供精确的加法运算。
	 *
	 * @param value1
	 *            被加数
	 * @param value2
	 *            加数
	 * @return 两个参数的和
	 */
	public static double add(double value1, double value2) {
		BigDecimal b1 = new BigDecimal(Double.toString(value1));
		BigDecimal b2 = new BigDecimal(Double.toString(value2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供精确的加法运算。
	 *
	 * @param value1
	 *            被加数
	 * @param value2
	 *            加数
	 * @return 两个参数的和
	 */
	public static BigDecimal add(BigDecimal value1, BigDecimal value2) {
		return valueOf(value1).add(valueOf(value2));
	}

	/**
	 * 提供精确的加法运算。
	 *
	 * @param values
	 *            加数
	 * @return 所有参数的和
	 */
	public static double addAll(double...values) {
		double total = 0;
		if (values.length > 0) {
			for (double value : values) {
				total = add(total, value);
			}
		}
		return total;
	}

	/**
	 * 提供精确的加法运算。
	 *
	 * @param values
	 *            加数
	 * @return 所有参数的和
	 */
	public static BigDecimal addAll(BigDecimal...values) {
		BigDecimal total = new BigDecimal(0);
		if (values.length > 0) {
			for (BigDecimal value : values) {
				total = add(total, value);
			}
		}
		return total;
	}

	/**
	 * 提供精确的减法运算。
	 *
	 * @param value1
	 *            被减数
	 * @param value2
	 *            减数
	 * @return 两个参数的差
	 */

	public static double subtract(Double value1, Double value2) {
		BigDecimal b1 = new BigDecimal(Double.toString(valueOf(value1)));
		BigDecimal b2 = new BigDecimal(Double.toString(valueOf(value2)));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确的减法运算。
	 *
	 * @param value1
	 *            被减数
	 * @param value2
	 *            减数
	 * @return 两个参数的差
	 */
	public static BigDecimal subtract(BigDecimal value1, BigDecimal value2) {
		return valueOf(value1).subtract(valueOf(value2));
	}

	/**
	 * 提供精确的乘法运算。
	 *
	 * @param value1
	 *            被乘数
	 * @param value2
	 *            乘数
	 * @return 两个参数的积
	 */

	public static double multiply(double value1, double value2) {
		BigDecimal b1 = new BigDecimal(Double.toString(value1));
		BigDecimal b2 = new BigDecimal(Double.toString(value2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算。由scale参数指 定精度，对结果四舍五入
	 *
	 * @param value1
	 *            被乘数
	 * @param value2
	 *            乘数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的积
	 */

	public static double multiply(double value1, double value2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(value1));
		BigDecimal b2 = new BigDecimal(Double.toString(value2));
		return round(b1.multiply(b2).doubleValue(), scale);
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 *
	 * @param value1
	 *            被除数
	 * @param value2
	 *            除数
	 * @return 两个参数的商
	 */

	public static double divide(double value1, double value2) {
		return divide(value1, value2, DEF_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 *
	 * @param value1
	 *            被除数
	 * @param value2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */

	public static double divide(double value1, double value2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(value1));
		BigDecimal b2 = new BigDecimal(Double.toString(value2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 提供精确的加法运算。
	 *
	 * @param value1
	 *            被加数
	 * @param value2
	 *            加数
	 * @return 两个参数的和
	 */
	public static float add(float value1, float value2) {
		BigDecimal b1 = new BigDecimal(Float.toString(value1));
		BigDecimal b2 = new BigDecimal(Float.toString(value2));
		return b1.add(b2).floatValue();
	}

	/**
	 * 提供精确的减法运算。
	 *
	 * @param value1
	 *            被减数
	 * @param value2
	 *            减数
	 * @return 两个参数的差
	 */

	public static float subtract(float value1, float value2) {
		BigDecimal b1 = new BigDecimal(Float.toString(value1));
		BigDecimal b2 = new BigDecimal(Float.toString(value2));
		return b1.subtract(b2).floatValue();
	}

	/**
	 * 提供精确的乘法运算。
	 *
	 * @param value1
	 *            被乘数
	 * @param value2
	 *            乘数
	 * @return 两个参数的积
	 */

	public static float multiply(float value1, float value2) {
		BigDecimal b1 = new BigDecimal(Float.toString(value1));
		BigDecimal b2 = new BigDecimal(Float.toString(value2));
		return b1.multiply(b2).floatValue();
	}

	/**
	 * 提供精确的乘法运算。由scale参数指 定精度，对结果四舍五入
	 *
	 * @param value1
	 *            被乘数
	 * @param value2
	 *            乘数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的积
	 */

	public static float multiply(float value1, float value2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Float.toString(value1));
		BigDecimal b2 = new BigDecimal(Float.toString(value2));
		return round(b1.multiply(b2).floatValue(), scale);
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 *
	 * @param value1
	 *            被除数
	 * @param value2
	 *            除数
	 * @return 两个参数的商
	 */

	public static float divide(float value1, float value2) {
		return divide(value1, value2, DEF_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 *
	 * @param value1
	 *            被除数
	 * @param value2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */

	public static float divide(float value1, float value2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Float.toString(value1));
		BigDecimal b2 = new BigDecimal(Float.toString(value2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).floatValue();
	}


	/**
	 * 提供精确的小数位四舍五入处理。
	 *
	 * @param value
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double value, int scale) {
		return rounding(value, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 *
	 * @param value
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static float round(float value, int scale) {
		return rounding(value, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 提供精确的小数位"入"处理。
	 *
	 * @param value
	 *            需要"入"的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return "入"后的结果
	 */
	public static double roundUp(double value, int scale) {
		return rounding(value, scale, BigDecimal.ROUND_UP);
	}

	/**
	 * 提供精确的小数位"入"处理。
	 *
	 * @param value
	 *            需要"入"的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return "入"后的结果
	 */
	public static float roundUp(float value, int scale) {
		return rounding(value, scale, BigDecimal.ROUND_UP);
	}

	/**
	 * 提供精确的小数位"舍"处理。
	 *
	 * @param value
	 *            需要"舍"的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return "舍"后的结果
	 */
	public static double roundDown(double value, int scale) {
		return rounding(value, scale, BigDecimal.ROUND_DOWN);
	}

	/**
	 * 提供精确的小数位"舍"处理。
	 *
	 * @param value
	 *            需要"舍"的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return "舍"后的结果
	 */
	public static float roundDown(float value, int scale) {
		return rounding(value, scale, BigDecimal.ROUND_DOWN);
	}

	/**
	 * 提供精确的小数位处理。
	 *
	 * @param value
	 *            需要处理的数字
	 * @param scale
	 *            小数点后保留几位
	 * @param roundingMode
	 *            处理模式
	 * @return "处理后的结果
	 */
	private static double rounding(double value, int scale, int roundingMode) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(value));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, roundingMode).doubleValue();
	}

	/**
	 * 提供精确的小数位处理。
	 *
	 * @param value
	 *            需要处理的数字
	 * @param scale
	 *            小数点后保留几位
	 * @param roundingMode
	 *            处理模式
	 * @return "处理后的结果
	 */
	private static float rounding(float value, int scale, int roundingMode) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Float.toString(value));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, roundingMode).floatValue();
	}
}
