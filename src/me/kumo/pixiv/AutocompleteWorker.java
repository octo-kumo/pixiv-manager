package me.kumo.pixiv;

import pixivj.exception.APIException;
import pixivj.model.Autocomplete;
import pixivj.model.SearchedIllustsFilter;

import javax.swing.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class AutocompleteWorker extends SwingWorker<Autocomplete, String> {
    private final String word;
    private final Consumer<Autocomplete> consumer;

    public AutocompleteWorker(String word, Consumer<Autocomplete> consumer) {
        this.word = word;
        this.consumer = consumer;
    }

    @Override
    protected Autocomplete doInBackground() throws Exception {
        Thread.sleep(200);
        return Pixiv.getInstance().autocomplete(new SearchedIllustsFilter(word));
    }

    @Override
    protected void done() {
        try {
            consumer.accept(get());
        } catch (InterruptedException | CancellationException e) {
//            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof APIException) return;
            throw new RuntimeException(e);
        }
    }
}
