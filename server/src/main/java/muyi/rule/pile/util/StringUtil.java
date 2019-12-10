package muyi.rule.pile.util;

import java.util.List;
import java.util.function.Function;

public class StringUtil {

    /**
     * 将字符串转换为Long对象
     *
     * @param s 原字符串
     * @return 当符合10进制整数, 并且值不越界时, 返回相应对象, 否则返回null
     */
    public static Long toLong(String s) {
        try {
            return Long.valueOf(s);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 判断一个字串是否为空字串, null或0长度
     *
     * @param s String
     * @return boolean
     */
    public static boolean isEmpty(String s) {
        if (s == null || s.trim().length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断一个字串是否为非空字串, null或0长度
     *
     * @param s
     * @return
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    /**
     * join
     */
    public static String join(String separator, List<?> list) {
        return join(separator, list, Object::toString);
    }

    public static <T> String join(String separator, List<T> list, Function<T, String> mapper) {
        if (list == null) {
            return null;
        } else {
            if (separator == null) {
                separator = "";
            }
            StringBuilder buf = new StringBuilder(list.size() * 16);

            for (int i = 0; i < list.size(); ++i) {
                if (i > 0) {
                    buf.append(separator);
                }
                if (list.get(i) != null) {
                    buf.append(mapper.apply(list.get(i)));
                }
            }
            return buf.toString();
        }
    }
}
