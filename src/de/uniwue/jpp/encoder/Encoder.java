package de.uniwue.jpp.encoder;

public interface Encoder {

    public char encode (char c) throws EncoderInputException;
    //	Encodiert den Buchstaben c in einen anderen Buchstaben (abhängig vom Mapping des Encoders).
    //	Wenn diesem Encoder noch Encoder nachfolgen soll der Aufruf von encode rekursiv weitergegeben werden
    //	und auch die Rückgabe "rückwärts" gemapped werden, bevor das Ergbenis zurückgegeben wird.
    //	Ist c nicht aus [a-z] so soll eine aussagekräftige EncoderInputException geworfen werden.



    public void rotate (boolean carry);
    //	Gibt an ob dieser Encoder rotieren soll. rotate wird also mit true aufgerufen,
    //	wenn der vorherige Encoder eine volle Rotation erreicht hat, oder man dieser Encoder der erste (Rotating-)Encoder ist.
}
