package me.kumo.ui;

import com.github.hanshsieh.pixivj.exception.APIException;
import com.github.hanshsieh.pixivj.exception.PixivException;
import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.hanshsieh.pixivj.model.SearchedIllusts;
import com.github.hanshsieh.pixivj.model.User;
import com.github.hanshsieh.pixivj.util.JsonUtils;
import com.github.weisj.darklaf.components.loading.LoadingIndicator;
import me.kumo.io.pixiv.BookmarkFilter;
import me.kumo.io.pixiv.Pixiv;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class BookmarkManager extends ControlPanel {

    private static final String PATH = "bookmarks.json";
    private final Pixiv pixiv;
    private final ArrayList<Illustration> bookmarks;
    private LoadingIndicator indicator;
    private User user;

    public BookmarkManager(Pixiv pixiv) {
        this.pixiv = pixiv;
        add(new JPanel(new FlowLayout(FlowLayout.LEADING)) {{
            indicator = new LoadingIndicator("Loading bookmarks");
            add(indicator);
        }}, BorderLayout.SOUTH);
        bookmarks = new ArrayList<>();
        setIllustrations(bookmarks);
        pixiv.onLoad(user -> {
            this.user = user;
            System.out.println("Logged in as " + user.getName());
            indicator.setRunning(true);
            new SwingWorker<ArrayList<Illustration>, Void>() {
                @Override
                protected ArrayList<Illustration> doInBackground() throws PixivException, IOException {
                    return loadAllBookmarks(list -> {
                        indicator.setText(list.size() + " loaded");
                        setIllustrations(list);
                    });
                }

                @Override
                protected void done() {
                    super.done();
                    indicator.setEnabled(false);
                }
            }.execute();
        });
    }

    public ArrayList<Illustration> loadAllBookmarks(Consumer<ArrayList<Illustration>> updater) throws PixivException, IOException {
        bookmarks.clear();
        try {
            bookmarks.addAll(List.of(JsonUtils.GSON.fromJson(new FileReader(PATH, StandardCharsets.UTF_8), Illustration[].class)));
        } catch (FileNotFoundException ignored) {
        }
        AtomicReference<String> nextUrl = new AtomicReference<>(null);
        long stopAt = bookmarks.size() > 0 ? bookmarks.get(0).getId() : -1;
        int counter = 0;
        do {
            List<Illustration> bunch = loadBookmarks(nextUrl, stopAt);
            bookmarks.addAll(counter, bunch);
            counter += bunch.size();
            SwingUtilities.invokeLater(() -> updater.accept(bookmarks));
        } while (nextUrl.get() != null);
        FileWriter writer = new FileWriter(PATH, StandardCharsets.UTF_8);
        JsonUtils.GSON.toJson(bookmarks, writer);
        writer.flush();
        writer.close();
        return bookmarks;
    }

    public List<Illustration> loadBookmarks(AtomicReference<String> nextURL, Long stopAt) throws PixivException, IOException {
        SearchedIllusts searchedIllusts;
        try {
            if (nextURL.get() == null) searchedIllusts = pixiv.bookmarks(new BookmarkFilter(user.getId()));
            else
                searchedIllusts = pixiv.requestSender.send(pixiv.createApiReqBuilder().url(nextURL.get()).get().build(), SearchedIllusts.class);
        } catch (APIException exception) {
            exception.printStackTrace();
            try {
                indicator.setRunning(false);
                Thread.sleep(10000);
                indicator.setRunning(true);
            } catch (InterruptedException ignored) {
            }
            return loadBookmarks(nextURL, stopAt);
        }
        List<Illustration> illusts = searchedIllusts.getIllusts();
        System.out.println("loaded illusts " + illusts.size() + " :: ..." + illusts.get(0).getId());
        if (illusts.stream().anyMatch(i -> Objects.equals(i.getId(), stopAt))) {
            AtomicInteger position = new AtomicInteger(-1);
            illusts.stream().peek(x -> position.incrementAndGet())
                    .filter(i -> Objects.equals(i.getId(), stopAt))
                    .findFirst();
            return illusts.subList(0, position.get());
        }
        nextURL.set(searchedIllusts.getNextUrl());
        return illusts;
    }
}
