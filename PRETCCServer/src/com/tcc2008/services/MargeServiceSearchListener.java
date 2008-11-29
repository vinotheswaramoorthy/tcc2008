package com.tcc2008.services;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import net.java.dev.marge.inquiry.ServiceSearchListener;

import com.tcc2008.extend.Utility;

public class MargeServiceSearchListener implements ServiceSearchListener {




	public void deviceNotReachable() {
		Utility.Log("deviceNotReachable...");		
	}


	public void serviceSearchCompleted(RemoteDevice remoteDevice,
			ServiceRecord[] services) {
		Utility.Log("Encontrado " + services.length+ " serviços.");
		for (int i = 0; i < services.length; i++) {
			Utility.Log(remoteDevice.getBluetoothAddress()+"\t"+services[i].toString());
		}	
	}


	public void serviceSearchError() {
		Utility.Log("serviceSearchError...");
	}
	
}
