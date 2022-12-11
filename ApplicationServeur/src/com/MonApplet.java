package com;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;

public class MonApplet extends Applet {
	
	
	public static final byte CLA_MONAPPLET = (byte) 0xB0;
	public static final byte INS_DEBITER_COMPTEUR = 0x00;
	public static final byte INS_INTERROGER_COMPTEUR = 0x01; 
	public static final byte INS_AJOUTER_COMPTEUR = 0x02;
	public static final byte INS_ECRASER_COMPTEUR = 0x03;
	/* Attributs */
	private byte solde;
	
	private MonApplet() {
		solde = 50;
	}

	public static void install(byte bArray[], short bOffset, byte bLength) throws ISOException {
		new MonApplet().register();
	}

	public void process(APDU apdu) throws ISOException {
		byte[] buffer = apdu.getBuffer();
		if (this.selectingApplet()) return;
		if (buffer[ISO7816.OFFSET_CLA] != CLA_MONAPPLET) {
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
		}else {
			
		}
		switch (buffer[ISO7816.OFFSET_INS]) {
		
		case INS_INTERROGER_COMPTEUR:

			buffer[0] = solde;
			apdu.setOutgoingAndSend((short) 0, (short) 1);
			break;
			
		case INS_DEBITER_COMPTEUR:
			apdu.setIncomingAndReceive();
			byte montant = buffer[ISO7816.OFFSET_CDATA];
			solde -= montant;
			buffer[0] = solde;
			apdu.setOutgoingAndSend((short) 0, (short) 1);
			break;
			
		case INS_AJOUTER_COMPTEUR:

			apdu.setIncomingAndReceive();
			byte montant2 = buffer[ISO7816.OFFSET_CDATA];
			solde += montant2;
			buffer[0] = solde;
			apdu.setOutgoingAndSend((short) 0, (short) 1);
			break;
			
		case INS_ECRASER_COMPTEUR:

			apdu.setIncomingAndReceive();
			solde = buffer[ISO7816.OFFSET_CDATA];
			buffer[0] = solde;
			apdu.setOutgoingAndSend((short) 0, (short) 1);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		} 
	}
}
