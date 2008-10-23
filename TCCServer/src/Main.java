import javax.bluetooth.*;

import java.io.IOException;   
import java.util.Vector;  


public class Main {

	public static void main(String[] args){
	
		BluetoothConnection bc = new BluetoothConnection();
		if( bc.discoverDevices() )
		{
			System.out.println("Dispositivos foram encontrados.");
		}
		
		
		/////////////////////////////////////////////////////////////
		DiscoveryAgent agent = null;
		int idServico = 0;
		try {
			agent = bc.getLocalDevice().getDiscoveryAgent();
			for (int i = 0; i < bc.getDevices().size(); i++) {
				RemoteDevice remdev = (RemoteDevice) bc.getDevices().get(i).getDeviceObj();
				
				try {
					idServico = agent.searchServices(null, new UUID[]{new UUID("00000000000000001",false)},remdev, bc.getListener());
					
					System.out.println("Servico "+idServico + " do usuario "+bc.getDevices().get(i).getName());
				} catch (BluetoothStateException e) {
					e.printStackTrace();
				}
				
				if( agent!=null ){
					agent.cancelInquiry(bc.getListener());
					agent.cancelServiceSearch(idServico);
				}
			}
			
		}	
		finally{

		}
		
		
	
	}

}
