package me.kumo.ui.managers;

import me.kumo.components.utils.StartAndStoppable;
import me.kumo.ui.Refreshable;
import me.kumo.ui.control.ControlPane;
import me.kumo.ui.gallery.Gallery;
import pixivj.model.Illustration;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class GalleryManager extends JPanel implements Refreshable<List<Illustration>>, Supplier<ArrayList<Illustration>>, StartAndStoppable {
    public final ControlPane filter;
    public final Gallery gallery;

    protected ArrayList<Illustration> illustrations;

    public GalleryManager() {
        super(new BorderLayout());
        add(filter = new ControlPane(this), BorderLayout.NORTH);
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

    public void setIllustrations(ArrayList<Illustration> illustrations) {
        this.illustrations = illustrations;
        filter.applyAll();
    }

    public void tapGallery() {
        gallery.tapGallery();
    }

    public void append(List<Illustration> illustrations) {
        if (this.illustrations == null) this.illustrations = new ArrayList<>();
        illustrations = illustrations.stream().filter(i -> this.illustrations.stream().noneMatch(I -> I.getId().equals(i.getId()))).toList();
        this.illustrations.addAll(illustrations);
        filter.refresh(this.illustrations);
        gallery.append(filter.filterAndSort(illustrations));
    }

    public Gallery getGallery() {
        return gallery;
    }

    @Override
    public void start() {
        gallery.start();
    }

    @Override
    public void stop() {
        gallery.stop();
    }
}
