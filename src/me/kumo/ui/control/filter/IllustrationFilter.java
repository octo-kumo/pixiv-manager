package me.kumo.ui.control.filter;

import pixivj.model.Illustration;

import java.util.stream.Stream;

public interface IllustrationFilter {
    void reset();

    boolean test(Illustration illustration);

    default Stream<Illustration> filter(Stream<Illustration> illustrations) {
        return illustrations.filter(this::test);
    }
}
