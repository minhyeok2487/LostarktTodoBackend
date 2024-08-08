package lostark.todo.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlobalMethod {

    public static void compareLists(List<Long> list1, List<Long> list2, String message) {
        Set<Long> set1 = new HashSet<>(list1);
        Set<Long> set2 = new HashSet<>(list2);

        if (!set1.equals(set2)) {
            throw new IllegalArgumentException(message);
        }
    }
}
