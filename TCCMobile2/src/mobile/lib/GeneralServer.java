package mobile.lib;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import com.sun.kvem.jsr082.impl.Utilites;

public class GeneralServer implements Runnable
{
  // major service class as SERVICE_TELEPHONY
  private final static int SERVICE_TELEPHONY = 0x400000;

  // reference to local bluetooth device singleton
  LocalDevice localDevice = null;
  // reference to local discovery agent singleton
  DiscoveryAgent agent = null;
  // local BlueChat service server object
  StreamConnectionNotifier server;
  // reference to BListener implementation. for BlueChat event callback
  BTListener callback = null;

  
  boolean done = false;

  public String localName = "";

  // list of active DevicePoints. all messages will be sent to all
  // active DevicePoints
  Vector endPoints = new Vector();
  public Vector getEndPoints(){
	  return endPoints;
  }

  // list of pending DevicePoints. this is used to keep track of
  // discovered devices waiting for service discovery. When all the near-by
  // Application service has been discovered, this list will be cleared until the
  // next inquiry
  Vector pendingEndPoints = new Vector();


  // map ServiceRecord to EndPoint
  // see DoServiceDiscovery and serviceSearchCompleted
  Hashtable serviceRecordToEndPoint = new Hashtable();

  // synchronization lock
  // see DoServiceDiscovery and serviceSearchCompleted
  Object lock = new Object();

  // timer to schedule task to do service discovery
  // see inquiryCompleted
  Timer timer = new Timer();
  
  //object to handle the packs using Communication Bridge (Ponte de Comunicação)
  PackQueue commBridge = new PackQueue();
  
  public void insertRoute(String deviceName, DevicePoint endPoint){
	  commBridge.putRoute(deviceName, endPoint);
  }

  public GeneralServer()
  {
  }

  public void init(String name, BTListener callback)
  {
    Util.Log( "invoke init()" );
    try {
      this.localName = name;
      this.callback = callback;

      //
      // initialize the JABWT stack
      localDevice = LocalDevice.getLocalDevice(); // obtain reference to singleton
      localDevice.setDiscoverable(DiscoveryAgent.GIAC); // set Discover mode to GIAC
      agent = localDevice.getDiscoveryAgent(); // obtain reference to singleton

      if( localName == "")
    	  this.localName = localDevice.getBluetoothAddress();
      // print local device information
      //Util.printLocalDevice( localDevice );


      // start bluetooth server socket
      // see run() for implementation of local BlueChat service
      Thread thread = new Thread( this );
      thread.start();

      //Starting Communication Bridge (PC)
      commBridge.init();

    }
    catch (BluetoothStateException e) {
      e.printStackTrace();
      Util.Log(e.getClass().getName()+" "+e.getMessage());

    }
    catch (Exception e) {
      e.printStackTrace();
      Util.Log(e.getClass().getName()+" "+e.getMessage());

    }
  }

  public void disconnect()
  {
    Util.Log("invoke disconnect()");

    commBridge.stop();
    
    // stop server socket, not longer accept client connection
    done = true;
    try {
      // this close will interrupt server.acceptAndOpen()
      // wake it up to exit
      server.close();
    }
    catch (IOException ex) {
    }

    // stop each EndPoint reader and sender threads
    // and send TERMINATE signal to other connected
    // BlueChat peers
    for ( int i=0; i < endPoints.size(); i++ )
    {
      DevicePoint endpt = (DevicePoint) endPoints.elementAt( i );

      ProtoPackage pkt = new ProtoPackage(
    		  Constants.APP_GENERAL, 
    		  Constants.CMD_TERMINATE,
    		  this.localName,
    		  "",
    		  "end");
      
      
      
      endpt.putPacket(pkt);
      endpt.sender.stop();
      endpt.reader.stop();

    }
  }

