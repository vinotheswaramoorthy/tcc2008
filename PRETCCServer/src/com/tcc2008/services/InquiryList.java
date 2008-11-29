package com.tcc2008.services;


import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import net.java.dev.marge.entity.ServerDevice;
import net.java.dev.marge.factory.CommunicationFactory;
import net.java.dev.marge.inquiry.DeviceDiscoverer;
import net.java.dev.marge.inquiry.InquiryListener;
import net.java.dev.marge.inquiry.ServiceSearchListener;

import com.tcc2008.extend.Utility;

public class InquiryList implements ServiceSearchListener, InquiryListener {
    private Vector devices;
    private CommunicationFactory factory;

    public InquiryList(CommunicationFactory factory) {
        this.factory = factory;
        this.devices = new Vector();
        
    }

	
	public void deviceNotReachable() {
		Utility.Log("NOT REARCHABLE");
	}

	public void serviceSearchCompleted(RemoteDevice remote, ServiceRecord[] servicos) {
		Utility.Log("Device: "+ remote.getBluetoothAddress());
		for (int i = 0; i < servicos.length; i++) {
			Utility.Log("Service: "+ servicos[i].toString());
		}
	}

	public void serviceSearchError() {
		Utility.Log("Search error");
		
	}

	public void deviceDiscovered(RemoteDevice remote, DeviceClass device) {
		Utility.Log("Encontrei: " + remote.getBluetoothAddress());
		this.devices.addElement(remote);
	}

	public void inquiryCompleted(RemoteDevice[] devices) {
		
		for (int i = 0; i < devices.length; i++) {
			Utility.Log(devices[i].getBluetoothAddress());
		}
		
	}

	public void inquiryError() {
		Utility.Log("ERRO inquiryError");
		
	}



	public void connectionEstablished(ServerDevice arg0, RemoteDevice arg1) {
		Utility.Log("ESTABILISHED");
	}


	

   
//    public void serviceSearchCompleted(RemoteDevice remoteDevice,
//            ServiceRecord[] services) {
//        try {
//            ClientConfiguration config = new ClientConfiguration(services[0],
//                    this.mainMenu.getChatForm());
//            ClientDevice clientDevice = this.factory.connectToServer(config);
//           
//        } catch (IOException e) {
//            Utility.Log(e.getMessage());
//        }
//    }
//
//    public void serviceSearchError() {
//        MobileTester.getInstance().setCurrent(new Alert("Error", "Service search error", null, AlertType.ERROR), this);
//    }
//
//    public void deviceDiscovered(RemoteDevice device, DeviceClass deviceClass) {
//        if (this.select == null) {
//            this.select = new Command("Selec", Command.OK, 1);
//            this.addCommand(select);
//        }
//
//        this.devices.addElement(device);
//        this.setTitle("Buscando... " + this.devices.size());
//        try {
//            this.append(device.getFriendlyName(false), null);
//        } catch (IOException e) {
//            this.append(device.getBluetoothAddress(), null);
//            e.printStackTrace();
//        }
//    }
//
//    public void inquiryCompleted(RemoteDevice[] devices) {
//       
//    }
//
//    public void inquiryError() {
//        
//    }
}
