package me.kumo.ui.gallery;

public class RankedItem extends GalleryItem {
    public RankedItem() {
        remove(1);
        add(info = new RankedInfo(), 1);
    }

    public void setRank(int r) {
        ((RankedInfo) info).setRank(r);
    }
}
