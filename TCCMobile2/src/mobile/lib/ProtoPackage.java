package mobile.lib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

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
		  public String getMsg(){
			  if(arrData==null) return "";
			  return new String(arrData);
		  }
		  
		  public void setMsg(String msg){
			  arrData = msg.getBytes();
		  }
		  
		  private byte[] arrData;
		  public byte[] getData(){
			  if( arrData==null) return new byte[0];			  
			  return arrData;
		  }
		  
		  public void setData(byte[] arrBytes){
			  arrData = arrBytes;
		  }

		  public ProtoPackage(){}
		  public ProtoPackage(byte signal, byte command, String sender, String receiver, String msg)
		  {
		    this.application 	= signal;
		    this.command	 	= command;
		    this.sender 		= sender;
		    this.receiver 		= receiver;
		    this.arrData 		= msg.getBytes();
		  }

		  public static ProtoPackage getProtoPackage(byte[] arrBytes)
		  {  
			  try {
				ProtoPackage pkt = new ProtoPackage();
				  
				  ByteArrayInputStream bb = new ByteArrayInputStream(arrBytes);			 		
				  
				  
				 //PROTOCOLO: SOH STX APP[1] CMD[1] ORIG[16] DEST[16] NCHAR[1] DATA ETX EOT
				  
				  if( bb.read()!=0x01 ) return null;
				  if( bb.read()!=0x02 ) return null;

				  
				  pkt.application  = (byte)bb.read();
				  			 
				  pkt.command 		= (byte)bb.read();			  			 			  			  			  
				  
				  
				  byte[] btSender= new byte[16];
				  bb.read(btSender, 0, 16);
				  //bb.get(btSender,0,16);
				  pkt.sender = new String(btSender).trim();
				  			  
				  byte[] btReceiv= new byte[16];			  
				  bb.read(btReceiv,0,16);
				  pkt.receiver = new String(btReceiv).trim();			  
				  
				  byte nchar = (byte)bb.read();
				  
				  if( nchar!= 0x00 )
				  {
					  int msgLength = Util.unsignedByteToInt(nchar);
					  if( msgLength>0 ){
						 byte[] btMessage = new byte[msgLength];
						 bb.read(btMessage, 0, msgLength);
						 pkt.arrData = btMessage;
					  }			  
				  }
				  
				  if ( bb.read()!=0x03 ) return null;
				  if ( bb.read()!=0x04 ) return null;		
				  
				  return pkt;
			} 	catch(Exception ex){
				  Util.Log("Erro ao obter o protocolo");
				  return null;				  
			  }
			  
		  }

		  public byte[] getBytes() throws Exception{
			  
			  try{
				  //PROTOCOLO: SOH STX APP[1] CMD[1] ORIG[16] DEST[16] NCHAR[1] DATA ETX EOT 			 

				  //Build the ProtoPacket that will be sent
				  ByteArrayOutputStream bb = new ByteArrayOutputStream(296);			  

				  DataOutputStream dos = new DataOutputStream(bb);			

				  // 16 bytes to Sender			  
				  byte[] btSender 	= sender.getBytes();
				  if( btSender.length>16 ) throw new Exception("Invalid byte sender size");

				  // 16 bytes to Receiver
				  byte[] btReceiver = receiver.getBytes();
				  if( btReceiver.length>16 ) throw new Exception("Invalid byte receiver size");			

				  // XXXX bytes to the Message 			  			  
				  if( getData().length>255 ) throw new Exception("Invalid byte application size. Max Size 255");			  			 			  

				  //SOH			  
				  dos.write( (byte)0x01);

				  //STX
				  dos.write(  (byte)0x02 );			  

				  dos.write( application );

				  dos.write( command );

				  byte[] btAuxSender = new byte[16-btSender.length];
				  dos.write(btAuxSender);
				  dos.write(btSender);

				  byte[] btAuxReceiver = new byte[16-btReceiver.length];  
				  dos.write(btAuxReceiver);
				  dos.write(btReceiver);			  

				  //NCHAR
				  dos.write( (byte)getData().length);

				  dos.write(getData());

				  //ETX
				  dos.write( (byte)0x03 );

				  //EOT
				  dos.write( (byte)0x04 );

				  int falta = 296-bb.size();

				  dos.write(new byte[falta]);

				  dos.flush();			  		

				  byte[] btArray = bb.toByteArray(); 			  

				  return btArray;	
			  }
			  catch(Exception ex){
				  Util.Log("Erro ao gerar os bytes do protocolo");
				  throw ex;				  
			  }
		  }		
		  
			static void showArrayData(
					byte[] array){
				System.out.println(
				"Show array data");
				for(int cnt = 0; 
				cnt < array.length; cnt++){
					System.out.print(
							array[cnt] + " ");
					if((cnt+1)%12 == 0)
						System.out.println();//line
				}//end for loop
				System.out.println();//blank line
			}//end showArrayData
	}