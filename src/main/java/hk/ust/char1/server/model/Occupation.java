package hk.ust.char1.server.model;

import javax.persistence.Embeddable;

@Embeddable
public class Occupation {
    private String occupationTitle;
    private String position;

    public Occupation() {
    }

    public Occupation(String occupationTitle) {
        this.occupationTitle = occupationTitle;
    }

    public String getOccupationTitle() {
        return occupationTitle;
    }

    public void setOccupationTitle(String occupationTitle) {
        this.occupationTitle = occupationTitle;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
