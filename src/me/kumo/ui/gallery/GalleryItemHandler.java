package me.kumo.ui.gallery;

import me.kumo.components.utils.FileTransferable;
import me.kumo.io.LocalGallery;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class GalleryItemHandler extends TransferHandler {
    public int getSourceActions(JComponent c) {
        return COPY;
    }

    public Transferable createTransferable(JComponent c) {
        if (c instanceof GalleryItem) {
            if (((GalleryItem) c).image.downloaded()) {
                return new FileTransferable(LocalGallery.getImages(((GalleryItem) c).getIllustration()));
            } else {
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
