package com.tcc2008.services;

import java.util.Vector;

import com.tcc2008.extend.Dictionary;
import com.tcc2008.extend.Protocol;


public class UpdateService implements Runnable{

	Vector<Protocol> queueUpdate;
	Vector<Protocol> queueTX;
	private boolean started = true;

	public UpdateService(Vector<Protocol> queueUpdate, Vector<Protocol> queueTX){
		this.queueUpdate = queueUpdate;
		this.queueTX = queueTX;
		
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() {
		
		while(started){
			
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