  public void query()
  {
    try {
      Util.Log("invoke query()");
      // although JSR-82 provides the ability to lookup
      // cached and preknown devices, we intentionally by-pass
      // them and go to discovery mode directly.
      // this allow us to retrieve the latest active BlueChat parties
      agent.startInquiry(DiscoveryAgent.GIAC, new Listener());
    }
    catch (BluetoothStateException e)
    {
      e.printStackTrace();
      Util.Log(e.getClass().getName()+" "+e.getMessage());

    }
  }


  public DevicePoint findEndPointByRemoteDevice( RemoteDevice rdev )
  {
    for ( int i=0; i < endPoints.size(); i++ )
    {
      DevicePoint endpt = (DevicePoint) endPoints.elementAt( i );
      if ( endpt.remoteName.equalsIgnoreCase( rdev.getBluetoothAddress() ) )
      {
        return endpt;
      }
    }
    return null; // not found, return null
  }

  public DevicePoint findEndPointByTransId( int id )
  {
    for ( int i=0; i < pendingEndPoints.size(); i++ )
    {
      DevicePoint endpt = (DevicePoint) pendingEndPoints.elementAt( i );
      if ( endpt.transId == id )
      {
        return endpt;
      }
    }
    return null; // not found, return null
  }
  
  public void sendPacket(ProtoPackage pp){
	    Util.Log("invoke sendString string="+pp.getMsg());
	    for ( int i=0; i < endPoints.size(); i++ )
	    {	      
	      DevicePoint endpt = (DevicePoint) endPoints.elementAt( i );
	      // put the string on EndPoint, so sender will send the message
	      pp.receiver = endpt.remoteName;
	      Util.Log("Sending broadcast packet from="+pp.sender+"; to="+pp.receiver);
	      endpt.putPacket( pp );
	    }
  }

  public void sendPacket(ProtoPackage pp, String deviceName ){	  	  	
	    Util.Log("invoke sendString string="+pp.getMsg());
	      // put the string on EndPoint, so sender will send the message
	    pp.receiver = deviceName;
	    boolean packSended = false;
	    for ( int i=0; i < endPoints.size(); i++ )
	    {	      
	      DevicePoint endpt = (DevicePoint) endPoints.elementAt( i );
	      
	      if( endpt.remoteName.equals(deviceName) ){
		      Util.Log("Sending packet from="+pp.sender+"; to="+pp.receiver);
		      endpt.putPacket( pp );
		      packSended = true;
	      }
	    }
	    if( !packSended ) //Se o pack não foi enviado, tentar utilizar a PC
	    {
	    	Util.Log("Pacote naum enviado, inicializando PC");
	    	ProtoPackage protoPC = 
	    		new ProtoPackage(Constants.APP_GENERAL,
	    				Constants.CMD_FINDROUTE,
	    				localName,
	    				"",
	    				deviceName + "|" + localName);
	    	sendPacket(protoPC);
	    	commBridge.insertPack(pp);
	    }
}

  
  /**
   * Clean up the resource for a EndPoint, remove it from the active list.
   * This is triggered by a remote EndPoint leaving the network
   * @param endpt
   */
  public void cleanupRemoteEndPoint( DevicePoint endpt )
  {
    Util.Log("invoke cleanupRemoteEndPoint()");
    // set 'done' flag to true to exit the run loop
    endpt.reader.stop();
    endpt.sender.stop();

    // remove this end point from the active end point list
    endPoints.removeElement( endpt );
  }
  
  public void insertRemoteEndPoint(DevicePoint endpt){
	  
	  for (int i = 0; i < endPoints.size(); i++){
		  DevicePoint dp = (DevicePoint)endPoints.elementAt(i);
		  if( endpt.remoteName.equalsIgnoreCase(dp.remoteName)){
			  cleanupRemoteEndPoint(dp);
		  }
	  }
	  endPoints.addElement(endpt);
	  
  }

