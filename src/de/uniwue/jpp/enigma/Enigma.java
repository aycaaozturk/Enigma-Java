package de.uniwue.jpp.enigma;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uniwue.jpp.encoder.*;

public class Enigma {

    FixedEncoder first;
    ArrayList<Encoder> ENIG= new ArrayList<>();
    List<String> EncoderTypes= new ArrayList<>();
    private static final char[] alfabe = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final List<Character> alfabeList = new ArrayList<>();

    static {
        for (char ch : alfabe) {
            alfabeList.add(ch);
        }
    }

//	Initialisiert eine neue Enigma-Instanz mit first als ersten Baustein. Behandeln Sie folgende Ausnahmen:
//	first hat den Wert null: Werfen Sie eine aussagekräftige EnigmaCreationException.

    public Enigma(FixedEncoder first) throws EnigmaCreationException {
        if (first == null) {
            throw new EnigmaCreationException("first cant be null");
        }
        this.first = first;
        this.ENIG.add(first);
    }

//	Initialisiert eine neue Enigma-Instanz durch Einlesen von Daten im obigen Dateiformat von is.
//	Hinweis: Benutzen Sie zum Lesen des InputStreams die Klasse Scanner. Beachten Sie dabei,
//	dass es nur möglich ist eine Instanz eines Scanners für den gleichen InputStream zu benutzen.
//	Behandeln Sie folgende Ausnahmefälle:
//	is hat den Wert null: Werfen Sie eine aussagekräftige EnigmaCreationException.
//	is ist leer: Werfen Sie eine aussagekräftige EnigmaCreationException.
//	is enthält nur einen Encoder: Werfen Sie eine aussagekräftige EnigmaCreationException.
//	is enthält Encoder gleichen Typs direkt hintereinander (z.B: # fixed, # fixed, # rotating, ...): Werfen Sie eine aussagekräftige EnigmaCreationException.
//	is enthält zwei Reflectoren: Werfen Sie eine aussagekräftige EnigmaCreationException.
//	Der erste Encoder ist nicht vom Typ FixedEncoder: Werfen Sie eine aussagekräftige EnigmaCreationException.

