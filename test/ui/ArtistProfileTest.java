package ui;

import me.kumo.ui.artist.ArtistDetails;

import javax.swing.*;

public class ArtistProfileTest {
    public static void main(String... args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(new ArtistDetails());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
