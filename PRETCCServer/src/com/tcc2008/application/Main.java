package com.tcc2008.application;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Vector;

import com.tcc2008.extend.*;
import com.tcc2008.services.*;



public class Main {

	private static String serverName = "Emerson";

	public static void main(String[] args) 
	{
		/****************************************************************/
		
		// Fila de Pacotes recebidos, esperando para ser redirecionado
		Vector<Protocol> queueRX = new Vector<Protocol>();
		
		// Fila de Pacotes p/ envio, esperando para ser enviados
		Vector<Protocol> queueTX = new Vector<Protocol>();
		 
		// Fila de Pacotes de Atualização
		Vector<Protocol> queueUpdate = new Vector<Protocol>();
		
		// Repositório de Pacotes inacessiveis
		Vector<Protocol> repository = new Vector<Protocol>();
		
		/****************************************************************/
		
		BTRFCOMMService serviceRecptor = new BTRFCOMMService();
		serviceRecptor.startServer();
		
		PackageRXService rxService = new PackageRXService(serviceRecptor.buffer , queueRX);
		PackageTXService txService = new PackageTXService(queueTX, serviceRecptor);
		RedirectService redirectService = new RedirectService(serverName, queueRX, queueTX, queueUpdate, repository);
		UpdateService updateService = new UpdateService(serverName , queueUpdate, queueTX);
		RestoreService restoreService = new RestoreService(repository, queueRX);
		
		
		/******************************************************************/
		/*  Iniciando o Serviço de comunicação RMI entre os servidores    */
		/******************************************************************/

//		try {
//			ServerCOMM serviceCOMM = new ServerCOMM(queueRX);
//			String rmiObjectName = "rmi://localhost/REDIRECTSERVERSCOMM";
//			Naming.rebind(rmiObjectName, serviceCOMM);
//			
//			Utility.Log("THE RMI SERVICE OF SERVERS' COMMUNICATION INITIALIZED...  ");
//		} catch (RemoteException e) {
//			Utility.Log("CONNECTION FAILED:\n" +e.getMessage());
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			Utility.Log("URL RMISERVER MALFORMED: " + e.getMessage());
//		} 		 
//		
		/******************************************************************/
		
		
	}

}
