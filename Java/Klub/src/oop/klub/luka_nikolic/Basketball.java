package oop.klub.luka_nikolic;

import oop.klub.luka_nikolic.Exceptions.InitialsNotMatching;
import oop.klub.luka_nikolic.Exceptions.MessageContainsCharacters;
import oop.klub.luka_nikolic.Interfaces.PlayingIndors;

public class Basketball extends Club implements PlayingIndors {
    public Basketball(String league, int membersNo, Category category) {
        super(league, membersNo, category);
    }

    @Override
    public void showData() throws InitialsNotMatching, MessageContainsCharacters {
        super.showData();
        System.out.println();
    }

    @Override
    public void playingIndors() {
        System.out.println("The club is playing indors.");
    }
}
