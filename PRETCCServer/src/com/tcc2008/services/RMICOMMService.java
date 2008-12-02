package com.tcc2008.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.tcc2008.extend.Protocol;


public interface RMICOMMService extends Remote {
	 
	public boolean enqueuePackage(Protocol proto )  throws RemoteException;
			
} 
