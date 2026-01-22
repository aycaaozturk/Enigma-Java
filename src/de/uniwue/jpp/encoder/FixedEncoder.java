package de.uniwue.jpp.encoder;

import java.util.*;

public class FixedEncoder extends AbstractChainableEncoder {
    //	A aB bC cD dE eF fG gH hI iJ jK kL lM mN nO oP pQ qR rS sT tU uV vW wX xY yZ z
    private static final char[] alfabe = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final List<Character> alfabeList = new ArrayList<>();

    static {
        for (char ch : alfabe) {
            alfabeList.add(ch);
        }
    }

    HashMap<Character, Character> map=new HashMap<>();
    HashMap<Character, Character> map_inv;


    public FixedEncoder(Encoder delegate, HashMap<Character, Character> map) throws EncoderCreationException {
//		Initialisiert einen neuen FixedEncoder mit den gegebenen Nachfolger und Mapping. Behandeln Sie folgende Ausnahmen:
//		delegate hat den Wert null: Werfen Sie eine aussagekräftige EncoderCreationException.
//				map hat den Wert null: Werfen Sie eine aussagekräftige von EncoderCreationException.
//		map beinhaltet nicht alle 26 Buchstaben des Alphabets: Werfen Sie eine aussagekräftige EncoderCreationException.
//				map hat doppelte Werteinträge (z.B: a->b, d->b): Werfen Sie eine aussagekräftige EncoderCreationException.
        super(delegate);


        if (map == null || map.isEmpty() == true ) {
            throw new EncoderCreationException("map is null! or has null values/keys (Fixed Encoder");
        }
        this.map.putAll(map);
        this.map_inv=new HashMap<>();

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


        for(Character c : this.map.keySet()){
            map_inv.put(this.map.get(c), c);
        }


    }

    @Override
    public char encode(char c) throws EncoderInputException {
        //	Diese implementiert die encode-Methode des Interfaces.
        //	Encodiert den Buchstaben c in einen anderen Buchstaben (abhängig vom Mapping des Encoders).
        //	Wenn diesem Encoder noch Encoder nachfolgen soll der Aufruf von encode rekursiv weitergegeben werden
        //	und auch die Rückgabe "rückwärts" gemapped werden, bevor das Ergbenis zurückgegeben wird.
        //	Ist c nicht aus [a-z] so soll eine aussagekräftige EncoderInputException geworfen werden.

        if(this.map==null || this.map.isEmpty()){
            throw new EncoderInputException("map null");
        }

        if (alfabeList.contains(c) == false || Character.isLowerCase(c) == false) {
            throw new EncoderInputException("the character isnt in the ABC (Fixed Encoder");
        }
//        int indexOfC = abcList.indexOf(c);
//		int shift =1;
//		int shiftedIndexOfC = (indexOfC + shift) % 26;
//		return abcList.get(shiftedIndexOfC);


        char mappedChar = map.get(c);
        char rück = getDelegate().encode(mappedChar);
        char rückwärts = map_inv.get(rück);
        return rückwärts;

    }

    @Override
    public void rotate(boolean carry) {
        //	Diese implementiert die rotate-Methode des Interfaces.
        //	Gibt an ob dieser Encoder rotieren soll. rotate wird also mit true aufgerufen,
        //	wenn der vorherige Encoder eine volle Rotation erreicht hat, oder man dieser Encoder der erste (Rotating-)Encoder ist.
        getDelegate().rotate(carry);
    }

    @Override
    public int hashCode() {
        //	Überschreiben Sie die Methode gemäß dem in der Java-API spezifizierten Vertrages.
    return Objects.hashCode(map);
    }

    @Override
    public boolean equals(Object obj) {
        //	Überschreiben Sie die Methode gemäß dem in der Java-API spezifizierten Vertrages so,
        //	dass zwei FixedEncoder genau dann gleich sind, falls ihre map identisch ist.
        if(obj instanceof FixedEncoder ==false){
            return false;
        }
        else{
            FixedEncoder compare = (FixedEncoder) obj;
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
     //   List<Map.Entry<Character,Character>> alfabetikList = new ArrayList<>();
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