package mobile.midlet;

import java.util.Enumeration;

import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.sun.kvem.midp.pim.formats.EndMatcher;

import mobile.chat.Chat;
import mobile.filetransfer.FileTransfer;
import mobile.lib.BTListener;
import mobile.lib.Constants;
import mobile.lib.DevicePoint;
import mobile.lib.GeneralServer;
import mobile.lib.ProtoPackage;
import mobile.lib.Util;


public class MainMenu extends MIDlet implements CommandListener, BTListener {

	Alert alert;

	String msg = "";
	private Display display;
	//private Command cmdExit;
	private Command cmdSelect;
	private Command cmdBackMain;
	private List menuMain;
	private List menuConfig;

	private Command cmdChat;
	private Command cmdProfile;
	private Command cmdShareFile;
	private Command cmdConfigChat;
	private Command cmdConfigProfile;
	private Command cmdConfigShareFile;
	
	private GeneralServer btServer;
	
	
	private FileTransfer fileTransfer;
	public Enumeration listRoots;
	private Chat chat;

	public MainMenu() {
		
		//LOG!
		Util.enableLog = false;
		
		display = Display.getDisplay(this);

		//cmdExit 		= new Command("Exit", 			Command.EXIT, 	1);
		cmdSelect 			= new Command("Select", 		Command.OK, 	1);
		cmdBackMain			= new Command("Back",			Command.BACK, 	1);
		cmdChat				= new Command("Chat",			Command.BACK, 	1);
		cmdProfile			= new Command("Profile",		Command.BACK, 	1);
		cmdShareFile		= new Command("ShareFile",		Command.BACK, 	1);
		cmdConfigChat		= new Command("ConfigChat",		Command.BACK, 	1);
		cmdConfigProfile	= new Command("ConfigProfile",	Command.BACK, 	1);
		cmdConfigShareFile	= new Command("ConfigShareFile",Command.BACK, 	1);

		/*****************************************/

		String[] optMain = {"Bate-Papo", "Buscar Amigos", "Compartilhar Arquivos", "Configuração"};
		menuMain = new List("MENU", Choice.IMPLICIT, optMain, null);
		menuMain.addCommand(cmdSelect);
//		menuMain.addCommand(cmdProfile);
//		menuMain.addCommand(cmdChat);
//		menuMain.addCommand(cmdShareFile);
		menuMain.setCommandListener(this);

		/*****************************************/

		String[] optConfig = {"Configurar Chat", "Configurar Perfil", "Configurar Compartilhador"};
		menuConfig = new List("CONFIGURAÇÃO", Choice.IMPLICIT, optConfig, null);
		menuConfig.addCommand(cmdBackMain);
//		menuConfig.addCommand(cmdConfigProfile);
//		menuConfig.addCommand(cmdConfigChat);
//		menuConfig.addCommand(cmdConfigShareFile);
		menuConfig.setCommandListener(this);

		/*****************************************/
		
		////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////
		
		btServer = new GeneralServer();
		
		btServer.init( "", this);
		
		btServer.query();
		
		////////////////////////////////////////////////////////////////////////

		
		
		
		//instancia o fileTransfer
		fileTransfer = new FileTransfer(this);
		chat = new Chat(this);
	}

	protected void startApp() throws MIDletStateChangeException {
		listRoots = FileSystemRegistry.listRoots();
		display.setCurrent(menuMain);
	}

	public void showAlert(String message){
		alert = new Alert("...TESTE...",message,null, AlertType.INFO);
		alert.setTimeout(Alert.FOREVER);
		this.display.setCurrent(alert);
	}
	
	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	public void commandAction(Command cmd, Displayable screen) {
		if(cmd == List.SELECT_COMMAND)
		{
			if(screen == this.menuMain)
			{
				System.out.println("entrei main");
				switch(menuMain.getSelectedIndex())
				{
					case 0: cmd = this.cmdChat;break;
					case 1: cmd = this.cmdProfile;break;
					case 2: cmd = this.cmdShareFile;break;
					case 3: cmd = this.cmdSelect;break;					
				}
				System.out.println("Select: "+menuMain.getSelectedIndex()); 
			}
			else if(screen == this.menuConfig)
			{
				System.out.println("entrei config");
				switch(menuConfig.getSelectedIndex())
				{
					case 0: cmd = this.cmdConfigChat; break;
					case 1: cmd = this.cmdConfigProfile; break;
					case 2: cmd = this.cmdConfigShareFile; break;
				}
				System.out.println("Select: "+menuConfig.getSelectedIndex()); 
			}
		}
		//System.out.println("Select: "+ cmd);
		if(cmd == this.cmdChat)
		{
			/*
			alert = new Alert("...CHAT...",msg,null, AlertType.INFO);
			alert.setTimeout(Alert.FOREVER);
			this.display.setCurrent(alert);
			*/
			// chama o Form Chat
			this.screenShow(chat);
		}
		else if(cmd == this.cmdProfile)
		{
			alert = new Alert("...PROFILE...",msg,null, AlertType.INFO);
			alert.setTimeout(Alert.FOREVER);
			this.display.setCurrent(alert);
		}
		else if(cmd == this.cmdShareFile)
		{
			/*
			alert = new Alert("...SHARE FILES",msg,null, AlertType.INFO);
			alert.setTimeout(Alert.FOREVER);
			this.display.setCurrent(alert);
			*/
			//chama o fileTransfer
			this.display.setCurrent(fileTransfer);
		}
		else if(cmd == this.cmdConfigChat)
		{
			alert = new Alert("...CONFIG CHAT...",msg,null, AlertType.INFO);
			alert.setTimeout(Alert.FOREVER);
			this.display.setCurrent(alert);
		}
		else if(cmd == this.cmdConfigProfile)
		{	
			alert = new Alert("...CONFIG PROFILE...",msg,null, AlertType.INFO);
			alert.setTimeout(Alert.FOREVER);
			this.display.setCurrent(alert);
		}
		else if(cmd == this.cmdConfigShareFile)
		{	
			alert = new Alert("...CONFIG SHARE FILES",msg,null, AlertType.INFO);
			alert.setTimeout(Alert.FOREVER);
			this.display.setCurrent(alert);
		}
		else if(cmd == this.cmdSelect)
		{
			this.screenShow(this.menuConfig);
		}
		else if(cmd == this.cmdBackMain)
		{
			this.screenShow(null); 
		}
		
	}

	public void showMainMenu(){
		display.setCurrent(menuMain);
	}
		
	public void screenShow(Displayable diplay) {
		if (diplay==null)
			display.setCurrent(menuMain);
		else
			display.setCurrent(diplay);
	}

	public void handleAction(byte action, Object param1, Object param2) {				
		
		DevicePoint endpt = (DevicePoint) param1;
		ProtoPackage pkt = (ProtoPackage) param2;
		
		
		if( pkt.application == Constants.APP_GENERAL ) {			
			//General must be handled HERE!			
		}
		else if( pkt.application == Constants.APP_CHAT ){
			chat.handleAction(action, param1, param2);
			
		}
		else if( pkt.application == Constants.APP_PROFILE ){
			
			
		}
		else if( pkt.application == Constants.APP_FILETRANSFER ){
			
			
		} 
		
	    
		
	}

	public void send(byte app, byte cmd, String msg){
		
		ProtoPackage senderPkt = new ProtoPackage(
					app,
					cmd, 
					btServer.localName,
					"", //Will be replaced by the correct destination
					msg
				); 
		btServer.sendPacket(senderPkt);		
	}
}