    public Enigma(InputStream is) throws IOException, EnigmaCreationException {
        if (is == null) {
            throw new EnigmaCreationException("input stream is null");
        }
        Scanner inputEnig = new Scanner(is);

        List<String> inputList = new ArrayList<>();
       // List<String> EncoderTypes = new ArrayList<>();
        while (inputEnig.hasNextLine()) {
            String line = inputEnig.nextLine().trim();
            if (line.isEmpty() == true) {
                throw new EnigmaCreationException("empty");
            } else {
                inputList.add(line);
            }
        }
        if(inputList.size()%27 !=0){
            throw new EnigmaCreationException("incorrect number of lines");
        }
        //name of encoder - 27
        // 26 letters
        int numberOfEncoders = inputList.size() / 27;  //bu kadar sayida 27 harf var
        // encoder indexi = number of encoders sayisina ulasana kadar -1
        //                  0*27, 1*27, 2*27,....
        if(numberOfEncoders<=1){
            throw new EnigmaCreationException("encoders: less than 2");
        }

        // A a b c d e f   B a b c  d  e  f   C  a  b  c  d  e  f      6 harf, toplam 7,
        // 0 1 2 3 4 5 6   7 8 9 10 11 12 13  14 15 16 17 18 19 20     size=21  encoder: 3

        for (int i = 0; i < inputList.size(); i += 27) {
            EncoderTypes.add(inputList.get(i));
        }
        //	is enthält nur einen Encoder: Werfen Sie eine aussagekräftige EnigmaCreationException.
//	is enthält Encoder gleichen Typs direkt hintereinander (z.B: # fixed, # fixed, # rotating, ...): Werfen Sie eine aussagekräftige EnigmaCreationException.
//	is enthält zwei Reflectoren: Werfen Sie eine aussagekräftige EnigmaCreationException.
//	Der erste Encoder ist nicht vom Typ FixedEncoder: Werfen Sie eine aussagekräftige EnigmaCreationException.
        if (EncoderTypes.size() == 1) {
            throw new EnigmaCreationException("only 1 encode: invalid");
        }
        for (int a = 1; a < EncoderTypes.size(); a++) {
            if (EncoderTypes.get(a - 1).equals(EncoderTypes.get(a))) {
                throw new EnigmaCreationException("following encoders cant be the same");
            }
        }
        String patternRotating = "# rotating";
        String patternFixed = "# fixed";
        String patternReflector = "# reflecting";
        int numberOfRef = 0;
        for (int r = 0; r < EncoderTypes.size(); r++) {
            if (EncoderTypes.get(r).equals(patternReflector)) {
                numberOfRef++;
            }
        }
        if (numberOfRef >= 2) {
            throw new EnigmaCreationException("2 reflectors not valid");
        }
        if (EncoderTypes.get(0).equals(patternFixed) == false) {
            throw new EnigmaCreationException("first encoder should be fixed");
        }
        //inputlist
        //encoderTypes: patternRotating, patternFixed, patternReflector
        // encoder indexi = number of encoders sayisina ulasana kadar -1
        //                  0*27, 1*27, 2*27,....

//		List<String> MapListOfFixed = new ArrayList<>();
//		List<String> MapListOfRotate = new ArrayList<>();
//		List<String> MapListOfReflector = new ArrayList<>();
        this.EncoderTypes= EncoderTypes;
        if(EncoderTypes==null || EncoderTypes.isEmpty()){
            throw new EnigmaCreationException("ENCODER TYPES EMPTY");
        }
//
        List<List> EncoderLists = new ArrayList<>();
        for (String enc : EncoderTypes) {
            if (enc.equals(patternFixed)) {
                List<String> MapListOfFixed = new ArrayList<>();
                EncoderLists.add(MapListOfFixed);
            } else if (enc.startsWith(patternRotating)) {

                List<String> MapListOfRotate = new ArrayList<>();
                EncoderLists.add(MapListOfRotate);
            } else if (enc.equals(patternReflector)) {
                List<String> MapListOfReflector = new ArrayList<>();
                EncoderLists.add(MapListOfReflector);
            }
        }  //bastan sonra bos encoderlar koyduk

        int numberOfIterations = 0;
        while (numberOfIterations <= numberOfEncoders) {
//            List<String> putThisList = new ArrayList<>();
            int encIndex;    //a->b
            for (encIndex = numberOfIterations * 27 + 1;  encIndex<Math.min(inputList.size(), numberOfIterations * 27 + 1 + 26); encIndex++) {
                //putThisList.add(inputList.get(encIndex));
                EncoderLists.get(numberOfIterations).add(inputList.get(encIndex));
            }
            //EncoderLists.add(numberOfIterations, putThisList);
            numberOfIterations++;

            //BU ALGORITMADAN SONRA BEYNIM KALMADI
        }
        List<Encoder> ENCODER = new ArrayList<>();

        for (int a = EncoderTypes.size() - 1; a >= 0; a--) {
            if (EncoderTypes.get(a).equals(patternReflector)) {
                List<String> mapThis = EncoderLists.get(a);
                HashMap<Character, Character> mapp = new HashMap<Character,Character>();
                for (String s : mapThis) {
                    char key = s.charAt(0);
                    char value = s.charAt(3);
                    mapp.put(key, value);
                }
                try {
                    Reflector ref = new Reflector(mapp);
                    ENCODER.add(ref);
                } catch (EncoderCreationException e) {
                    System.out.println("!");
                }


            }
            else if(EncoderTypes.get(a).startsWith(patternRotating)){
                String tipi = EncoderTypes.get(a);
                String[] num = tipi.split(" ");
                int iterations = 0;
                if (num.length == 3) {
                    String last = num[num.length-1];
                    iterations = Integer.parseInt(last);
                }

                List<String> mapThis = EncoderLists.get(a);
                HashMap<Character,Character> mapp = new HashMap<Character,Character>();
                for (String s : mapThis) {
                    char key = s.charAt(0);
                    char value = s.charAt(3);
                    mapp.put(key, value);
                }
                try {
                    RotatingEncoder rotate = new RotatingEncoder(ENCODER.get(ENCODER.size()-1), mapp, iterations);
                    ENCODER.add(rotate);
                } catch (Exception e) {
                    System.out.println("!");
                }
            }
            else{
                List<String> mapThis = EncoderLists.get(a);
                HashMap<Character, Character> mapp = new HashMap<Character,Character>();
                for (String s : mapThis) {
                    char key = s.charAt(0);
                    char value = s.charAt(3);
                    mapp.put(key, value);
                }
                try {
                    FixedEncoder fixed = new FixedEncoder(ENCODER.get(ENCODER.size()-1), mapp);
                    ENCODER.add(fixed);
                } catch (Exception e) {
                    System.out.println("!");
                }

            }


        }

        Collections.reverse(ENCODER);
        ENIG.addAll(ENCODER);

//        for(int rev = ENCODER.size()-1; rev>=0; rev-- ){
//            ENIG.add(ENCODER.get(rev));
//        }
//        this.first = (FixedEncoder)ENIG.get(0);

        if(ENIG==null || ENIG.isEmpty() || ENIG.size()==0){
            throw new EnigmaCreationException("ENIGMA EMPTY");
        }

        if(ENIG.get(0) instanceof FixedEncoder==true){
            this.first=(FixedEncoder) ENIG.get(0);
        }
        else{
            throw new EnigmaCreationException("first enc should be FIXED");
        }
    }

