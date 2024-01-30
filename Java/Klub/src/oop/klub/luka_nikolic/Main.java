package oop.klub.luka_nikolic;

import oop.klub.luka_nikolic.IOStreams.FileReader;
import oop.klub.luka_nikolic.IOStreams.FileWriter;

import java.util.*;

import static oop.klub.luka_nikolic.Category.*;

public class Main {
    public static void main(String[] args) {
        Club Manchester = new Football("Premier", 32, SENIOR, "winter");
        Club RedStar = new Football("SuperLiga", 16, SENIOR, "summer");
        Club Barcelona = new Football("La Liga", 24, CADET, "summer");

        Club RealMadrid = new Basketball("ACB", 30, CADET);
        Club Lakers = new Basketball("NBA", 50, JUNIOR);
        Club Partizan = new Basketball("ABA", 13, CADET);

        Set<Club> clubs = new HashSet<>();

        clubs.add(Manchester);
        clubs.add(RedStar);
        clubs.add(Barcelona);

        clubs.add(RealMadrid);
        clubs.add(Lakers);
        clubs.add(Partizan);

        Util.show(clubs);
        System.out.println();

        ArrayList<Club> sortedClubs = Util.list(clubs);
        Util.show(sortedClubs);
        System.out.println();

        ArrayList<Football> footballList = Util.footballList(clubs);

        LinkedList<Basketball> basketballList = Util.basketballList(clubs);

        Runnable FootballWriter = new FileWriter(footballList);
        Runnable BasketballWriter = new FileWriter(basketballList);
        Runnable fileReader = new FileReader();

        Thread threadFootballWriter = new Thread(FootballWriter);
        Thread threadBasketballWriter = new Thread(BasketballWriter);
        Thread threadFileReader = new Thread(fileReader);

        threadFootballWriter.start();
        threadBasketballWriter.start();

        try {
            threadFootballWriter.join();
            threadBasketballWriter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadFileReader.start();
    }
}