package me.kumo.ui.control.filter;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import com.github.weisj.darklaf.components.tristate.TristateCheckBox;
import com.github.weisj.darklaf.components.tristate.TristateState;
import me.kumo.io.Process;
import me.kumo.ui.Refreshable;
import me.kumo.ui.control.ControlPane;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ToolFilter extends OverlayScrollPane implements IllustrationFilter, Refreshable<List<Illustration>> {
    private final ControlPane controlPane;
    private final JPanel filters;
    private String[] tools;
    private TristateState[] states;

    public ToolFilter(ControlPane controlPane) {
        super(null, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getScrollPane().setViewportView(filters = new JPanel(new FlowLayout(FlowLayout.LEADING)));
        this.controlPane = controlPane;
    }

    @Override
    public void reset() {
        tools = null;
        states = null;
    }

    @Override
    public boolean test(Illustration illustration) {
        if (tools == null) return true;
        List<String> illustrationTools = illustration.getTools();
        for (int i = 0; i < tools.length; i++) {
            if (states[i] == null || states[i].isIndeterminate()) continue;
            if (states[i] == TristateState.DESELECTED && illustrationTools.contains(tools[i])) return false;
            if (states[i] == TristateState.SELECTED && !illustrationTools.contains(tools[i])) return false;
        }
        return true;
    }

    @Override
    public void refresh(List<Illustration> illustrations) {
        HashMap<String, TristateState> map = new HashMap<>();
        for (int i = 0; tools != null && i < tools.length; i++)
            if (!states[i].isIndeterminate()) map.put(tools[i], states[i]);
        tools = Process.getTools(illustrations);
        tools = Stream.concat(Stream.concat(
                                map.entrySet().stream().filter(entry -> entry.getValue() == TristateState.SELECTED).map(Map.Entry::getKey),
                                map.entrySet().stream().filter(entry -> entry.getValue() == TristateState.DESELECTED).map(Map.Entry::getKey)),
                        Arrays.stream(tools))
                .distinct()
                .toArray(String[]::new);
        states = new TristateState[tools.length];
        filters.removeAll();
        for (int i = 0; i < tools.length; i++) {
            final int I = i;
            TristateCheckBox checkBox = new TristateCheckBox(tools[I], null, states[I] = map.getOrDefault(tools[I], TristateState.INDETERMINATE_SEL));
            checkBox.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                states[I] = checkBox.getState();
                System.out.println(states[I]);
                controlPane.applyAll();
            }));
            filters.add(checkBox);
        }
    }
}
