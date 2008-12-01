package com.tcc2008.services;


import java.rmi.Naming;
import java.util.UUID;

import net.java.dev.marge.factory.CommunicationFactory;
import net.java.dev.marge.factory.RFCOMMCommunicationFactory;

import com.tcc2008.extend.Protocol;
import com.tcc2008.webservice.MasterReference;

public class ClientCOMM {

	public ClientCOMM(){
		super();
		
		//String remoteName = "rmi://" + server + "/COMMService";
		String remoteName = "rmi://localhost/REDIRECTSERVERSCOMM";
		
		try {
			RMICOMMService commService =
				( RMICOMMService ) Naming.lookup( remoteName);
		
			Protocol proto = new Protocol();
			proto.setData("TESTE".getBytes());
			
			if((boolean) commService.enqueuePackage(proto))
				System.out.println("RETORNO OK...");
			else System.out.println("RETORNO FALHOU...");
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ClientCOMM client = new ClientCOMM();
	}

}