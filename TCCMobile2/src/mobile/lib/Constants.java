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
	  public final static byte CMD_UPDATEINFO		= 0x3D;
	  
	  //Events
	  public final static byte EVENT_JOIN			= 0x37;
	  public final static byte EVENT_LEAVE 			= 0x38;	  
	  public final static byte EVENT_SENT 			= 0x39;
	  public final static byte EVENT_RECEIVED		= 0x3A;
	  public final static byte EVENT_LISTCHAT		= 0x3B;
	  public final static byte EVENT_LISTCHAT_ACK 	= 0x3C;	 
	  
	  /*FileTransfer Commands*/
	  //comando de inicio de transmissão de arquivo
	  public final static byte CMD_STARTSEND		= 0x51;
	  //comando de finalização de transmissão do arquivo
	  public final static byte CMD_STOPSEND			= 0x52;
	  //comando para confirmação e requisição do proximo arquivo
	  public final static byte CMD_TRANSFERING		= 0x53;
	  //comando de confirmação dos frames de transferencia do arquivo
	  public final static byte CMD_TRANSFERING_ACK	= 0x54;
	  //comando para requisitar um novo arquivo
	  public final static byte CMD_REQUESTSEND      = 0x55;
	  //comando para requisitar a lista de arquivo
	  public final static byte CMD_REQUESTLIST		= 0x56;
	  //comando de retorno da lista de arquivos
	  public final static byte CMD_RETURNLIST		= 0x57;
	  //comando de requisição de arquivos
	  public final static byte CMD_REQUESTFILE 		= 0x58;
	  //comando de requisição da lista de usuários
	  public final static byte CMD_REQUESTUSERS 	= 0x59;
	  //comando de retorno da lista de usuários
	  public static final byte CMD_RETURNUSER 		= 0x5A;
	  
	  //Profile Commands
	  
	  /*
	  public final static String EVENT_JOIN = "join";
	  public final static String EVENT_LEAVE = "leave";
	  public final static String EVENT_RECEIVED = "received";
	  public final static String EVENT_SENT = "sent";
	  */ 
	
}
