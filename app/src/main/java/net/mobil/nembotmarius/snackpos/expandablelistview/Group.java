package net.mobil.nembotmarius.snackpos.expandablelistview;

import java.util.ArrayList;
import java.util.List;

public class Group {

    public String nofact;
    public String totqte;
    public String montant;
    public String facture;
    public String valide;
    public final List<Product> details = new ArrayList<Product>();

    public Group() {
        nofact="";
        totqte="0";
        montant="0";
        facture="";
        valide="";
    }

    public String getNofact() {
        return nofact;
    }

    public void setNofact(String nofact) {
        this.nofact = nofact;
    }

    public String getTotqte() {
        return totqte;
    }

    public void setTotqte(String totqte) {
        this.totqte = totqte;
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public String getFacture() {
        return facture;
    }

    public void setFacture(String facture) {
        this.facture = facture;
    }

    public String getValide() {
        return valide;
    }

    public void setValide(String valide) {
        this.valide = valide;
    }
}
