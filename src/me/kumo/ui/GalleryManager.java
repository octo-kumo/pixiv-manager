package me.kumo.ui;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.ui.control.ControlPane;
import me.kumo.ui.gallery.Gallery;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class GalleryManager extends JPanel implements Refreshable<List<Illustration>>, Supplier<ArrayList<Illustration>> {
    protected final ControlPane filter;
    protected final Gallery gallery;

    protected ArrayList<Illustration> illustrations;

    public GalleryManager() {
        super(new BorderLayout());
        add(filter = new ControlPane(this, this), BorderLayout.NORTH);
        add(gallery = new Gallery(), BorderLayout.CENTER);
    }

    public void refresh(Illustration illustration) {
        illustrations.replaceAll(i -> Objects.equals(i.getId(), illustration.getId()) ? illustration : i);
        gallery.refresh(illustration);
    }

    @Override
    public void refresh(List<Illustration> illustrations) {
        filter.refresh(illustrations);
        gallery.refresh(illustrations);
    }

    @Override
    public ArrayList<Illustration> get() {
        return illustrations;
    }

    public ArrayList<Illustration> getIllustrations() {
        return illustrations;
    }

    public void setIllustrations(ArrayList<Illustration> illustrations) {
        this.illustrations = illustrations;
        filter.applyAll();
    }

    public void tapGallery() {
        gallery.tapGallery();
    }

    public void append(List<Illustration> illustrations) {
        if (this.illustrations == null) this.illustrations = new ArrayList<>();
        this.illustrations.addAll(illustrations);
        filter.refresh(this.illustrations);
        gallery.append(filter.filterAndSort(illustrations));
    }
}
