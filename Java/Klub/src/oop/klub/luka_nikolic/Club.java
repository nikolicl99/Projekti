package oop.klub.luka_nikolic;

import oop.klub.luka_nikolic.Exceptions.InitialsNotMatching;
import oop.klub.luka_nikolic.Exceptions.MessageContainsCharacters;
import oop.klub.luka_nikolic.Interfaces.ShowingData;

public abstract class Club implements ShowingData, Comparable<Club> {
    private final String type = this.getClass().getSimpleName();
    private String league;
    private int membersNo;
    private Category category;

    public Club(String league, int membersNo, Category category) {
        this.league = league;
        this.membersNo = membersNo;
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public String getLeague() {
        return league;
    }

    public int getMembersNo() {
        return membersNo;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public void showData() throws InitialsNotMatching, MessageContainsCharacters {
        String message = Util.getInitials() + " - type: " + getType() + " - league: " + getLeague() + " - members no: " + getMembersNo() + " - category: " + getCategory();
        if (message.contains(Util.getCurlyBraceLeft()) || message.contains(Util.getCurlyBraceRight()) || message.contains(Util.getEquivalent())) {
            throw new MessageContainsCharacters("You tried to use \"{\", \"}\" and/or \"=\" in presentation message!");
        } else if (message.startsWith(Util.getInitials())) {
            System.out.print(message);
        } else {
            throw new InitialsNotMatching("The message doesn't start with the initials: " + Util.getInitials());
        }
    }

    @Override
    public int compareTo(Club club) {
        if (getMembersNo() > club.getMembersNo()) {
            return 1;
        } else if (getMembersNo() < club.getMembersNo()) {
            return -1;
        } else {
            return 0;
        }
    }
}
