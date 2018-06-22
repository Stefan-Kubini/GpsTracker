package sk.upjs.kubini.gps2.pocasieProvider;

public class struPocasie {
    public String casPocasia = "";
    public String slovnyPopis = "";
    public double tlak = 0.0;
    public double smerVetra = 0.0;
    public double rychlostVetra = 0.0;
    public double teplota = 0.0;
    public int vlhkost = 0;
    public int oblacnost = 0;
    public double mnozstvoZrazok = 0.0;

    public String toString() {
        return casPocasia + " " + slovnyPopis + " " + tlak + " " + smerVetra + " " + rychlostVetra + " " + teplota + " " + vlhkost + " " + oblacnost + " " + mnozstvoZrazok;

    }
}
