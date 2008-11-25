package com.tcc2008.services;

import java.util.Vector;

import com.tcc2008.extend.Protocol;


public class RestoreService implements Runnable {

	Vector<Protocol> repository;
	private boolean started = true;

	public RestoreService(Vector<Protocol> repository ){
		this.repository = repository;

		
		Thread t = new Thread(this);
		t.start();		
	}
	
	public void run() {
		while(started ){
			
		}
	}
	
	public void stop(){
		this.started = false;
	}

}
