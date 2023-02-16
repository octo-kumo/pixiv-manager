package me.kumo.ui;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.ui.control.ControlPane;
import me.kumo.ui.gallery.Gallery;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class ControlPanel extends JPanel implements Refreshable<Illustration[]>, Supplier<Illustration[]> {
    private final ControlPane filter;
    private final Gallery gallery;
    private final Illustration[] illustrations;

    public ControlPanel(Illustration[] illustrations) {
        super(new BorderLayout());
        this.illustrations = illustrations;
        setPreferredSize(new Dimension(1280, 720));
        add(filter = new ControlPane(this, this), BorderLayout.NORTH);
        add(gallery = new Gallery());
        refresh(illustrations);
    }

    @Override
    public void refresh(Illustration[] illustrations) {
        filter.refresh(illustrations);
        gallery.refresh(illustrations);
    }

    @Override
    public Illustration[] get() {
        return illustrations;
    }
}
