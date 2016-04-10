package fr.limayrac.ppe.henrybouetard.frediapp;

/**
 * Created by William on 30/03/2016.
 */
public class NoteDeFrais {

    int id;
    String date;
    String trajet;
    int km;
    double coutP;
    double coutR;
    double coutH;
    int idDem;
    int idMotif;
    int annee;

    // Constructeur vide
    public NoteDeFrais() {

    }

    // Constructeur avec les paramètre id, date, trajet, km, coutPeage, coutRepas, coutHebergement,
    // iDemandeur, idMotif, idAnnee
    public NoteDeFrais(int id, String date, String trajet, int km, double coutP, double coutR, double coutH, int idDem, int idMotif, int annee) {
        this.id = id;
        this.date = date;
        this.trajet = trajet;
        this.km = km;
        this.coutP = coutP;
        this.coutR = coutR;
        this.coutH = coutH;
        this.idDem = idDem;
        this.idMotif = idMotif;
        this.annee = annee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTrajet() {
        return trajet;
    }

    public void setTrajet(String trajet) {
        this.trajet = trajet;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public double getCoutP() {
        return coutP;
    }

    public void setCoutP(double coutP) {
        this.coutP = coutP;
    }

    public double getCoutR() {
        return coutR;
    }

    public void setCoutR(double coutR) {
        this.coutR = coutR;
    }

    public double getCoutH() {
        return coutH;
    }

    public void setCoutH(double coutH) {
        this.coutH = coutH;
    }

    public int getIdDem() {
        return idDem;
    }

    public void setIdDem(int idDem) {
        this.idDem = idDem;
    }

    public int getIdMotif() {
        return idMotif;
    }

    public void setIdMotif(int idMotif) {
        this.idMotif = idMotif;
    }

    public int getIdAnnee() {
        return annee;
    }

    public void setIdAnnee(int idAnnee) {
        this.annee = annee;
    }

    public String formatYearFr() {
        String year = this.date.split("-")[0];
        String month = this.date.split("-")[1];
        String day = this.date.split("-")[2];
        return day + "-" + month + "-" + year;
    }
}
