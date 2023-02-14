package me.kumo.ui.filter;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.tristate.TristateState;

public class FilterOptions {
    private TristateState r18 = TristateState.DESELECTED;

    public void setR18(TristateState state) {
        r18 = state;
    }

    public boolean test(Illustration illustration) {
        if (!r18.isIndeterminate())
            if (r18 == TristateState.SELECTED && illustration.getXRestrict() == 0 ||
                    r18 == TristateState.DESELECTED && illustration.getXRestrict() != 0) return false;

        return true;
    }
}
