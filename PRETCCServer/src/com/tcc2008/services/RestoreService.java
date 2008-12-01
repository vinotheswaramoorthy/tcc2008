package com.tcc2008.services;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import com.tcc2008.extend.Utility;
import com.tcc2008.extend.Protocol;
import com.tcc2008.webservice.MasterReference;


public class RestoreService implements Runnable {

	private Vector<Protocol> repository;
	private Vector<Protocol> queueRX;
	private boolean started = true;

	public RestoreService(Vector<Protocol> repository , Vector<Protocol> queueRX){
		this.repository = repository;
		this.queueRX = queueRX;
		
		Thread t = new Thread(this);
		t.start();		
	}
	
	public void run() {
		while(started ){
			
			Vector<Protocol> list = new Vector<Protocol>();
			while (repository.size() > 0) {
			
				Protocol proto = repository.remove(0);
				if( MasterReference.getServerDestination(proto.getIDTo().toString(), proto.getIDApp()) != "")
					queueRX.add(proto);
				else list.add(proto);
			}
				
			repository.addAll(list);
		
			
			try {Thread.sleep(360000);} catch (InterruptedException e) { Utility.Log(e.getMessage()); }
		}
	}
	
	public void stop(){
		this.started = false;
	}

}