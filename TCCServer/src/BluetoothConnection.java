import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import java.io.IOException;   
import java.util.Vector;  
		
public class BluetoothConnection implements IConnection {

	private Vector<MobileDevice> mDevices = new Vector<MobileDevice>();
	
	private Object inquiryCompletedEvent = new Object();
	
	private LocalDevice localDevice = null;
	
    DiscoveryListener listener = new DiscoveryListener() {   
    	  
        public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {   
        	
        	MobileDevice md = new MobileDevice();
        	md.setDeviceObj(btDevice);
        	md.setAddress(btDevice.getBluetoothAddress());
        	try {
				md.setName(btDevice.getFriendlyName(false));
			} catch (IOException e) {
				md.setName("(noname)");
			}        	
            
        	System.out.println("Dispositivo " + md.getName() + " encontrado");   
            
        	mDevices.addElement(md);                   
        }   

        public void inquiryCompleted(int discType) {   
            System.out.println("Device Inquiry completed!");   
            synchronized(inquiryCompletedEvent){   
                inquiryCompletedEvent.notifyAll();   
            }   
        }   

        public void serviceSearchCompleted(int transID, int respCode) {   
        }   

        public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {        
			if (servRecord.length > 0) {
				String url = servRecord[0].getConnectionURL(
						ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
				try {
					RemoteDevice dev = servRecord[0].getHostDevice();
					String nome;
					if (dev != null) {
						nome = dev.getFriendlyName(false);
						StreamConnection conn = (StreamConnection) Connector.open(url);
						System.out.println("foi conectado...");
					}
				} catch (IOException e) {
					e.printStackTrace();		
				}
			}
        	
        }   
    };

    
	public boolean discoverDevices() {
		
		boolean discovered = false;
		
		
		synchronized (inquiryCompletedEvent) {				
			boolean started = false;
			try {
				localDevice = LocalDevice.getLocalDevice();
				started = localDevice.getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
			} catch (BluetoothStateException e) {
				System.out.println("Bluetooth Connection Error..."+e.getMessage());
			}   
	        if (started) {   
	            System.out.println("wait for device inquiry to complete...");   
	            try {
					inquiryCompletedEvent.wait();
					discovered = true;
				} catch (InterruptedException e) { 
					e.printStackTrace();
				}   
	            System.out.println(mDevices.size() +  " device(s) found");	            
	        }
		}
		
		return discovered;
	}

	public DiscoveryListener getListener(){
		return listener;
	}
	
	public Vector<MobileDevice> getDevices() {
		return mDevices;
	} 
	
	public LocalDevice getLocalDevice(){
		return localDevice;
	}


	
}