  /**
   * Implement local BlueChat service.
   */
  public void run()
  {
	  Thread.yield(); 
	  
    // connection to remote device
    StreamConnection c = null;
    try
    {
      // Create a server connection object, using a
      // Serial Port Profile URL syntax and our specific UUID
      // and set the service name to BlueChatApp
      server =  (StreamConnectionNotifier)Connector.open(
          "btspp://localhost:" + MIDService.BEEHIVE_UUID.toString() +";name=BeehiveApp");

      // Retrieve the service record template
      ServiceRecord rec = localDevice.getRecord( server );

      // set ServiceRecrod ServiceAvailability (0x0008) attribute to indicate our service is available
      // 0xFF indicate fully available status
      // This operation is optional
      rec.setAttributeValue( 0x0008, new DataElement( DataElement.U_INT_1, 0xFF ) );

      // Print the service record, which already contains
      // some default values
      //Util.printServiceRecord( rec );

      // Set the Major Service Classes flag in Bluetooth stack.
      // We choose Object Transfer Service
      rec.setDeviceServiceClasses(
          SERVICE_TELEPHONY  );



    } catch (Exception e)
    {
      e.printStackTrace();
      Util.Log(e.getClass().getName()+" "+e.getMessage());
    }

    while( !done)
    {
      try {
        ///////////////////////////////
        Util.Log("local service waiting for client connection");

        // this message is to inform user that the server is up and ready
        Util.Log("Ready to accept connection. Wait..." );

        //
        // start accepting client connection.
        // This method will block until a client
        // connected
        c = server.acceptAndOpen();

        Util.Log("local service accept a new client connection");


        //
        // retrieve the remote device object
        RemoteDevice rdev = RemoteDevice.getRemoteDevice( c );
        //
        // check to see if the EndPoint already exist
        DevicePoint endpt = findEndPointByRemoteDevice( rdev );
        if ( endpt != null )
        {
        	endpt.con = c;
        	// this is a safe guard to assure that this client
        	// has not been connected before
        	Util.Log("client connection end point already exist.. ignore this connection");
        } else
        {

        	// - create a new DevicePoint object
        	// - initialize the member variables
        	// - start the data reader and sender threads.
        	endpt = new DevicePoint( this, rdev, c);

        	if( Util.enableLog ) //Estou debugando, então EMULA Ponte de comunicação
        	{
        		//Dispositivo rodando é o 000, então não posso visualizar o 002
        		if( this.localName=="0000000DECAF")
        		{
        			if( endpt.remoteName == "0123456789AF" ) 
        				continue;
        		}
        		//Se dispositivo rodando é o 002, então não posso visualizar o 000
        		else if( this.localName=="0123456789AF"){
        			if( endpt.remoteName == "0000000DECAF") 
        				continue;

        		}
        	}

        	Thread t1 = new Thread( endpt.sender );
        	t1.start();

        	Thread t2 = new Thread( endpt.reader );
        	t2.start();

        	// add this EndPoint to the active list
        	insertRemoteEndPoint( endpt );

        	Util.Log("a new active EndPoint is established. name=" + endpt.remoteName);

        }


      }
      catch (IOException e) {
        e.printStackTrace();
        Util.Log(e.getClass().getName()+" "+e.getMessage());

        // if any exception happen, we assume this connection is
        // failed and close it. closing the connection will cause
        // the reader and sender thread to exit (because they will got
        // exception as well).
        if (c != null)
          try {
            c.close();
          }
          catch (IOException e2) {
            // ignore
          }

      }
      finally {
        // nothing to do here
      }
    } // while !done
  } // end run()


  /**
   * Internal discovery listener class for handling device & service discovery events.
   * @author Ben Hui
   * @version 1.0
   */
  class Listener implements DiscoveryListener
  {

