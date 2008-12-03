package com.tcc2008.services;

import java.util.Vector;

import com.tcc2008.extend.Dictionary;
import com.tcc2008.extend.Protocol;
import com.tcc2008.extend.UUID;
import com.tcc2008.extend.Utility;
import com.tcc2008.webservice.MasterReference;


public class UpdateService implements Runnable{

	private String serverName = "";
	private Vector<Protocol> queueUpdate;
	private Vector<Protocol> queueTX;
	private boolean started = true;

	public UpdateService(String serverName, Vector<Protocol> queueUpdate, Vector<Protocol> queueTX){
		this.serverName = serverName;
		this.queueUpdate = queueUpdate;
		this.queueTX = queueTX;
		
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() {
		
		while(started){
		
			while(queueUpdate.size() > 0){
				Protocol proto = queueUpdate.remove(0);
				if(!proto.getIDFrom().toString().equals("00000000-0000-0000-0000-000000000000"))
				{
					if(MasterReference.updateLocation(proto.getIDFrom().toString(), proto.getIDApp().toString(), serverName))
					{
						Utility.Log("LOCATION UPDATE:\t"+proto.getIDFrom()+"["+ new String(proto.getData())+"] TO "+ proto.getIDApp()+" IN "+serverName  );
					}
					else Utility.Log("FAILED LOCATION UPDATE :\t"+proto.getIDFrom()+" / "+ proto.getIDApp()+" IN "+serverName  );
				}
				else Utility.Log("FAILED LOCATION UPDATE :\t"+proto.getIDFrom()+" / "+ proto.getIDApp()+" IN "+serverName  );
			}
			
			//sendBroadcast();
			
			try {Thread.sleep(60000);} catch (InterruptedException e) { Utility.Log(e.getMessage()); }
		}
		
	}
	
	public void stop(){
		this.started = false;
	}
	
	private void sendBroadcast(){
		Protocol proto = new Protocol();		
		proto.setCommand(Dictionary.CMD_UPDATELOCAL);
		queueTX.add(proto);
	}

}
