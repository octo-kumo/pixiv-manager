package me.kumo.io.pixiv;

import com.github.hanshsieh.pixivj.model.Illustration;

import java.util.EventListener;
import java.util.function.Consumer;

public interface IllustUpdateListener extends EventListener, Consumer<Illustration> {
}
