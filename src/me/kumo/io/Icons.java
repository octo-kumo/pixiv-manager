package me.kumo.io;

import com.github.weisj.darklaf.iconset.IconSet;

import javax.swing.*;

public enum Icons {
    empty, down, up, heart, heart_path, eye, pixiv, download;

    public Icon get() {
        return IconSet.iconLoader().getIcon("/icons/" + name() + ".svg", true);
    }
}
