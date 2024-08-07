package me.kumo.ui.control.filter;

import com.github.weisj.darklaf.components.OverlayScrollPane;
import com.github.weisj.darklaf.components.tristate.TristateCheckBox;
import com.github.weisj.darklaf.components.tristate.TristateState;
import me.kumo.io.LocalGallery;
import me.kumo.ui.control.ControlPane;
import pixivj.model.Illustration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.function.Function;

public class OptionFilter extends OverlayScrollPane implements ActionListener, IllustrationFilter {
    private final ControlPane controlPane;
    private final ArrayList<ToggleOption> options;

    public OptionFilter(ControlPane controlPane) {
        super(null, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEADING));
        getScrollPane().setViewportView(filters);
        this.controlPane = controlPane;
        options = new ArrayList<>();
        options.add(new ToggleOption("R-18", i -> i.getXRestrict() != 0, this));
        options.add(new ToggleOption("AI", Illustration::isAI, this));
        options.add(new ToggleOption("Bookmarked", Illustration::isBookmarked, this));
        options.add(new ToggleOption("Landscape", i -> i.getWidth() > i.getHeight(), this));
        options.add(new ToggleOption("Visible", Illustration::isVisible, this));
        options.add(new ToggleOption("Restricted", i -> i.getRestrict() != 0, this));
        options.add(new ToggleOption("Missing", i -> LocalGallery.getImage(i.getId()) == null, this));
        options.forEach(filters::add);
    }

    public boolean test(Illustration illustration) {
        return options.stream().allMatch(o -> o.test(illustration));
    }

    @Override
    public void reset() {
        options.forEach(ToggleOption::reset);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(controlPane::applyAll);
    }

    public static class ToggleOption extends TristateCheckBox implements IllustrationFilter {
        private final Function<Illustration, Boolean> checker;

        public ToggleOption(String name, Function<Illustration, Boolean> checker, ActionListener actionListener) {
            super(name, null, TristateState.INDETERMINATE_SEL);
            this.checker = checker;
            addActionListener(actionListener);
        }

        @Override
        public void reset() {
            setState(TristateState.INDETERMINATE_SEL);
        }

        @Override
        public boolean test(Illustration illustration) {
            if (getState().isIndeterminate()) return true;
            return !checker.apply(illustration) ^ isSelected();
        }
    }
}
