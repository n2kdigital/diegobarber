package net.mobil.nembotmarius.snackpos.expandablelistview;

public class Product {
    public String quantity;
    public String designation;
    public String montant;
    public String hhmm;

    public Product(String quantity,String designation,String montant,String hhmm) {
        this.quantity = quantity;
        this.designation = designation;
        this.montant = montant;
        this.hhmm = hhmm;
    }
}
