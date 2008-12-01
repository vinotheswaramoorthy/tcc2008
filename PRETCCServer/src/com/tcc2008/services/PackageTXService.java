package com.tcc2008.services;

import java.util.UUID;
import java.util.Vector;

import com.tcc2008.extend.Dictionary;
import com.tcc2008.extend.Protocol;
import com.tcc2008.extend.Utility;

public class PackageTXService implements Runnable{
	
	private boolean started = true;
	private BTRFCOMMService serviceRecptor;
	private Vector<Protocol> queueTX;

	public PackageTXService(Vector<Protocol> queueTX, BTRFCOMMService serviceRecptor){
		this.queueTX = queueTX;
		this.serviceRecptor = serviceRecptor;
		
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() {
		while(started )
		{
			if (serviceRecptor.getDevice() != null) {
			
				while(queueTX.size() > 0)
				{
					Protocol proto = queueTX.remove(0);
					// 2 soh/stx + 48 ids + 1 pers+ 1 cmd + 2nchar + N data+ 1 Etx + 1 bcc + 1 eot
					int pkgSize = 57 + proto.getData().length; 
					
					byte[] pkg = new byte[pkgSize];
					
					pkg[0] = (byte) Dictionary.SOH;
					pkg[1] = (byte) Dictionary.STX;
					
					byte[] idFrom = new byte[16];
					proto.getIDFrom()					;
					
					
					for(int i=0;i<16;i++)
					{
						pkg[i+2] = idFrom[i];
					}
					
					byte[] idTo = proto.getIDTo().toString().getBytes();
					for(int i=0;i<16;i++)
					{
						pkg[i+18] = idTo[i];
					}
					
					byte[] idApp = proto.getIDApp().getBytes();
					for(int i=0;i<16;i++)
					{
						pkg[i+34] = idApp[i];
					}
					
					int ndata = proto.getData().length;
					
					pkg[50] = (byte)(proto.isPersisted()?1:0);
					pkg[51] = (byte) proto.getCommand();
					pkg[52] = (byte) (ndata/256);
					pkg[53] = (byte) (ndata%256);
					
					for(int i=0;i<ndata;i++)
			    	{
			    		pkg[i+54] = (byte)proto.getData()[i];
			    	}
			    	
					pkg[ndata+54] = 0x03; 	//ETX
			    	pkg[ndata+55] = 0;		//BCC
			    	pkg[ndata+56] = 0x04; 	//EOT
			    
			    	// Envia pro dispositivos
			    	serviceRecptor.getDevice().send(pkg);
					
				}
			}
			try { Thread.sleep(10000);}catch (Exception e) { Utility.Log(e.toString());}
		}
	}
	

	public void stop(){
		started = false;
	}

}
