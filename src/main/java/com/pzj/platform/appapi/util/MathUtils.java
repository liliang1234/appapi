package com.pzj.platform.appapi.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MathUtils {

    /**
     * 数值的相加
     * @author fanggang
     * @date 2016年4月12日 下午4:28:08
     * @param addend 被加数
     * @param addends 加数列表
     * @return
     */
    public static Double add(Double addend, Double... addends) {
        return addOrSubtract(true, addend, addends).doubleValue();
    }

    /**
     * 数值的相减
     * @author fanggang
     * @date 2016年4月12日 下午4:28:12
     * @param minuend 被减数
     * @param subtrahends 减数列表
     * @return
     */
    public static Double subtract(Double minuend, Double... subtrahends) {
        return addOrSubtract(false, minuend, subtrahends).doubleValue();
    }

    /**
     * 两数相乘；
     * 保留两位有效数字；
     * 向零方向舍入的舍入模式。注意，此舍入模式始终不会增加计算值的绝对值。
     * 
     * @author fanggang
     * @param m1 乘数
     * @param m2 被乘数
     * @return
     */
    public static Double multiply(Double m1, Double m2) {
        MathContext mc = new MathContext(2, RoundingMode.DOWN);
        return new BigDecimal(Double.toString(m1)).multiply(new BigDecimal(Double.toString(m2)), mc).doubleValue();
    }

    /**
     * 相加或相减
     * @author fanggang
     * @date 2016年4月12日 下午4:30:51
     * @param isAdd 是否做加法
     * @param minuendOrAddend
     * @param ops
     * @return
     */
    private static BigDecimal addOrSubtract(boolean isAdd, Double minuendOrAddend, Double... ops) {
        if (minuendOrAddend == null)
            minuendOrAddend = 0d;
        for (int i = 0; i < ops.length; i++) {
            if (ops[i] == null)
                ops[i] = 0d;
        }

        BigDecimal bdMinuend = new BigDecimal(Double.toString(minuendOrAddend));
        BigDecimal result = bdMinuend;
        for (Double sub : ops) {
            BigDecimal bgSubtrahend = new BigDecimal(Double.toString(sub));
            if (isAdd) {
                result = result.add(bgSubtrahend);
            } else {
                result = result.subtract(bgSubtrahend);
            }
        }

        return result;
    }

    /**
     * 取负值
     * @author fanggang
     * @date 2016年4月8日 下午2:10:49
     * @param value
     * @return
     */
    public static Double negate(Double value) {
        if (value == null) {
            return Double.valueOf("0");
        }
        BigDecimal bd = new BigDecimal(Double.toString(value));
        return bd.negate().doubleValue();
    }

    /**
     * 取负值
     * @author fanggang
     * @date 2016年4月8日 下午2:10:49
     * @param value
     * @return
     */
    public static Integer negate(Integer value) {
        if (value == null || value.intValue() == 0) {
            return 0;
        }
        return -value;
    }

    public static Integer add(Integer v1, Integer v2) {
        if (v1 == null) {
            v1 = 0;
        }
        if (v2 == null) {
            v2 = 0;
        }
        return v1.intValue() + v2.intValue();
    }
}
