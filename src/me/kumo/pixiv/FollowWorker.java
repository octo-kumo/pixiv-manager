package me.kumo.pixiv;

import com.github.hanshsieh.pixivj.model.Author;
import com.github.hanshsieh.pixivj.model.FollowUserRequest;
import com.github.hanshsieh.pixivj.model.Illustration;

import javax.swing.*;
import java.util.function.Consumer;

public class FollowWorker extends SwingWorker<Boolean, Illustration> {
    private final Consumer<Boolean> listener;
    private final Author user;

    public FollowWorker(Author user, Consumer<Boolean> listener) {
        this.user = user;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        try {
            FollowUserRequest request = new FollowUserRequest(user.getId());
            if (user.isFollowed()) Pixiv.getInstance().unfollowUser(request);
            else Pixiv.getInstance().followUser(request);
            user.setFollowed(!user.isFollowed());
        } catch (Exception e) {
            e.printStackTrace();
        }
        listener.accept(user.isFollowed());
        return user.isFollowed();
    }
}
