package mobile.lib;
import java.io.DataInputStream;

public class Reader implements Runnable
{
  // end point that this reader reads data from
  public DevicePoint endpt;

  private boolean done = false;

  public Reader() {
  }

  /**
   * set 'done' flag to true, which will exit the while loop
   */
  public void stop()
  {
    done = true;
  }

  public void run()
  {
    try
    {
      DataInputStream datain = endpt.con.openDataInputStream();

      while ( !done )
      {
        Util.Log("waiting for next signal from "+endpt.remoteName);

        byte[] dataPkt = new byte[512];
        datain.readFully(dataPkt);
        
        ProtoPackage pkt = ProtoPackage.getProtoPackage(dataPkt);
        
        if( pkt.command == Constants.CMD_HANDSHAKE){
        	
        	endpt.remoteName = pkt.sender;        	
        	Util.Log("read in HANDSHAKE from "+endpt.remoteName);
        	
        	ProtoPackage answer = new ProtoPackage(
        				Constants.APP_GENERAL,
        				Constants.CMD_HANDSHAKE_ACK,
        				pkt.receiver, //The receiver becomes a Sender
        				pkt.sender, //The sender becomes a Receiver
        				""
        			);
        	endpt.putPacket(answer);
        	
        	endpt.callback.handleAction(Constants.CMD_INITIATED, endpt, pkt);
        } 
        else if ( pkt.command == Constants.CMD_MESSAGE )
        {

          Util.Log("read in MESSAGE string '"+pkt.msg+"' from "+endpt.remoteName);

          // read in a string message. emit RECEIVED event to BTListener implementation
          endpt.callback.handleAction( Constants.EVENT_RECEIVED, endpt, pkt );

        } else if ( pkt.command == Constants.CMD_TERMINATE )
        {
        	Util.Log("read in TERMINATE from "+endpt.remoteName);

        	// echo acknowledgment back to remote device
        	ProtoPackage answer = new ProtoPackage(
				Constants.APP_GENERAL,
				Constants.CMD_TERMINATE_ACK,
				pkt.receiver, //The receiver becomes a Sender
				pkt.sender, //The sender becomes a Receiver
				"end"
			);
      	  	endpt.putPacket(answer);

          // emit LEAVE event to BTListener implementation
          endpt.callback.handleAction( Constants.EVENT_LEAVE, endpt, pkt );

          // clean up end point resources and associated connections
          endpt.btnet.cleanupRemoteEndPoint( endpt );

          // stop this reader, no need to read any more signal
          stop();

        } else if ( pkt.command == Constants.CMD_HANDSHAKE_ACK )
        {
 
          Util.Log("read in  HANDSHAKE_ACK from "+endpt.remoteName);
          // update remote user nick name
          endpt.remoteName = pkt.sender;

        } else if ( pkt.command == Constants.CMD_TERMINATE_ACK )
        {

          System.out.println("read in TERMINATE_ACK from "+pkt.sender);
          // doesn't do anything, just wake up from readInt() so that the thread can stop


        } else
        {
        	endpt.callback.handleAction(pkt.command, endpt, pkt);
          //Util.Log("Unkonwn signal, probably connection closed");
        }

      } // while !done

      datain.close();
    } catch (Exception e)
    {
      e.printStackTrace();
      Util.Log(e.getClass().getName()+" "+e.getMessage());
    }
    Util.Log("reader thread exit for "+endpt.remoteName);

  }
}