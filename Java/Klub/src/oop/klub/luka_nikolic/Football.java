package oop.klub.luka_nikolic;

public class Football extends Club {
    private String halfSeason;

    public Football(String league, int membersNo, Category category, String halfSeason) {
        super(league, membersNo, category);
        this.halfSeason = halfSeason;
    }

    @Override
    public void showData() {
        super.showData();
        System.out.println(" - half season: " + getHalfSeason());
    }

    public String getHalfSeason() {
        return halfSeason;
    }

}
