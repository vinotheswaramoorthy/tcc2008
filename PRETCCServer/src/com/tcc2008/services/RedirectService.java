package com.tcc2008.services;

import java.rmi.Naming;
import java.util.Vector;

import com.tcc2008.extend.Dictionary;
import com.tcc2008.extend.Protocol;
import com.tcc2008.extend.Utility;
import com.tcc2008.webservice.MasterReference;


public class RedirectService implements Runnable {

	Vector<Protocol> queueRX; 
	Vector<Protocol> queueTX; 
	Vector<Protocol> queueUpdate; 
	Vector<Protocol> repository;
	private boolean started = true;
	private String serverName = "";
	
	
	public RedirectService(String serverName, Vector<Protocol> queueRX, Vector<Protocol> queueTX, Vector<Protocol> queueUpdate, Vector<Protocol> repository){
		this.queueRX = queueRX;
		this.queueTX = queueTX;
		this.queueUpdate = queueUpdate;
		this.repository = repository;
		this.serverName = serverName;
		
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() { 
		
		while(started){
			
			// Verifica se há pacotes na Fila  
			if(queueRX.size() <= 0){	
				try { Thread.sleep(1000);}catch (Exception e) { Utility.Log(e.toString());}
				continue;
			}
			
			Protocol proto = queueRX.remove(0);
			
			// Se é uma resposta de UpdateLocation, manda para a fila de update
			if(proto.getCommand()== Dictionary.CMD_UPDATELOCAL){
				if(sendToUpdate(proto))
					Utility.Log("SENT TO 'UPDATE QUEUE': " + proto.toString());
				else Utility.Log("FAILED SEND TO 'UPDATE QUEUE': " + proto.toString());
			}			
			// Se é um pacote de envio   
			else if(proto.getCommand()== Dictionary.CMD_SEND){
				
				// Verifica se o ID de Origem é válido
				if(!MasterReference.checkUID(proto.getIDFrom().toString()))
				{
					Utility.Log("INVALID 'IDFROM': "+ proto.getIDFrom() );
					// Posso colocar uma resposta de invalid ID
					continue;
				}
				else Utility.Log("VALIDATED 'IDFROM': "+ proto.getIDFrom() );
				
				String serverDest = MasterReference.getServerDestination(proto.getIDTo().toString(), proto.getIDApp());
				
				if(isThis(serverDest))
				{
					if(sendToMobile(proto))
						Utility.Log("SENT TO 'SEND QUEUE': "+ serverDest+" PKG:" + proto.toString());
					else Utility.Log("FAILED SEND TO 'SEND QUEUE': "+ serverDest+" PKG:" + proto.toString());
				
				}
				else if(serverDest != "")
				{
					if(sendToServer(proto, serverDest))
						Utility.Log("SENT TO SERVER: "+ serverDest+" PKG:" + proto.toString());
					else Utility.Log("FAILED SEND TO SERVER: "+ serverDest+" PKG:" + proto.toString());
				
				}
				else if(proto.isPersisted())
				{					
					if(sendToRepository(proto))
						Utility.Log("SENT TO REPOSITORY: " + proto.toString());
					else Utility.Log("FAILED SEND TO REPOSITORY: " + proto.toString());
				}
			
			}			
			
			try { Thread.sleep(1000);}catch (Exception e) { Utility.Log(e.getMessage());}
		}
		
	}
	
	private boolean isThis(String serverName) {
		return this.serverName == serverName;
	}

	public void stop(){
		started = false;
	}
	
	public boolean sendToServer(Protocol protocol, String server){
		String remoteName = "rmi://" + server + "/REDIRECTSERVERSCOMM";
		
		try {
			RMICOMMService commService =
				( RMICOMMService ) Naming.lookup( remoteName);					
			
			return commService.enqueuePackage(protocol);
			
		} catch (Exception e) {	Utility.Log(e.getMessage()); }
		
		return false;
	}
	
	public boolean sendToRepository(Protocol protocol){
		return repository.add(protocol);
	}
	
	public boolean sendToMobile(Protocol protocol){
		return queueTX.add(protocol);
	}
	
	public boolean sendToUpdate(Protocol protocol){
		return queueUpdate.add(protocol);
	}
	

}
