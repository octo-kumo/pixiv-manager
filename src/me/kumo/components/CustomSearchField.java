package me.kumo.components;

import com.github.weisj.darklaf.components.text.SearchEvent;
import com.github.weisj.darklaf.components.text.SearchListener;
import com.github.weisj.darklaf.components.text.SearchTextFieldWithHistory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.prefs.Preferences;

public class CustomSearchField extends SearchTextFieldWithHistory implements SearchListener {
    private static final Preferences preferences = Preferences.userNodeForPackage(CustomSearchField.class);
    private ArrayList<String> realHistory = new ArrayList<>();

    public CustomSearchField() {
        addSearchListener(this);
        String history = preferences.get("Search.History", "");
        for (String s : history.split(",")) {
            if (s.isEmpty()) continue;
            addEntry(new String(Base64.getDecoder().decode(s)));
        }
    }

    public void setSuggestions(List<String> list) {
        history.clearHistory();
        for (int i = list.size() - 1; i >= 0; i--) history.addEntry(list.get(i));
        history.show(this, 0, this.getHeight());
        requestFocusInWindow();
    }

    @Override
    public void searchPerformed(SearchEvent e) {
        realHistory.add(e.getText());
        preferences.put("Search.History", preferences.get("Search.History", "") + Base64.getEncoder().encodeToString(e.getText().getBytes(StandardCharsets.UTF_8)) + ",");
    }

    public void resetToRealHistory() {
        history.clearHistory();
        for (String s : realHistory) addEntry(s);
    }
}
