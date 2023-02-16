package me.kumo.io;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.hanshsieh.pixivj.model.Tag;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Process {
    public static String[] getTools(List<Illustration> illustrations) {
        return getTools(illustrations.stream()).toArray(String[]::new);
    }

    public static Tag[] getTags(List<Illustration> illustrations) {
        return getTags(illustrations.stream()).toArray(Tag[]::new);
    }

    public static Stream<Tag> getTags(Stream<Illustration> illustrations) {
        HashMap<Tag, Long> collect = illustrations.flatMap(i -> i.getTags().stream()).collect(Collectors.groupingBy(Function.identity(), HashMap<Tag, Long>::new, Collectors.counting()));
        List<Map.Entry<Tag, Long>> list = new LinkedList<>(collect.entrySet());
        list.sort(Map.Entry.comparingByValue((a, b) -> -Long.compare(a, b)));
        return list.stream().map(Map.Entry::getKey).filter(Objects::nonNull);
    }

    public static Stream<String> getTools(Stream<Illustration> illustrations) {
        HashMap<String, Long> collect = illustrations.flatMap(i -> i.getTools().stream()).collect(Collectors.groupingBy(Function.identity(), HashMap<String, Long>::new, Collectors.counting()));
        List<Map.Entry<String, Long>> list = new LinkedList<>(collect.entrySet());
        list.sort(Map.Entry.comparingByValue((a, b) -> -Long.compare(a, b)));
        return list.stream().map(Map.Entry::getKey).filter(Objects::nonNull);
    }

    public static <T> List<T> get(List<Illustration> illustrations, Function<Illustration, T> converter) {
        return illustrations.stream().map(converter).distinct().toList();
    }
}
