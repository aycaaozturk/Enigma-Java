package de.uniwue.jpp.encoder;

public abstract class AbstractChainableEncoder implements Encoder {
	//HASHMAP KULLLAN
    public Encoder delegate;

	public AbstractChainableEncoder(Encoder delegate) throws EncoderCreationException {
//		Initialisiert einen abstrakten Encoder dem sein Nachfolger delegate angegeben wird. Behandeln Sie folgende Ausnahmen:
//
//		delegate hat den Wert null: Werfen Sie eine aussagekräftige EncoderCreationException.
		if(delegate==null){
			throw new EncoderCreationException("delegate is null! incorrect! (AbstractChainableEncoder)");
		}
		this.delegate=delegate;



	}

	public Encoder getDelegate() {
	//	Liefert den zugewiesenen Nachfolger zurück.
		return delegate;

		//kendinden sonraki sifreleyici tipini veriyor
		//ama bu tip de zaten bir encoder tipi. o yüzden bir obje yani
		//döndügü deger aslinda bu bir sonraki encoder'in referansi, bize bir obje dönüyor, ve bu objenin de
		// encode() gibi metotlari mevcut

		//bunun adi: dynamic binding
		//Java'da dynamic binding (dinamik bağlama), program çalışırken hangi metodun çağrılacağına karar verilmesi işlemidir.
		// Bu mekanizma, özellikle polimorfizm ve geçersiz kılma (method overriding) ile ilişkilidir.
	}
}