package muyi.rule.pile.util;

/**
 * @author: Yang Fan
 * @date: 2019/3/19
 * @desc:
 */
public class NumberUtil {


    public static boolean nonZero(Number number) {
        return number != null && number.intValue() != 0;
    }

    public static boolean nullOrZero(Number number) {
        return !nonZero(number);
    }

}
