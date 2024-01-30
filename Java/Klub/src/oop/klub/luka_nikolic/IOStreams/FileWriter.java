package oop.klub.luka_nikolic.IOStreams;

import oop.klub.luka_nikolic.Club;
import oop.klub.luka_nikolic.Util;

import java.io.*;
import java.util.List;

public class FileWriter implements Runnable {
    private List list;

    public FileWriter(List list) {
        this.list = list;
    }

    @Override
    public void run() {
        List<Club> list2 = list;
        try (Writer writer = new java.io.FileWriter(Util.getTextFileName(), true)) {
            for (Club club : list2) {
                writer.write(club.getLeague() + " [" + club.getType() + "]\n");
            }
            Thread.yield();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
