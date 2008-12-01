package mobile.lib;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

public class PackQueue implements Runnable{

	
	private Hashtable packList = new Hashtable();
	
	// Routes Availables
	// (String,DevicePoint)
	private Hashtable routeList = new Hashtable(); 
	
	private boolean done = false;
	
	public void init(){		
		Thread thread = new Thread(this);
		thread.start();				
	}
	
	public void stop()
	{
		done = true;
	}
	
	public synchronized void insertPack(ProtoPackage pp){
		packList.put(pp.receiver, new PackDeliver(pp));
		this.notify();
	}
	
	public synchronized void putRoute(String receiver, DevicePoint routePoint ){
		routeList.put(receiver, routePoint);
		this.notify();
	}		
	
	public void run() {

		try
		{				
			while( !done )
			{

				// check to see if there are any message to send.
				// if not, then wait for 5 second
				if( routeList.size()<=0 ){					
					synchronized (this) {
						this.wait(5000);
					}
				}
				
				if( packList.size()>0 ){
					/////////////////////////////////////////////////////////////////////////////
					// Abaixo rola o funcionamento da PC:
					//   para cada rota existente checa se existe algum pacote pendente e o envia
					Enumeration e = routeList.keys();
					while(e.hasMoreElements()){
						
						String deviceName = (String)e.nextElement();
						if( deviceName!="" && packList.containsKey(deviceName)){
							
							PackDeliver pd = (PackDeliver)packList.get(deviceName);
							DevicePoint dp = (DevicePoint)routeList.remove(deviceName);
							if( dp!=null ){							
								dp.putPacket(pd.getPack());
								Util.Log("Pacote encaminhado via PC. Destino do pacote: "+pd.getPack().receiver);
							}
							
						}					
					}
					//////////////////////////////////////////////////////////////////////////
					
					
					//////////////////////////////////////////////////////////////////////////
					//  Checa se o pacote expirou, e exclui da lista
					Enumeration ePacks = packList.keys();
					while(e.hasMoreElements()){
						PackDeliver dpCheck = (PackDeliver)packList.get( ePacks.nextElement() );
						if( dpCheck!=null ){
							//Diferença entre Agora e a data de Expiração
							long diff = new Date().getTime() - dpCheck.getExpireDate().getTime();
							if( diff>0 ){
								Util.Log("Pacote expirou: " + dpCheck.getPack().receiver );									
							}
							
						}
					}
					//////////////////////////////////////////////////////////////////////////
				}
				
				

			}			
		} 
		catch (Exception e) {
			e.printStackTrace();
			Util.Log(e.getClass().getName()+" "+e.getMessage());
		}
		Util.Log("PackQueue thread exit ");
		
	}
	
	class PackDeliver{
		private ProtoPackage pp;
		private Date expireDate;
		
		public ProtoPackage getPack(){
			return pp;
		}
		
		public Date getExpireDate(){
			return expireDate;
		}
		
		public PackDeliver(ProtoPackage protoPack){
			pp = protoPack;
			expireDate = new Date();
			expireDate.setTime( expireDate.getTime()+30000 ); //Expira em 30 segundos
		}
		
	}

}
