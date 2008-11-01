package mobile.lib;
import java.io.DataOutputStream;
import java.io.IOException;


public class Sender implements Runnable {

	public DevicePoint endpt;

	private boolean done = false;
	public Sender()
	{
	}

	/**
	 * set 'done' flag to true, which will exit the while loop
	 */
	public void stop()
	{
		done = true;
	}
	public void run() {
		try
		{
			DataOutputStream dataout = endpt.con.openDataOutputStream();
			while( !done )
			{

				// check to see if there are any message to send.
				// if not, then wait for 5 second
				if ( ! endpt.peekPacket()  )
				{
					synchronized (this) {
						this.wait(5000);
					}
				}

				// wake up and get next string
				ProtoPackage s = endpt.getPacket();

				if ( s != null )
				{
					// if there is a message to send, send it now
					Util.Log("sending signal "+ Util.unsignedByteToInt(s.application)+"|| string '"+s.msg+"' to "+endpt.remoteName);
					
					dataout.write(s.getBytes());									
					/*dataout.writeInt(s.signal);
					dataout.writeUTF(s.msg );
					dataout.flush();*/
					dataout.flush();
					Util.Log("Sender: package sended.");
				}

				if ( s != null && s.command == Constants.CMD_TERMINATE )
				{
					// if the message is a TERMINATE signal, then break the run loop as well
					stop();
				}

			} // while !done

			// close the output stream
			dataout.close();
		} catch (IOException e)
		{
			e.printStackTrace();
			Util.Log(e.getClass().getName()+" "+e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			Util.Log(e.getClass().getName()+" "+e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			Util.Log(e.getClass().getName()+" "+e.getMessage());
		}
		Util.Log("sender thread exit for "+endpt.remoteName);


	}

}
