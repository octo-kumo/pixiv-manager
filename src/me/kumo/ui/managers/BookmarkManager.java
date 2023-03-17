package me.kumo.ui.managers;

import com.github.hanshsieh.pixivj.exception.APIException;
import com.github.hanshsieh.pixivj.exception.PixivException;
import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.hanshsieh.pixivj.model.SearchedIllusts;
import com.github.hanshsieh.pixivj.model.User;
import com.github.hanshsieh.pixivj.util.JsonUtils;
import com.github.weisj.darklaf.components.loading.LoadingIndicator;
import me.kumo.io.Icons;
import me.kumo.io.NetIO;
import me.kumo.io.pixiv.Pixiv;
import me.kumo.io.pixiv.V2Filter;
import me.kumo.ui.gallery.GalleryItem;

import javax.swing.*;
import java.awt.*;
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

public class BookmarkManager extends GalleryManager {

    private static final String PATH = "bookmarks.json";
    private final Pixiv pixiv;
    private final ArrayList<Illustration> bookmarks;
    private LoadingIndicator indicator;
    private JLabel countLabel;
    private User user;

    public BookmarkManager(Pixiv pixiv) {
        this.pixiv = pixiv;
        add(new JPanel(new FlowLayout(FlowLayout.LEADING)) {{
            add(indicator = new LoadingIndicator("Loading bookmarks"));
            add(countLabel = new JLabel(""));
            add(new JSeparator(JSeparator.VERTICAL));
            add(new JButton("Download All", Icons.download.get()) {{
                addActionListener(e -> {
                    Timer timer = new Timer(16, null);
                    timer.addActionListener(ev -> {
                        setText("Download All (" + NetIO.tasks() + ")");
                        if (NetIO.tasks() == 0) {
                            setText("Download All");
                            timer.stop();
                        }
                    });
                    timer.start();
                    gallery.forEach(GalleryItem::downloadIfNotExist);
                });
            }});
        }}, BorderLayout.SOUTH);
        bookmarks = new ArrayList<>();
        try {
            bookmarks.addAll(List.of(JsonUtils.GSON.fromJson(new FileReader(PATH, StandardCharsets.UTF_8), Illustration[].class)));
        } catch (IOException ignored) {
        }
        pixiv.addIllustUpdateListener(this::refresh);
        SwingUtilities.invokeLater(() -> pixiv.addOnLoadListener(result -> {
            if (result != null) {
                this.user = result.getUser();
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
            } else setIllustrations(bookmarks);
        }));
    }


    public void refresh(Illustration illustration) {
        if (illustration.isBookmarked() && bookmarks.stream().noneMatch(b -> Objects.equals(b.getId(), illustration.getId()))) {
            bookmarks.add(0, illustration);
            GalleryItem holder = gallery.getRefreshOrCreate(illustration);
            gallery.grid.add(holder, 0);
        } else if (!illustration.isBookmarked() && bookmarks.removeIf(b -> Objects.equals(b.getId(), illustration.getId()))) {
            GalleryItem holder = gallery.getRefreshOrCreate(illustration);
            gallery.grid.remove(holder);
        }
        try {
            writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Illustration> loadAllBookmarks(Consumer<ArrayList<Illustration>> updater) throws PixivException, IOException {
        AtomicReference<String> nextUrl = new AtomicReference<>(null);
        long stopAt = bookmarks.size() > 0 ? bookmarks.get(0).getId() : -1;
        int counter = 0;
        do {
            List<Illustration> bunch = loadBookmarks(nextUrl, stopAt);
            bookmarks.addAll(counter, bunch);
            counter += bunch.size();
            SwingUtilities.invokeLater(() -> updater.accept(bookmarks));
        } while (nextUrl.get() != null);
        writeToFile();
        return bookmarks;
    }

    private void writeToFile() throws IOException {
        FileWriter writer = new FileWriter(PATH, StandardCharsets.UTF_8);
        JsonUtils.GSON.toJson(bookmarks, writer);
        writer.flush();
        writer.close();
    }

    public List<Illustration> loadBookmarks(AtomicReference<String> nextURL, Long stopAt) throws PixivException, IOException {
        SearchedIllusts searchedIllusts;
        try {
            if (nextURL.get() == null) searchedIllusts = pixiv.bookmarks(new V2Filter(user.getId()));
            else
                searchedIllusts = pixiv.requestSender.send(pixiv.createApiReqBuilder().url(nextURL.get()).get().build(), SearchedIllusts.class);
        } catch (APIException exception) {
            exception.printStackTrace();
            try {
                indicator.setRunning(false);
                Thread.sleep(10000);
                indicator.setRunning(true);
            } catch (InterruptedException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
                nextURL.set(null);
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

    @Override
    public void refresh(List<Illustration> illustrations) {
        super.refresh(illustrations);
        countLabel.setText(illustrations.size() + " shown");
    }
}
