package fr.limayrac.ppe.henrybouetard.frediapp;

/**
 * Created by William on 08/04/2016.
 */
public class Motif {

    private String motif;
    private int id;

    public Motif(String motif, int id) {
        this.motif = motif;
        this.id = id;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
