package me.kumo.io;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.hanshsieh.pixivj.model.Tag;
import com.github.hanshsieh.pixivj.util.JsonUtils;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Loader {
    public static final String path = "/Users/zy/Documents/GitHub/RandomJavaProjects/pic_autodownload/pixiv.array.json";
    public static final Illustration[] illustrations;
    public static final Tag[] tags;

    static {
        try {
            illustrations = JsonUtils.GSON.fromJson(new FileReader(path, StandardCharsets.UTF_8), Illustration[].class);
            tags = getTags(illustrations);
            System.out.println(Arrays.toString(getTools(illustrations)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static String[] getTools(Illustration[] illustrations) {
        return getTools(Arrays.stream(illustrations)).toArray(String[]::new);
    }

    public static Tag[] getTags(Illustration[] illustrations) {
        return getTags(Arrays.stream(illustrations)).toArray(Tag[]::new);
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
}
