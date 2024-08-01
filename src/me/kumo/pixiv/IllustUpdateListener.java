package me.kumo.pixiv;

import pixivj.model.Illustration;

import java.util.EventListener;
import java.util.function.Consumer;

public interface IllustUpdateListener extends EventListener, Consumer<Illustration> {
}
