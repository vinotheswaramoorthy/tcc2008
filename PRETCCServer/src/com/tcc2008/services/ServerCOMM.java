package com.tcc2008.services;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import com.tcc2008.extend.Protocol;
import com.tcc2008.extend.Utility;


public class ServerCOMM extends UnicastRemoteObject implements RMICOMMService{

	Vector<Protocol> queueRX;
	
	public ServerCOMM(Vector<Protocol> queueRX) throws RemoteException {
		super();
		
		this.queueRX = queueRX;
	}


	public boolean enqueuePackage(Protocol proto) throws RemoteException{
		Utility.Log("ENQUEUE RX:\n"+proto);
		return queueRX.add(proto);
	}
	
}
