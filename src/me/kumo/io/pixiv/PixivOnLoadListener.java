package me.kumo.io.pixiv;

import com.github.hanshsieh.pixivj.model.AuthResult;
import org.jetbrains.annotations.Nullable;

import java.util.EventListener;

public interface PixivOnLoadListener extends EventListener {
    void done(@Nullable AuthResult result);
}
