package me.kumo.ui.gallery;

import me.kumo.io.LocalGallery;
import me.kumo.ui.utils.FileTransferable;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

public class GalleryItemHandler extends TransferHandler {
    public int getSourceActions(JComponent c) {
        return COPY;
    }

    public Transferable createTransferable(JComponent c) {
        if (c instanceof GalleryItem) {
            if (((GalleryItem) c).image.downloaded()) {
                System.out.println("lolol");
                return new FileTransferable(List.of(LocalGallery.getImage(((GalleryItem) c).getIllustration().getId())));
            } else {
                System.out.println("nonono");
                return new StringSelection("https://pixiv.net/artworks/" + ((GalleryItem) c).getIllustration().getId());
            }
        }
        return new StringSelection("not supported");
    }

    public void exportDone(JComponent c, Transferable t, int action) {
        for (DataFlavor flavor : t.getTransferDataFlavors()) {
            try {
                System.out.println(flavor + " :: " + t.getTransferData(flavor));
            } catch (UnsupportedFlavorException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
