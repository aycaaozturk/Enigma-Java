package de.uniwue.jpp.encoder;

import java.util.*;
import java.util.Map.Entry;

public class RotatingEncoder extends AbstractChainableEncoder {
    private static final char[] alfabe = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final List<Character> alfabeList = new ArrayList<>();

    static {
        for (char ch : alfabe) {
            alfabeList.add(ch);
        }
    }

    public Encoder delegate;
    HashMap<Character, Character> map=new HashMap<>();
    HashMap<Character, Character> map_inv;
    int rotations;

    public RotatingEncoder(Encoder delegate) throws EncoderCreationException {
        super(delegate);
        map = new HashMap<>();
        map_inv = new HashMap<>();
        for (char c : alfabe) {
            map.put(c, c);
            map_inv.put(c, c);

        }
        rotations = 0;

    }
//	rotations değeri sıfır değilse, bu encoder önceden döndürülmüş anlamına gelir.
//			map, başlangıç durumu değil, mevcut (şu anki) harf eşleşmesini içerir.
//	Yani, bu map, encoder’ın önceki döndürmelerden sonra aldığı şekildir.
//	Bu encoder döndüğünde (rotate() çağrıldığında), map değişmelidir.


    public RotatingEncoder(Encoder delegate, HashMap<Character, Character> map, int rotations) throws EncoderCreationException {
//		Initialisiert einen RotatingEncoder mit übergebenem Mapping und bereits erledigten Rotationen.
//		Achtung: rotations hier nicht auf 0 zu setzen bedeutet, dass dieser Encoder sich schon gedreht hat.
//		map bezeichnet in diesem Fall das aktuelle und nicht das ursprüngliche Mapping.
//		Behandeln Sie folgende Ausnahmefälle:
//		delegate hat den Wert null: Werfen Sie eine aussagekräftige EncoderCreationException.
//				map hat den Wert null: Werfen Sie eine aussagekräftige von EncoderCreationException.
//		map beinhaltet nicht alle 26 Buchstaben des Alphabets: Werfen Sie eine aussagekräftige EncoderCreationException.
//				map hat doppelte Werteinträge (z.B: a->b, d->b): Werfen Sie eine aussagekräftige EncoderCreationException.
//				rotations ist negativ oder größer als 25: Werfen Sie eine EncoderCreationException.
        super(delegate);
        this.delegate = delegate;

        if (map == null || map.isEmpty()) {
            throw new EncoderCreationException("map or map values/keys are null (Rotating Encoder");
        }
        if (map.size() != 26) {
            throw new EncoderCreationException("map size invalid");
        }

        for (Character c : alfabeList) {
            if (map.containsKey(c) == false || map.containsValue(c) == false) {
                throw new EncoderCreationException("map doesnt contain all letters");
            }
        }
        Set<Map.Entry<Character, Character>> mapSet = map.entrySet();
        if (mapSet.size() != map.size()) {
            throw new EncoderCreationException("map has doubled values (Rotating Encoder");
        }
        if (rotations < 0 || rotations > 25) {
            throw new EncoderCreationException("rotations invalid (Rotating Encoder");
        }
        this.map.putAll(map);
        this.rotations = rotations;
        this.map_inv = new HashMap<>();
        for (Character c : this.map.keySet()) {
            map_inv.put(this.map.get(c), c);
        }


    }

    @Override
    public char encode(char c) throws EncoderInputException {
        //	Encodiert den Buchstaben c in einen anderen Buchstaben (abhängig vom Mapping des Encoders).
        //	Wenn diesem Encoder noch Encoder nachfolgen soll der Aufruf von encode rekursiv weitergegeben werden und
        //	auch die Rückgabe "rückwärts" gemapped werden, bevor das Ergbenis zurückgegeben wird.
        //	Ist c nicht aus [a-z] so soll eine aussagekräftige EncoderInputException geworfen werden.
        if (alfabeList.contains(c) == false || Character.isLowerCase(c) == false) {
            throw new EncoderInputException("the character isnt in the ABC (Fixed Encoder");
        }
        char mappedChar = map.get(c);
        char rück = getDelegate().encode(mappedChar);
        char rückwärts = map_inv.get(rück);
        return rückwärts;
    }
//	Birinci RotatingEncoder, ilk harften itibaren dönmeye başlar ve başlangıç konumuna geri gelene kadar dönmeye devam eder.
//	Ancak ancak bu tam dönüş tamamlandığında, ikinci RotatingEncoder bir adım ilerler.
//	Birinci RotatingEncoder tekrar tam bir tur attığında, ikinci RotatingEncoder bir kez daha döner.
//	Önemli Kural:
//	Her RotatingEncoder, ancak bir önceki RotatingEncoder tam bir dönüşü tamamladıktan sonra dönebilir!
//	Yani bir encoder tam bir tur atmadıkça, sonraki encoder hareket edemez.


    @Override
    public void rotate(boolean carry) {
        //	Gibt an ob dieser Encoder rotieren soll. rotate wird also mit true aufgerufen,

        // 	wenn der vorherige Encoder eine volle Rotation erreicht hat, oder man dieser Encoder der erste (Rotating-)Encoder ist.

        if (carry == true) {

            shiftMap();

            if (rotations == 25) {
                rotations = 0;  //basa döndük

            } else {
                rotations++;
            }
        }
        if (rotations == 0 ){
            getDelegate().rotate(carry);
        }
    }

    public void shiftMap() {
        HashMap<Character, Character> shiftedMap = new HashMap<>();

        List<Character> KeyList = new ArrayList<>(map.keySet());
        List<Character> ValueList = new ArrayList<>();

        for (int a = 0; a < KeyList.size(); a++) {
            Character keyOfIndex = KeyList.get(a);
            Character valueOfIndex = map.get(keyOfIndex);
            ValueList.add(valueOfIndex);
        }

        //hepsini bir kere kaydiricay, son harf en sona
        // a:a, b:b, ... z:z  ->>>>> a:b, b:c, ..., z:a

        Character firstKey = KeyList.get(0);
        Character firstValue = ValueList.get(0);
        for (int i = 0; i < 26 - 1; i++) {
            shiftedMap.put(KeyList.get(i), ValueList.get(i + 1));
        }
        shiftedMap.put(KeyList.get(KeyList.size() - 1), firstValue);
        map.clear();
        map.putAll(shiftedMap);
        this.map_inv = new HashMap<>();
        for (Character c : map.keySet()) {
            map_inv.put(map.get(c), c);
        }


    }

    @Override
    public int hashCode() {
        return Objects.hashCode(map);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RotatingEncoder == false) {
            return false;
        } else {
            RotatingEncoder compare = (RotatingEncoder) obj;
            if (map.equals(compare.map)) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public String toString() {
        Set<Map.Entry<Character, Character>> mapSet = map.entrySet();
        StringBuilder printThis = new StringBuilder();
        List<Map.Entry<Character, Character>> mapList = new ArrayList<>();
        for (char letter : alfabeList) {
            Map.Entry<Character, Character> input = new AbstractMap.SimpleEntry<>(letter, map.get(letter));
            mapList.add(input);

        }

        for (int i = 0; i < mapList.size() - 1; i++) {
            printThis.append(mapList.get(i).getKey() + "->" + mapList.get(i).getValue() + "\n");

        }
        printThis.append(mapList.get(mapList.size() - 1).getKey() + "->" + mapList.get(mapList.size() - 1).getValue());
        return printThis.toString();
    }
}