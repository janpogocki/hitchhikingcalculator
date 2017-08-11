package pl.janpogocki.hitchhikingcalculator.javas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class CenyPaliw {
    public static String stringHTML = "";

    public CenyPaliw() throws Exception {
        String urlPetrol = "http://www.e-petrol.pl/notowania/rynek-krajowy/ceny-stacje-paliw";
        URL url = new URL(urlPetrol);
        URLConnection connection = url.openConnection();

        BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            stringHTML = stringHTML + inputLine;
        }
        br.close();
    }

    public static String getDate(){
        String [] parsed = stringHTML.split("<td class=\"even\">");
        String [] parsed2 = parsed[1].split("</td>");

        return parsed2[0];
    }

    public static double getCost4Person(double dlugosc, double spalanie, int iloscOsob, double cenaPaliwa, double dodatkoweKoszty){
        if (iloscOsob == 0)
            return 0;
        else{
            double koszt = ((dlugosc * spalanie * cenaPaliwa)/(100 * iloscOsob)) + (dodatkoweKoszty / iloscOsob);
            return (double) Math.round(koszt * 100) / 100;
        }
    }

    public static double getCenaPaliwa(String gatunekPaliwa) throws Exception{
        String [] parsed = stringHTML.split("<td class=\"even\">");
        String [] parsed2 = null;
        // PB98, PB95, ON, LPG = 3,4,5,6

        switch (gatunekPaliwa) {
            case "Pb 98":
                parsed2 = parsed[3].split("</td>");
                break;
            case "Pb 95":
                parsed2 = parsed[4].split("</td>");
                break;
            case "ON":
                parsed2 = parsed[5].split("</td>");
                break;
            case "LPG":
                parsed2 = parsed[6].split("</td>");
                break;
        }

        // zamiana przecinka na kropke
        String parsed2bis = parsed2[0].replace(",", ".");

        // zamiana stringa na double

        return Double.parseDouble(parsed2bis);
    }

}
