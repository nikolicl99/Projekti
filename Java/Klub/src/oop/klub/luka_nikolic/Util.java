package oop.klub.luka_nikolic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public abstract class Util {
    private static final String textFileName = "club.txt";
    private static final String initials = "[LN]";
    private static final String equivalent = "=";
    private static final String curlyBraceLeft = "{";
    private static final String curlyBraceRight = "}";

    public static final void show(Collection<Club> set) {
        for (Club club : set) {
            Club element = club;
            element.showData();
        }
    }

    public static final ArrayList list(Collection<Club> set) {
        ArrayList<Club> list = new ArrayList(set);
        Collections.sort(list);
        return list;
    }

    public static final ArrayList<Football> footballList(Collection<Club> set) {
        ArrayList<Football> list = new ArrayList<>();
        for (Club element : set)
            if (element.getClass() == (Football.class)) list.add((Football) element);
        return list;
    }

    public static final LinkedList<Basketball> basketballList(Collection<Club> set) {
        LinkedList<Basketball> list = new LinkedList<>();
        for (Club element : set)
            if (element.getClass() == (Basketball.class)) list.add((Basketball) element);
        return list;
    }

    public static String getTextFileName() {
        return textFileName;
    }

    public static String getInitials() {
        return initials;
    }

    public static String getEquivalent() {
        return equivalent;
    }

    public static String getCurlyBraceLeft() {
        return curlyBraceLeft;
    }

    public static String getCurlyBraceRight() {
        return curlyBraceRight;
    }
}
