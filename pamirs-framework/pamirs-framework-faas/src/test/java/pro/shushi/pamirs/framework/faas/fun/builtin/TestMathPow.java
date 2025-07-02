package pro.shushi.pamirs.framework.faas.fun.builtin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * TestMathPow
 *
 * @author yakir on 2025/06/11 14:09.
 */
public class TestMathPow {

    @Test
    public void zeroPow() {

        double result = Math.pow(0, 0);
        Assertions.assertEquals(1.0d, result);
    }

    @Test
    public void nanPow() {
        double result = Math.pow(-2, 0.5);
        Assertions.assertEquals(Double.NaN, result);
    }

    @Test
    public void testPow() {
        System.out.println(MathFunctions.pow(1, 1));
        System.out.println(MathFunctions.pow(1, 0.5));
        System.out.println(MathFunctions.pow(-1, 1));
        System.out.println(MathFunctions.pow(-1, 0.5));
        System.out.println(MathFunctions.pow(1, -1));
        System.out.println(MathFunctions.pow(1, -0.5));
        System.out.println(MathFunctions.pow(-1, -1));
        System.out.println(MathFunctions.pow(-1, -0.5));

        System.out.println(MathFunctions.pow(1f, 1f));
        System.out.println(MathFunctions.pow(1f, -1f));
        System.out.println(MathFunctions.pow(-1f, 1f));
        System.out.println(MathFunctions.pow(-1f, -1f));

        System.out.println(MathFunctions.pow(1d, 1d));
        System.out.println(MathFunctions.pow(1d, -1d));
        System.out.println(MathFunctions.pow(-1d, 1d));
        System.out.println(MathFunctions.pow(-1d, -1d));

        Assertions.assertEquals(1, MathFunctions.pow(-1, 0));
        Assertions.assertEquals(1, MathFunctions.pow(1, 0));
        Assertions.assertEquals(1, MathFunctions.pow(1.1, 0));
        Assertions.assertEquals(1, MathFunctions.pow(-1.1, 0));
        Assertions.assertEquals(1, MathFunctions.pow(-0, 0));
        Assertions.assertEquals(1, MathFunctions.pow(0, 0));
        Assertions.assertEquals(1, MathFunctions.pow(-1, 0.0));
        Assertions.assertEquals(1, MathFunctions.pow(1, 0.000));
        Assertions.assertEquals(1, MathFunctions.pow(1.1, 0.0000));
        Assertions.assertEquals(1, MathFunctions.pow(-1.1, 0.00000));
        Assertions.assertEquals(1, MathFunctions.pow(-0, 0.000000));
        Assertions.assertEquals(1, MathFunctions.pow(0, 0.0000000));

        System.out.println(MathFunctions.pow(new BigInteger("1"), new BigInteger("1")));
        System.out.println(MathFunctions.pow(new BigInteger("-1"), new BigInteger("1")));
        System.out.println(MathFunctions.pow(new BigInteger("1"), new BigInteger("-1")));
        System.out.println(MathFunctions.pow(new BigInteger("-1"), new BigInteger("-1")));

        System.out.println(MathFunctions.pow(new BigDecimal("1"), new BigDecimal("1")));
        System.out.println(MathFunctions.pow(new BigDecimal("-1"), new BigDecimal("1")));
        System.out.println(MathFunctions.pow(new BigDecimal("1"), new BigDecimal("-1")));
        System.out.println(MathFunctions.pow(new BigDecimal("-1"), new BigDecimal("-1")));
    }
}
