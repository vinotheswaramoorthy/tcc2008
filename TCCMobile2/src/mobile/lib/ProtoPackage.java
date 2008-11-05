package mobile.lib;

import java.nio.ByteBuffer;


public class ProtoPackage {
		  // signal, must be one of GeneralServer.SIGNAL_XXX
		  public byte application;
		  //command
		  public byte command;
		  // indicate the nick name of the sender
		  public String sender;
		
		  // indicate the nick name of the destination
		  public String receiver;
		  // the message content
		  public String msg = "";

		  public ProtoPackage(){}
		  public ProtoPackage(byte signal, byte command, String sender, String receiver, String msg)
		  {
		    this.application 	= signal;
		    this.command	 	= command;
		    this.sender 		= sender;
		    this.receiver 		= receiver;
		    this.msg 			= msg;
		  }

		  public static ProtoPackage getProtoPackage(byte[] arrBytes)
		  {
			  ProtoPackage pkt = new ProtoPackage();
			  ByteBuffer bb = ByteBuffer.wrap(arrBytes);
			  
 			 //PROTOCOLO: SOH STX APP[1] CMD[1] ORIG[16] DEST[16] NCHAR[1] DATA ETX EOT
			  
			  if( bb.get()!=0x01 ) return null;
			  
			  if( bb.get()!=0x02 ) return null;
			  
			  pkt.application  = bb.get();
			  
			  pkt.command 		= bb.get();			  			 			  			  			  
			  
			  byte[] btSender= new byte[16];
			  bb.get(btSender,0,16);
			  pkt.sender = new String(btSender).trim();
			  
			  byte[] btReceiv= new byte[16];			  
			  bb.get(btReceiv,0,16);
			  pkt.receiver = new String(btReceiv).trim();			  
			  
			  byte nchar = bb.get();
			  //End, so get out! 
			  if( nchar == 0x03 || nchar == 0x04) return null;
			  if( nchar!= 0x00 )
			  {
				  int msgLength = Util.unsignedByteToInt(nchar);
				  if( msgLength>0 ){
					 byte[] btMessage = new byte[msgLength];
					 bb.get(btMessage, 0, msgLength);
					 pkt.msg = new String( btMessage );
				  }			  
			  }
			  
			  if ( bb.get()!=0x03 ) return null;
			  if ( bb.get()!=0x04 ) return null;
			  
			  return pkt;
			  
		  }

		  
			
		  public byte[] getBytes() throws Exception{
			  
			  
			  //PROTOCOLO: SOH STX APP[1] CMD[1] ORIG[16] DEST[16] NCHAR[1] DATA ETX EOT 			 
			  
			  byte[] btAux = new byte[512];
			  //Build the ProtoPacket that will be sent
			  ByteBuffer bb = ByteBuffer.wrap(btAux);			 
			  
			  // 16 bytes to Sender			  
			  byte[] btSender 	= sender.getBytes();
			  if( btSender.length>16 ) throw new Exception("Invalid byte sender size");
			  
			  // 16 bytes to Receiver
			  byte[] btReceiver = receiver.getBytes();
			  if( btReceiver.length>16 ) throw new Exception("Invalid byte receiver size");			
			  
			  // XXXX bytes to the Message 
			  byte[] btMessage	= msg.getBytes();			  			  
			  if( btMessage.length>255 ) throw new Exception("Invalid byte application size. Max Size 255");			  			 
			  
			  bb.position(0);
			  //SOH
			  bb.put( (byte)0x01 );
			  //STX
			  bb.put( (byte)0x02 );
			  
			  bb.put( application );
			  
			  bb.put( command );
			  			  
			  bb.position(20-btSender.length); //Calculated to fix 16 bytes
			  bb.put(btSender);
			  bb.position(36-btReceiver.length); //Calculated to fix 16 bytes
			  bb.put(btReceiver);
			  
			  //NCHAR
			  bb.put((byte)btMessage.length);
			  
			  bb.put(btMessage);
			  
			  //ETX
			  bb.put( (byte)0x03 );

			  //EOT
			  bb.put( (byte)0x04 );
			  			  
			  return bb.array();
			  
		  }		  
	}