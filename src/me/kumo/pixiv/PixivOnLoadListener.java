package me.kumo.pixiv;

import org.jetbrains.annotations.Nullable;
import pixivj.model.AuthResult;

import java.util.EventListener;

public interface PixivOnLoadListener extends EventListener {
    void done(@Nullable AuthResult result);
}
