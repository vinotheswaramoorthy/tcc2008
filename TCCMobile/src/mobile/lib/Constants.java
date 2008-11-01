package mobile.lib;

public class Constants {
	
	  public final static byte APP_GENERAL		= 0x31;
	  public final static byte APP_CHAT 		= 0x32;
	  public final static byte APP_FILETRANSFER = 0x33;
	  public final static byte APP_PROFILE 		= 0x34;
	  ////ATTENTION: Ignorar o sinal abaixo, será implementado se houver tempo
	  public final static byte APP_MESSAGE 		= 0x35;	  
	
	  //General Commands
	  public final static byte CMD_TERMINATE		= 0x31;
	  public final static byte CMD_TERMINATE_ACK	= 0x32;
	  public final static byte CMD_HANDSHAKE		= 0x33;
	  public final static byte CMD_HANDSHAKE_ACK	= 0x34;
	  public final static byte CMD_MESSAGE  		= 0x35;
	  public final static byte CMD_INITIATED		= 0x36;
	  
	  //Events
	  public final static byte EVENT_JOIN		= 0x37;
	  public final static byte EVENT_LEAVE 		= 0x38;	  
	  public final static byte EVENT_SENT 		= 0x39;
	  public final static byte EVENT_RECEIVED	= 0x3A;
	  
	  
	  
	  /*
	  public final static String EVENT_JOIN = "join";
	  public final static String EVENT_LEAVE = "leave";
	  public final static String EVENT_RECEIVED = "received";
	  public final static String EVENT_SENT = "sent";
	  */ 
	
}
