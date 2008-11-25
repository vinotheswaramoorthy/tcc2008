package com.tcc2008.services;

import java.util.Vector;

import com.tcc2008.extend.Dictionary;
import com.tcc2008.extend.Protocol;
import com.tcc2008.extend.Utility;

public class PackageRXService implements Runnable{
	Vector<byte[]> buffer;
	Vector<Protocol> repositoryRX;
	
	
	public PackageRXService(Vector<byte[]> buffer, Vector<Protocol> repositoryRX){
		this.buffer = buffer;	
		this.repositoryRX = repositoryRX;
		
		Thread t = new Thread(this); 
		t.start();
	}

	public void run() {
		while(true)
		{
			if(buffer.isEmpty()) continue;
						
			byte[] data = buffer.remove(0);
			
			String message = new String(data);
			Utility.Log("Receive Package: " + message);
			
			int index = 0;
			while(data.length > index)
			{				
				if(data[index] == Dictionary.SOH && data.length > (index+1) && data[index+1] == Dictionary.STX)
				{ 
					index+=2;
					//Encontrar o tamanho do DATA
					// 16 IDOrigem/IDDestino/IDAplicacao = 48  + 1 PER + 1 CMD 
					int nChar = ((int) data[index+50]) + data[index+51];
				
					// Avançar para encontrar o EOT
					// 2 NCHAR  1 ETX  1 BCC  
					if(data[index+nChar+54] != Dictionary.EOT) continue;
					
					//Falta testar o BCC 
					/* TESTE DO BCC AQUI */
					
					
					String idFrom 	= new String(Utility.wrap(data, index, 16));
					String idTo 	= new String(Utility.wrap(data, index+=16, 16));
					String idApp 	= new String(Utility.wrap(data, index+=16, 16));
					boolean isPersisted = ((int) data[index+=16]) != 0 ;
					byte cmd		= data[++index];
					
					//coloca na posição do data
					index+=3;
					
					Protocol proto = new Protocol();
					proto.setIDFrom(idFrom);
					proto.setIDTo(idTo);
					proto.setIDApp(idApp);
					proto.setPersisted(isPersisted);
					proto.setCommand(cmd);
					proto.setData(Utility.wrap(data, index, nChar));
					
					// 1 ETX  1 BCC  1 EOT
					index += nChar + 3;
					if (data[index-1] == Dictionary.EOT) 
						{
							repositoryRX.add(proto);
							Utility.Log("Add Repository: " + proto);
						}
				}
				else index++;
			}
			
			try {Thread.sleep(5000);} 
			catch (InterruptedException e) {e.printStackTrace();}
		}
	}



}