    public char enig(char s){
        char c = ' ';
        try {
            c = first.encode(s);
            first.rotate(true);
        } catch (EncoderInputException ex) {
            System.out.println("cant encode");
        }
        return c;
    }
    //    Liefert die Kodierung der in is enthaltenen Worte. Beachten Sie, dass auch Leerzeichen (' ') und NewLines ('\n')
//    enthalten sein können, jedoch nicht kodiert werden sollen. Die Ausgabe soll nur noch aus Kleinbuchstaben bestehen.
//    Die Tests auf PABS konstruieren zu Beginn diese Enigma und testen dann Eingaben.
//    Das kann Ihnen also bei der Fehlersuche behilflich sein.
//    Behandeln Sie folgende Ausnahmefälle:
//    is hat den Wert null: Werfen Sie eine aussagekräftige EnigmaEncryptionException.
//    is ist leer: Werfen Sie eine aussagekräftige EnigmaEncryptionException.
//    is enthält Großbuchstaben: Werfen Sie eine aussagekräftige EnigmaEncryptionException.
//

    public String encrypt(InputStream is) throws IOException, EnigmaEncryptionException {
        if (is == null) {
            throw new EnigmaEncryptionException("input stream is null");
        }
        Scanner inputEnig = new Scanner(is);
        String result = "";
        int count = 0;
        while (inputEnig.hasNextLine()) {
            String line = inputEnig.nextLine();
            if (count > 0) {
                result += "\n";
            }

            for (int i=0; i< line.length(); i++) {
                Character c = line.charAt(i);
                if (c==' ' || c=='\n') {
                    result += c;            //bosluklari sonuca aynen ekledik
                } else if(Character.isUpperCase(c)) {
                    throw new EnigmaEncryptionException("uppercase");
                }
                else if(alfabeList.contains(c)==false){
                    throw new EnigmaEncryptionException("illegal characters");
                }
                else {
                    count++;
                    result += enig(c);
                }
            }
         //   result += "\n";
        }
        if (count == 0) {
            throw new EnigmaEncryptionException("empty");
        }
        return result;


    }


    public String toString() {
        String patternRotating = "# rotating";
        String patternFixed = "# fixed";
        String patternReflector = "# reflecting";
        StringBuilder enigmaString = new StringBuilder();
        AbstractChainableEncoder current = first;
        while (current!=null){
            if (current instanceof RotatingEncoder) {
                enigmaString.append(patternRotating + "\n");
            } else if (current instanceof FixedEncoder) {
                enigmaString.append(patternFixed + "\n");
            }
            enigmaString.append(current.toString() + "\n");
            if (current.getDelegate() instanceof AbstractChainableEncoder ) {
                current = (AbstractChainableEncoder) current.getDelegate();
            } else {
                enigmaString.append(patternReflector + "\n");
                enigmaString.append(current.getDelegate().toString());
                break;

            }

        }

//
//        if(EncoderTypes==null || EncoderTypes.isEmpty() || ENIG==null || ENIG.isEmpty()){
//            try {
//                throw new EnigmaCreationException("ILLEGAL");
//            } catch (EnigmaCreationException e) {
//                e.printStackTrace();
//            }
//        }
//
//        for(int i=0; i<ENIG.size()-1; i++){
//            if(EncoderTypes.get(i).equals(patternFixed)){
//                enigmaString.append(patternFixed+"\n");
//                enigmaString.append(ENIG.get(i).toString()+"\n");
//            }
//            else if(EncoderTypes.get(i).startsWith(patternRotating)){
//                enigmaString.append(patternRotating+"\n");
//                enigmaString.append(ENIG.get(i).toString()+"\n");
//            }
//            else{
//                enigmaString.append(patternReflector+"\n");
//                enigmaString.append(ENIG.get(i).toString()+"\n");
//            }
//
//        }
//        if(EncoderTypes.get(EncoderTypes.size()-1).equals(patternReflector)){
//            enigmaString.append(patternReflector+"\n");
//            enigmaString.append(ENIG.get(ENIG.size()-1).toString());
//        }


        return enigmaString.toString();
    }
}
