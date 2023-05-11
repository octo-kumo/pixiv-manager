package me.kumo.pixiv;

import com.github.hanshsieh.pixivj.model.DeleteBookmark;
import com.github.hanshsieh.pixivj.model.IllustDetail;
import com.github.hanshsieh.pixivj.model.Illustration;

import javax.swing.*;
import java.util.function.Consumer;

public class BookmarkWorker extends SwingWorker<Boolean, Illustration> {
    private Illustration illustration;
    private final Consumer<Boolean> listener;

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
