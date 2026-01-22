package de.uniwue.jpp.enigma;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EnigmaConsole {

//	Startet das Enigma-Programm indem es eine Instanz von EnigmaConsole erzeugt und run(InputStream is, OutputStream os)
//	mit dem Standard Ein- und Augbaestrom aufruft.
//	Nutzen Sie diese Methode zum Testen.

    Enigma enigma;

    public static void main(String[] args) {
        EnigmaConsole console = new EnigmaConsole();
        InputStream inputStream = System.in;
        OutputStream outputStream = System.out;
        console.run(inputStream, outputStream);

    }
//	public InputStream convertInputIntoInputStream(String dosyam){
//		File fi = new File(dosyam);
//		try {
//			InputStream convert = new FileInputStream(fi);
//			return convert;
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//
//
//	}

    public boolean enigmaConfigValid(String dosya) {
        try {
            InputStream is = new FileInputStream(dosya);
            try {
                Enigma e = new Enigma(is);
                this.enigma = e;
                return true;
            } catch (IOException ex) {
                return false;
            } catch (EnigmaCreationException ex) {
                return false;
            }


        } catch (FileNotFoundException e) {
            return false;
        }


    }

    public boolean userTextValid(String s) {
       String tryThis = "";
       if(s!=null){
           tryThis=s;
       }
        boolean valid = false;
        if ( (s!=null) && (tryThis.matches("[a-z ]+") == true)) {
            valid = true;
        }
        return valid;
    }

    public String encryptInput(String s) {
        InputStream is = new ByteArrayInputStream(s.getBytes());
        try {
            String encText = enigma.encrypt(is);
            return encText;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EnigmaEncryptionException e) {
            e.printStackTrace();
        }
        return "FEHLER";
    }


    public void run(InputStream is, OutputStream os) {
        String userText="";
        boolean end1 = false;
        boolean end2 = false;
        BufferedReader oku = new BufferedReader(new InputStreamReader(is));
        PrintWriter yaz = new PrintWriter(os, true); //true: otomatik flush
        yaz.println("Enigma wird gestartet.");
        while (end1 == false) {
            yaz.println("Bitte geben Sie den Pfad zu einer Konfiguration an:");
            try {
                String dosya = oku.readLine();
                if (Files.exists(Paths.get(dosya)) == false) {
                    yaz.println("Datei nicht gefunden!");
                    continue;
                }
                if (enigmaConfigValid(dosya) == false) {
                    yaz.println("Fehlerhafte Enigma-Datei!");
                    continue;
                }
                end1 = true;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        while (end2 == false) {
            yaz.println("Geben Sie ihren Ver- oder Entschlüsselten Text ein:");
            try {
                userText = oku.readLine();
                if (userTextValid(userText) == true) {
                    end2 = true;
                } else {
                    yaz.println("Nicht erlaubte Symbole!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        String encTextOfUser = encryptInput(userText);
        yaz.println("En- oder Dekodierter Text:"+ encTextOfUser);
        yaz.println("Vielen Dank für die Nutzung der Enigma.");


    }


}