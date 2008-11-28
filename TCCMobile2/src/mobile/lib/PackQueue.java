package mobile.lib;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;

public class PackQueue implements Runnable{

	
	private Hashtable packList = new Hashtable();
	
	// Routes Availables
	// (String,String)
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
	
	//public synchronized void putRoute(String device )
	
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
	
	class RouteItem{
		
		public RouteItem(){
			
		}
		
	}

}
