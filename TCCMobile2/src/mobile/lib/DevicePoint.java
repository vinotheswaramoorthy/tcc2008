package mobile.lib;
import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;

public class DevicePoint
{
  // remote device object
  RemoteDevice remoteDev;
  // remote device class
  DeviceClass remoteClass;
  // remote service URL
  String remoteUrl;
  // connection to remote service
  StreamConnection con;
  // bluetooth discovery transId, obtainsed from searchServices
  int transId = -1; // -1 must be used for default. cannot use 0

  // sender thread
  Sender sender;
  // reader thread
  Reader reader;

  // local user nick name
  String localName;
  // remote user nick name
  public String remoteName;
  
  private String nickname;
  
  public String getNickname(){
	  return nickname;
  }
  public void setNickname(String nick){
	  //se estiver vazio vai manter o BTAddress
	  if( nick!="") nickname = nick;
  }

  // BTListener implementation for callback GeneralServer event
  BTListener callback;

  // reference to GeneralServer
  GeneralServer btnet;

  // vector of ProtoPacket pending to be sent to remote service.
  // when message is sent, it is removed from the vector.
  Vector packages = new Vector();

  public DevicePoint( GeneralServer btnet, RemoteDevice rdev, StreamConnection c )
  {
    this.btnet = btnet;

    remoteDev = rdev;

    try {
      // NOTE in 6600, this parameter must be false because
      // according to some observation from other developer
      // setting this to true mean the Bluetooth system need to make
      // another connection to remote device, however, there is no available
      // free connection, so it will give you exception
      //remoteName = rdev.getFriendlyName(false); // this is a temp name
    	remoteName = rdev.getBluetoothAddress();
    	nickname = "n:"+remoteName;
    }
    catch (Exception ex) {
      remoteName = "Unknown";
      // ignore
    }
    localName = btnet.localName;
    callback = btnet.callback;
    con = c;

    sender = new Sender();
    sender.endpt = this;

    reader = new Reader();
    reader.endpt = this;


  }

//  public synchronized void putString( int signal, String s )
//  {
//    Util.Log("invoke putString "+signal+" "+s);
//    // put the message on the queue, pending to be sent by Sender thread
//    packages.addElement( new ProtoPackage( signal, s ) );
//    synchronized( sender )
//    {
//      // tell sender that there is a message pending to be sent
//      sender.notify();
//    }
//  }
  
  public synchronized void putPacket(ProtoPackage pp){
	  
	  //enqueue the protocol packet.
	  packages.addElement(pp);
	  
	  synchronized( sender ){
		// tell sender that there is a message pending to be sent
		  sender.notify();
	  }
  }

  public synchronized ProtoPackage getPacket()
  {
//    log("invoke getString()");
    if ( packages.size() > 0 )
    {
    	// if there are message pending, return it and remove it from the vector
    	ProtoPackage pp = (ProtoPackage) packages.firstElement();
    	packages.removeElementAt(0);
    	return pp;
    } else
    {
      // if there is no message pending. return null
      return null;
    }
  }

  public synchronized boolean peekPacket()
  {
    return ( packages.size() > 0 );
  }

}