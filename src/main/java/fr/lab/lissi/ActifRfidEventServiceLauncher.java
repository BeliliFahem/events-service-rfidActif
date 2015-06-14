package fr.lab.lissi;

import fr.lab.lissi.eco.EcoImpl;
import fr.lab.lissi.model.device.rfid.ActifRFID;


public class ActifRfidEventServiceLauncher {

	public static void main(String[] args) {

// testPostVariable();
		System.out.println("INFO : Start ActifRFID");
		Thread t = new Thread(ActifRFID.getInstance());
		

		ActifRFID.getInstance().addObserver(new EcoImpl());
		
		System.out.println("INFO : add EcoImpl as Observer to ActifRFID OK");

		System.out.println("INFO : " + ActifRFID.getInstance().getClass().getSimpleName() + " has "
				+ ActifRFID.getInstance().countObservers() + " observers");
		
		t.start();
	}

}
