package me.kumo.io.pixiv;

import com.github.hanshsieh.pixivj.model.AddBookmark;
import com.github.hanshsieh.pixivj.model.Restrict;

public class DeleteBookmark extends AddBookmark {
    public DeleteBookmark(long id) {
        setIllustId(id);
        setRestrict(Restrict.PUBLIC);
    }
}
