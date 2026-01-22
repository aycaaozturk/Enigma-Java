package de.uniwue.jpp.encoder;

import java.sql.Ref;
import java.util.*;

public class Reflector implements Encoder {
    private static final char[] alfabe = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final List<Character> alfabeList = new ArrayList<>();

    static {
        for (char ch : alfabe) {
            alfabeList.add(ch);
        }
    }

//	Initialisiert einen Reflector mit übergebenem Mapping. Behandeln Sie folgende Ausnahmefälle:
//	map hat den Wert null: Werfen Sie eine aussagekräftige EncoderCreationException.
//	map beinhaltet nicht alle 26 Buchstaben des Alphabets: Werfen Sie eine aussagekräftige EncoderCreationException.
//	map hat doppelte Werteinträge (z.B: a->b, d->b): Werfen Sie eine aussagekräftige EncoderCreationException.
//	Mapping ist illegal für einen Reflector:
//	Bei einem Reflector müssen alle Schlüssel symmetrisch mappen, d.h. gilt a->b so muss auch b->a.
//	Nur so ist eine Reflektion möglich. Werfen Sie eine aussagekräftige EncoderCreationException im Falle einer illegalen Map.
//

    HashMap<Character, Character> map= new HashMap<>();
    HashMap<Character, Character> map_inv;

    public Reflector(HashMap<Character, Character> map) throws EncoderCreationException {
        if (map == null || map.isEmpty() == true) {
            throw new EncoderCreationException("map is null! or has null values/keys (Fixed Encoder");
        }

        if(map.size()!=26){
            throw new EncoderCreationException("map size invalid");
        }

        for (Character c : alfabeList) {
            if (map.containsKey(c) == false || map.containsValue(c) == false) {
                throw new EncoderCreationException("map doesnt contain the letter (Fixed Encoder");
            }
        }
        Set<Character> valuesSet = new HashSet<>(map.values());
        if (valuesSet.size() != map.size()) {
            throw new EncoderCreationException("some values are doubled in the map (Fixed Encoder");
        }
        Set<Map.Entry<Character, Character>> mapSet = map.entrySet();
        List<Map.Entry<Character, Character>> mapList = new ArrayList<>(mapSet);
        List<Character> KeyList = new ArrayList<>(map.keySet());
        List<Character> ValueList = new ArrayList<>();
        for (char c : KeyList) {
            ValueList.add(map.get(c));
        }
//       for(char c: KeyList){
//           Map.Entry<Character,Character> aa = new AbstractMap.SimpleEntry<>(c,c);
//           if(mapList.contains(aa)){
//               throw new EncoderCreationException("a!=a Reflection not valid!");
//           }
//       }

        for (int i = 0; i < KeyList.size(); i++) {
            char key = KeyList.get(i);
            char value = ValueList.get(i);
            Map.Entry<Character, Character> forwardAB = new AbstractMap.SimpleEntry<>(key, value);
            Map.Entry<Character, Character> backwarBA = new AbstractMap.SimpleEntry<>(value, key);
            if ((mapSet.contains(forwardAB) == true && mapSet.contains(backwarBA) == false) || (mapSet.contains(forwardAB) == false && mapSet.contains(backwarBA) == true)) {
                throw new EncoderCreationException("Reflection not valid");

            }


        }
        this.map.putAll(map);

    }

    @Override
    public char encode(char c) throws EncoderInputException {
    //    Encodiert den Buchstaben c in einen anderen Buchstaben (abhängig vom Mapping des Encoders).
        //    Wenn diesem Encoder noch Encoder nachfolgen soll der Aufruf von encode rekursiv weitergegeben werden
        //    und auch die Rückgabe "rückwärts" gemapped werden, bevor das Ergbenis zurückgegeben wird.
        //    Ist c nicht aus [a-z] so soll eine aussagekräftige EncoderInputException geworfen werden.
        if (alfabeList.contains(c) == false || Character.isLowerCase(c) == false) {
            throw new EncoderInputException("the character isnt in the ABC (Fixed Encoder");
        }
        char mappedChar = map.get(c);
        return mappedChar;

    }

    @Override
    public void rotate(boolean carry) {
       return;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(map);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Reflector ==false){
            return false;
        }
        else{
            Reflector compare = (Reflector) obj;
            if(map.equals(compare.map)){
                return true;
            }
            else{
                return false;
            }
        }
    }

    @Override
    public String toString() {
        Set<Map.Entry<Character,Character>> mapSet = map.entrySet();
        StringBuilder printThis = new StringBuilder();
        List<Map.Entry<Character, Character>> mapList = new ArrayList<>();
        for(char letter: alfabeList){
            Map.Entry<Character,Character> input = new AbstractMap.SimpleEntry<>(letter, map.get(letter));

            mapList.add(input);

        }

        for(int i=0; i< mapList.size()-1; i++){
            printThis.append(mapList.get(i).getKey() +"->"+ mapList.get(i).getValue()+"\n");

        }
        printThis.append(mapList.get(mapList.size()-1).getKey()+"->"+ mapList.get(mapList.size()-1).getValue());
        return printThis.toString();

    }
}