    /**
     * A device is discovered.
     * Create a EndPoint for the device discovered and put it on the pending list.
     * A service search will happen when all the qualifying devices are discovered.
     *
     * @param remoteDevice
     * @param deviceClass
     */
    public void deviceDiscovered(RemoteDevice remoteDevice,
                                 DeviceClass deviceClass)
    {
      try {
        Util.Log("invoke deviceDiscovered name=" + remoteDevice.getFriendlyName(false));
      }
      catch (IOException ex) {
      }

      // only device of SERVICE_OBJECT_TRANSFER will be considered as candidate device
      // because in our BlueChat service, we explicitly set the service class to
      // SERVICE_OBJECT_TRANSFER. see the run() method
//      if ( (deviceClass.getServiceClasses() & SERVICE_OBJECT_TRANSFER) != 0 )
//      {
        try
        {
          // create a inactive EndPoint and put it on the pending list
          DevicePoint endpt = new DevicePoint(GeneralServer.this, remoteDevice, null);
          pendingEndPoints.addElement( endpt );

        } catch (Exception e)
        {
          e.printStackTrace();
          Util.Log(e.getClass().getName()+" "+e.getMessage());

        }
//      } else
//      {
//        Util.Log("found device that is not Object Transfer Service, ignore this device...");
//      }
    }

    /**
     * device discovery completed.
     * After device inquery completed, we start to search for BlueChat services.
     * We loop through all the pending EndPoints and request agent.searchServices
     * on each of the remote device.
     * @param transId
     */
    public void inquiryCompleted(int transId)
    {
      Util.Log( "invoke inqueryCompleted" );

      // wait 100ms and start doing service discovery
      // the choice of 100ms is really just a guess
      timer.schedule( new DoServiceDiscovery(), 100 );
    }

    /**
     * a service is discovered from a remote device.
     * when a BlueChat service is discovered, we establish a connection to
     * this service. This signal joining the existing virtual chat room.
     * @param transId
     * @param svcRec
     */
    public void servicesDiscovered(int transId, ServiceRecord[] svcRec)
    {
      Util.Log( "invoke servicesDiscovered:"+transId+","+svcRec.length);
      try {

        for ( int i=0; i< svcRec.length; i++ )
        {
          //Util.printServiceRecord( svcRec[i] );


          DevicePoint endpt = findEndPointByTransId( transId );

          serviceRecordToEndPoint.put( svcRec[i], endpt );

        }

      }
      catch (Exception e) {
        e.printStackTrace();
        Util.Log(e.getClass().getName());
        Util.Log(e.getMessage());

      }
    }

