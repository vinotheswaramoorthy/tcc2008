package mobile.lib;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class PackQueue implements Runnable{

	
	private Hashtable packList = new Hashtable();
	
//	private Hashtable routeList = new 
	
	private boolean done = false;
	
	public void stop()
	{
		done = true;
	}
	
	public void run() {

		try
		{				
			while( !done )
			{

				// check to see if there are any message to send.
				// if not, then wait for 5 second
//				if ( ! endpt.peekPacket()  )
//				{
//					synchronized (this) {
//						this.wait(5000);
//					}
//				}

			}			
		} 
		catch (Exception e) {
			e.printStackTrace();
			Util.Log(e.getClass().getName()+" "+e.getMessage());
		}
		Util.Log("PackQueue thread exit ");
		
	}

}
