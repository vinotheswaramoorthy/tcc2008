package mobile.chat;

import java.util.Vector;

import mobile.chat.FormChat.ChatRoom;
import mobile.lib.Constants;
import mobile.lib.DevicePoint;
import mobile.lib.ProtoPackage;
import mobile.lib.Util;
import mobile.midlet.MainMID;

import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;

public class FormRoom extends Form implements ActionListener{

	private Form parent;
	private MainMID midlet;
	
	//Sala que está On-Line
	private ChatRoom cr;
	
	//comando para retornar a tela anterior
	private Command back = new Command("Voltar");	
	//comando para enviar mensagem
	private Command send = new Command("Enviar");
	
	private boolean isChatting = false;
	
	private TextArea taTalking;
	private TextField tfMessage;
	
	public void setChatRoom(ChatRoom chatRoom){
		this.cr = chatRoom;
		this.setTitle(cr.getName());
	}
	
	//Informa se estou dentro da sala.
	public boolean getIsChatting(){		
		return isChatting;
	}
	
	public void show() {
		isChatting = true;
		super.show();
	}
	
	private int cols;
	
	public FormRoom(Form parent, MainMID midlet){
		this.parent = parent;
		this.midlet = midlet;
		
		this.setLayout(new BorderLayout());
		this.setScrollable(false);
		
		int width = Display.getInstance().getDisplayWidth(); //get the display width
		int elementWidth = 0;		
		
		
		
		taTalking = new TextArea(5,20,TextArea.ANY);
		taTalking.setEditable(false);
		this.addComponent(BorderLayout.CENTER, taTalking);
		elementWidth = Math.max(taTalking.getPreferredW(), elementWidth);
		
		tfMessage = new TextField(15);
		tfMessage.setMaxSize(100);
		tfMessage.setRows(1);
		tfMessage.requestFocus();
		
		Container cntBottom = new Container();
		cntBottom.addComponent(new Label("Falar:"));
		cntBottom.addComponent(tfMessage);			
		this.addComponent(BorderLayout.SOUTH, cntBottom);
		elementWidth = Math.max(cntBottom.getPreferredW(), elementWidth);	
		
		this.addCommand(back);
		this.addCommand(send);
		this.setCommandListener(this);
	}
	
	
	private void insertMessage(String message){
		taTalking.setText(taTalking.getText()+
				"\r\n"+
				message);
	}

	public void actionPerformed(ActionEvent evt) {
		if( evt.getSource()==back){
			
			midlet.send(Constants.APP_CHAT, Constants.EVENT_LEAVE, cr.getName().toString());
			
			//Limpa tela
			taTalking.setText("");
			
			isChatting = false;
			parent.show();
		}			
		else if( evt.getSource()==send){

			String msg = tfMessage.getText();
			for(int i=0;i<allDevices.size();i++){
			
				String deviceName = (String)allDevices.elementAt(i);
				
				midlet.sendSingle(deviceName,Constants.APP_CHAT, Constants.EVENT_RECEIVED, cr.getName() + "|" + msg);
				
			}			
			String msgPrefix = midlet.getMyDeviceName() + " fala: ";
			//(char)13+(char)10
			insertMessage(msgPrefix + msg);
			tfMessage.setText("");
		}
	}
	
	private Vector allDevices = new Vector();
	public void handleAction(byte event, Object param1, Object param2) {
		Util.Log("invoke ROOM handleAction. action=" + event);

		DevicePoint endpt = (DevicePoint) param1;
		ProtoPackage pkt = (ProtoPackage) param2;	
		
		
		if (event == Constants.EVENT_JOIN) {			
			// a new user has join the chat room
						
			//Dados do pacote contém o nome da sala (poderiamos alterar para um ID)
			if( pkt.msg.equals(cr.getName())){	
				if( !allDevices.contains(pkt.sender)){
					String msg = pkt.sender + " entrou na sala";
					allDevices.addElement(pkt.sender);
					insertMessage(msg);								
					
					//Responde avisando o que eu também estou na sala
					midlet.sendSingle(pkt.sender, Constants.APP_CHAT, Constants.EVENT_JOIN, cr.getName());
				}
			}					
		} else if (event == Constants.EVENT_SENT) {
			// nothing to do
		} else if (event == Constants.EVENT_RECEIVED) {
			// a new message has received from a remote user

			Util.Log("EVENT_RECEIVED: "+pkt.msg);
			
			String[] receivedPk = Util.split(pkt.msg,"|");
			
			if(receivedPk.length>0){
				if( receivedPk[0].equals(cr.getName())){
					String msg = pkt.sender + " fala: ";
					// render this message on screen
					insertMessage(msg+receivedPk[1]);
				}
			}

		} else if (event == Constants.EVENT_LEAVE) {
			// a user has leave the chat room
			
			if( pkt.msg.equals(cr.getName())){
			
				String msg = pkt.sender + " sai da sala";
				if( allDevices.contains(pkt.sender))
					allDevices.removeElement(pkt.sender);
				// 	display the leave message on screen
				insertMessage(msg);
			}
		}
	}
	
	
}
