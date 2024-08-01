package me.kumo.pixiv;

import pixivj.model.DeleteBookmark;
import pixivj.model.IllustDetail;
import pixivj.model.Illustration;

import javax.swing.*;
import java.util.function.Consumer;

public class BookmarkWorker extends SwingWorker<Boolean, Illustration> {
    private final Consumer<Boolean> listener;
    private Illustration illustration;

    public BookmarkWorker(Illustration illustration, Consumer<Boolean> listener) {
        this.illustration = illustration;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        try {
            DeleteBookmark a = new DeleteBookmark(illustration.getId());
            if (illustration.isBookmarked()) Pixiv.getInstance().removeBookmark(a);
            else Pixiv.getInstance().addBookmark(a);
            IllustDetail illustDetail = Pixiv.getInstance().getIllustDetail(illustration.getId());
            illustration.setBookmarked(illustDetail.getIllust().isBookmarked());
            illustration = illustDetail.getIllust();
        } catch (Exception e) {
            e.printStackTrace();
        }
        listener.accept(illustration.isBookmarked());
        return illustration.isBookmarked();
    }
}
