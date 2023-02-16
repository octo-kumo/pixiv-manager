package me.kumo.ui.control.filter;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.tristate.TristateCheckBox;
import com.github.weisj.darklaf.components.tristate.TristateState;
import me.kumo.ui.control.ControlPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionFilter extends JPanel implements ActionListener, IllustrationFilter {
    private final TristateCheckBox r18;
    private final TristateCheckBox visible;
    private final TristateCheckBox restrict;
    private final ControlPane controlPane;

    public OptionFilter(ControlPane controlPane) {
        super(new FlowLayout(FlowLayout.LEADING));
        this.controlPane = controlPane;
        add(r18 = new TristateCheckBox("R-18", null, TristateState.INDETERMINATE_SEL) {{
            addActionListener(OptionFilter.this);
        }});
        add(visible = new TristateCheckBox("Visible", null, TristateState.INDETERMINATE_SEL) {{
            addActionListener(OptionFilter.this);
        }});
        add(restrict = new TristateCheckBox("Restricted", null, TristateState.INDETERMINATE_SEL) {{
            addActionListener(OptionFilter.this);
        }});
    }

    public boolean test(Illustration illustration) {
        TristateState r18 = this.r18.getState();
        TristateState visible = this.visible.getState();
        TristateState restrict = this.restrict.getState();
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


        if (!restrict.isIndeterminate())
            if (restrict == TristateState.SELECTED && illustration.getRestrict() == 0) {
                return false;
            } else return restrict != TristateState.DESELECTED || illustration.getRestrict() == 0;


        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(controlPane::applyAll);
    }
}
