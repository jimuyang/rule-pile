package muyi.rule.pile.util;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 */
public class CollectionUtil {
    public static boolean isNullOrEmpty(Collection<?> c) {
        return (null == c || c.isEmpty());
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return (null != c && c.size() > 0);
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !(map == null || map.isEmpty());
    }

    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * demo use: list.stream().filter(CollectionUtil.distinctByKey(Person::getId))
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


}
