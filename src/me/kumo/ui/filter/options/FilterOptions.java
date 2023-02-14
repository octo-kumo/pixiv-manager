package me.kumo.ui.filter.options;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.tristate.TristateCheckBox;
import com.github.weisj.darklaf.components.tristate.TristateState;
import me.kumo.ui.filter.FilterPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.stream.Stream;

public class FilterOptions extends JPanel implements ActionListener {
    private final TristateCheckBox r18;
    private final TristateCheckBox visible;
    private final FilterPane filterPane;

    public FilterOptions(FilterPane filterPane) {
        super(new FlowLayout(FlowLayout.LEADING));
        this.filterPane = filterPane;
        add(r18 = new TristateCheckBox("R-18", null, TristateState.INDETERMINATE_SEL) {{
            addActionListener(FilterOptions.this);
        }});
        add(visible = new TristateCheckBox("Visible", null, TristateState.INDETERMINATE_SEL) {{
            addActionListener(FilterOptions.this);
        }});
    }

    public boolean test(Illustration illustration) {
        TristateState r18 = this.r18.getState();
        TristateState visible = this.visible.getState();
        if (!r18.isIndeterminate())
            if (r18 == TristateState.SELECTED && illustration.getXRestrict() == 0) {
                return false;
            } else if (r18 == TristateState.DESELECTED && illustration.getXRestrict() != 0) {
                return false;
            }

        if (!visible.isIndeterminate())
            if (visible == TristateState.SELECTED && !illustration.isVisible()) {
                return false;
            } else if (visible == TristateState.DESELECTED && illustration.isVisible()) {
                return false;
            }


        return true;
    }

    public Stream<Illustration> filter(Stream<Illustration> illustrations) {
        return illustrations.filter(this::test);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(filterPane::refresh);
    }
}