    /**
     * service discovery is completed.
     * @param int0
     * @param int1
     */
    public void serviceSearchCompleted(int transID, int respCode)
    {
      Util.Log("invoke serviceSearchCompleted: "+transID);
      // print response code
      if ( respCode == SERVICE_SEARCH_COMPLETED )
        Util.Log("SERVICE_SEARCH_COMPLETED");
      else if ( respCode == SERVICE_SEARCH_TERMINATED )
        Util.Log("SERVICE_SEARCH_TERMINATED");
      else if ( respCode == SERVICE_SEARCH_ERROR )
        Util.Log("SERVICE_SEARCH_ERROR");
      else if ( respCode == SERVICE_SEARCH_NO_RECORDS )
        Util.Log("SERVICE_SEARCH_NO_RECORDS");
      else if ( respCode == SERVICE_SEARCH_DEVICE_NOT_REACHABLE )
        Util.Log("SERVICE_SEARCH_DEVICE_NOT_REACHABLE");


      for ( Enumeration records = serviceRecordToEndPoint.keys(); records.hasMoreElements(); )
      {
        try {


        ServiceRecord rec = (ServiceRecord) records.nextElement();

        // We make an assumption that the first service is BlueChat. In fact, only one
        // service record will be found on each device.
        // Note: we know the found service is BlueChat service because we search on specific UUID,
        // this UUID is unique to us.
        String url  = rec.getConnectionURL( ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false );
        Util.Log("BeeHive service url="+url);
        StreamConnection con = (StreamConnection)Connector.open( url );

        // retrieve the pending EndPoint and initialize the necessary member variables
        // to activate the EndPoint. this includes
        // - initialize connection
        // - start sender and reader thread
        DevicePoint auxEndpt = (DevicePoint) serviceRecordToEndPoint.get( rec );
        DevicePoint endpt = findEndPointByRemoteDevice(auxEndpt.remoteDev);
        if( endpt==null ) endpt=auxEndpt;
        if ( endpt != null )
        {
        	endpt.con = con;

        	boolean ignoreDevice = false;
        	if( Util.enableLog ) //Estou debugando, então EMULA Ponte de comunicação
        	{
        		//Dispositivo rodando é o 000, então não posso visualizar o 002
        		if( localName=="0000000DECAF")
        		{
        			if( endpt.remoteName == "0123456789AF" ) 
        				ignoreDevice=true;
        		}
        		//Se dispositivo rodando é o 002, então não posso visualizar o 000
        		else if( localName=="0123456789AF"){
        			if( endpt.remoteName == "0000000DECAF") 
        				ignoreDevice=true;

        		}
        	}
        	if(!ignoreDevice){
        		Thread t1 = new Thread( endpt.sender );
        		t1.start();

        		Thread t2 = new Thread( endpt.reader );
        		t2.start();

        		insertRemoteEndPoint( endpt );

        		Util.Log("a new active EndPoint is established. name=" + endpt.remoteName);

        		// once a EndPoint established, the BlueChat client is responsible to initiate the
        		// handshake protocol.
        		ProtoPackage pkt = new ProtoPackage(
        				Constants.APP_GENERAL,
        				Constants.CMD_HANDSHAKE,
        				localName,
        				endpt.remoteName,
        				MobConfig.getNickname());

        		endpt.putPacket(pkt);
        		//endpt.putPacket( new ProtoPackage(GeneralServer.SIGNAL_HANDSHAKE, localName) );
        	}

        } else
        {
          Util.Log("cannot find pending EndPoint when a service is discovered. ignore this service...");
        }

        } catch (Exception e)
        {
          e.printStackTrace();
          Util.Log(e.getClass().getName()+" "+e.getMessage());

        }
      } // for

      // finished process current batch of service record
      // clear it and service discovery on next device
      serviceRecordToEndPoint.clear();

      synchronized( lock )
      {
        // unlock to proceed to service search on next device
        // see DoServiceDiscovery.run()
        lock.notifyAll();
      }

    }

  } // inner class Listener

  class DoServiceDiscovery extends TimerTask
  {
    public void run()
    {
      //
      // for each EndPoint, we search for BlueChat service
      for (int i = 0; i < pendingEndPoints.size(); i++)
      {

        DevicePoint endpt = (DevicePoint) pendingEndPoints.elementAt(i);

        try {
          Util.Log("search service on device " + endpt.remoteName);

          //
          // searchServices return a transaction id, which we will used to
          // identify which remote device the service is found in our callback
          // listener (class Listener)
          //
          // note: in theory, only one runtine instance of Listener is needed
          // to handle all discovery callback. however, there is a bug in rococo
          // simualtor that cause callback fails with one instance of used
          // so we make a new Listener for every searchServices()
          endpt.transId = agent.searchServices(null // null to indicate retrieve default attributes
                                               ,
                                               new UUID[] { MIDService.BEEHIVE_UUID }  
                                               ,
                                               endpt.remoteDev,
                                               new Listener());

          // wait until the above service discovery is completed
          // because N6600 cannot handle more than one service discovery
          // request at the same time
          // see serviceSearchCompleted()
          synchronized( lock )
          {
            try {
              lock.wait();
            }
            catch (InterruptedException ex) {
            }
          }
        }
        catch (BluetoothStateException e) {
          e.printStackTrace();
          Util.Log(e.getClass().getName()+" "+e.getMessage());

        }

      } // for

      // no more service to discovery. so any pending EndPoints
      // will be ignored and removed
      pendingEndPoints.removeAllElements();

      // this message is to inform user that chatting can start
      Util.Log("You can start chatting now" );

    }

  }

}