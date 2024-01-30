package oop.klub.luka_nikolic.IOStreams;

import oop.klub.luka_nikolic.Util;

import java.io.*;

public class FileReader implements Runnable {
    public void run() {
        try (Reader reader = new java.io.FileReader(Util.getTextFileName())) {

            int eof = -1;
            int currentChar = reader.read();

            while (currentChar != eof) {
                Thread.sleep(900);
                char currentCharacter = (char) currentChar;
                System.out.print(currentCharacter);
                currentChar = reader.read();
            }
            System.out.println();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
