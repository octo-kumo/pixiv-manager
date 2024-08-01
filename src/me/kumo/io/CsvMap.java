package me.kumo.io;

import java.io.*;
import java.util.HashMap;

public class CsvMap extends HashMap<String, String> {

    private final String filename;

    public CsvMap(String filename) {
        this.filename = filename;
        this.loadDataFromFile();
    }

    public String put(String key, String value) {
        super.put(key, value);
        this.appendEntryToFile(key, value);
        return key;
    }

    private void loadDataFromFile() {
        File file = new File(this.filename);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] entry = line.split(",");
                if (entry.length == 2) {
                    super.put(entry[0], entry[1]);
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    private void appendEntryToFile(String key, String value) {
        File file = new File(this.filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(key + "," + value + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}