package mobile.chat;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import mobile.lib.BTListener;
import mobile.lib.Constants;
import mobile.lib.DevicePoint;
import mobile.lib.GeneralServer;
import mobile.lib.ProtoPackage;
import mobile.lib.Util;
import mobile.ui.BaseForm;

import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.SelectionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Border;

public class FormChat extends BaseForm implements BTListener,ActionListener{


	//Lista total de salas
	private List itensList;
	
	//Salas criadas pelo usuário
	private Vector userChats;

	//Campo texto de criar uma sala
	private TextField txtRoomName;

	private FormRoom fRoom;
	
	public void cleanup() {    }
	
	public String getName() {
		return "Bate-Papo";
	}
	public String getIconName() {
		return "Chat";
	}
	
	protected String getHelp() {
		return "Bate-papo com amigos, através do bluetooth.  " +
		" Você pode conversar através de salas próprias ou de seus amigos.";
	}
	
	protected void execute(Form f) {
		f.setLayout(new BorderLayout());
		//disable the scroll on the Form.
		f.setScrollable(false);
		
		final ChatRoom[] roomArray = new ChatRoom[1];
		roomArray[0] = new ChatRoom("", "Pública","Sala de assuntos gerais");

		f.addComponent(BorderLayout.NORTH, new Label("Escolha uma sala"));

		itensList = createList(roomArray, List.VERTICAL, new ChatRoomRenderer());
		itensList.addActionListener(this);
		
		f.addComponent(BorderLayout.CENTER, itensList);
        
		fRoom = new FormRoom(f,this.getMidlet());		
		
		//////////////////////////////////////////////////////////////
		//Cria timer que atualiza as salas
		AtualizarSalas();
		////////////////////////////////////////////////////////////		
	
		Command cmdCreateRoom = new Command("Criar Sala"){
			public void actionPerformed(ActionEvent evt) {

				
				Container cnt = new Container(new BoxLayout(BoxLayout.Y_AXIS));
				cnt.addComponent(new Label("Criar sala: "));
				txtRoomName = new TextField(10);
				cnt.addComponent(txtRoomName);						
				
				Command cmdOk = new Command("Ok"){
					public void actionPerformed(ActionEvent evt) {
						//super.actionPerformed(evt);
						String chatName = txtRoomName.getText();
						if( userChats == null){
							userChats  = new Vector();
						}
						ChatRoom chatCreated = new ChatRoom( getMidlet().getMyDeviceName(), chatName,"Sala privada"); 
						userChats.addElement( chatCreated );					
						itensList.addItem( chatCreated );
					}
				};
				Command cmdCancel = new Command("Cancelar");
				
				Command[] cmdList = new Command[2];
				cmdList[0] = cmdOk; 
				cmdList[1] = cmdCancel;
				
				Dialog.show("Criar sala", cnt, cmdList);	
				
				super.actionPerformed(evt);
			}
		};
		
	
		f.addCommand(cmdCreateRoom);
		
		//Dialog.show("Informações", "testando...", "Ok","Cancelar");		
	}
	
	public void actionPerformed(ActionEvent evt) {
		if( evt.getSource() == itensList){
			
		 	ChatRoom cr = (ChatRoom)itensList.getSelectedItem();
			fRoom.setChatRoom(cr);
			
			getMidlet().send(Constants.APP_CHAT, Constants.EVENT_JOIN, cr.getName());
			
			fRoom.show();			
		}
	}

	private List createList(ChatRoom[] contacts, int orientation, ListCellRenderer renderer) {
		List list = new List(contacts);
		list.getStyle().setBgTransparency(0);
		list.setListCellRenderer(renderer);
		list.setOrientation(orientation);
		return list;
	}

	public void handleAction(byte event, Object param1, Object param2) {
		Util.Log("invoke CHAT handleAction. action=" + event);

		DevicePoint endpt = (DevicePoint) param1;
		ProtoPackage pkt = (ProtoPackage) param2;

		if( pkt.command == Constants.EVENT_LISTCHAT){

			if( userChats!=null){
				for(int i=0; i<userChats.size(); i++){
					
					ChatRoom aux = (ChatRoom)userChats.elementAt(i);
					getMidlet().sendSingle(pkt.sender, 
							Constants.APP_CHAT, 
							Constants.EVENT_LISTCHAT_ACK,
							aux.name + "|" + aux.description);
				}
			}
		} else if( pkt.command == Constants.EVENT_LISTCHAT_ACK){
			if( pkt.msg.length()>0){
				
				Util.Log("New chat found: "+pkt.msg);
				
				String[] dataReceived = Util.split(pkt.msg, "|"); 
				
				if( dataReceived.length>1){
					ChatRoom cr = new ChatRoom( pkt.sender,dataReceived[0],dataReceived[1]);
					itensList.addItem(cr);					
				}
			}
		} 
		else if(fRoom.getIsChatting()) //Só chama o handle se estiver dentro da sala
		{
			fRoom.handleAction(event, endpt, pkt);	
		}
	}
	
	public static final long TEMPO = ( 3000 * 10 ); //atualiza o site a cada 30 segundos   
	  
	   Timer timer = null;   
	   
	   public void AtualizarSalas() {   
	      if( timer == null ) {   
	         timer = new Timer();   
	         TimerTask tarefa = new TimerTask() {   
	            public void run() {   
	               try {  
	            	   if( !fRoom.getIsChatting() ) //Só envia a solicitação se não estiver na sala
	            	   {
	            		   //////////////////////////////////////////////////////////////
	            		   //Send in broadcast
	            		   getMidlet().send(Constants.APP_CHAT, Constants.EVENT_LISTCHAT, "listchats");
	            		   ////////////////////////////////////////////////////////////
	            	   }
	               }   
	               catch(Exception e){   
	                  e.printStackTrace();   
	               }   
	                 
	            }   
	         };   
	         timer.scheduleAtFixedRate(tarefa, TEMPO, TEMPO);   
	      }   
	   }  
	
	
	
	class ChatRoom {

		private String name;
		private String description;
		private String owner;

		public ChatRoom(String owner, String name, String description) {
			this.owner = owner;
			this.name = name;
			this.description = description;
		}

		public String getOwner(){
			return owner;
		}
		
		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}
	}

	class ChatRoomRenderer extends Container implements ListCellRenderer {

		private Label name = new Label("");
		private Label email = new Label("");
		private Label focus = new Label("");

		public ChatRoomRenderer() {
			setLayout(new BorderLayout());
			//addComponent(BorderLayout.WEST, pic);
			Container cnt = new Container(new BoxLayout(BoxLayout.Y_AXIS));
			name.getStyle().setBgTransparency(0);
			name.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
			email.getStyle().setBgTransparency(0);           
			cnt.addComponent(name);
			cnt.addComponent(email);
			//TODO: Melhorar borda
			cnt.getStyle().setBorder(Border.createLineBorder(1));

			addComponent(BorderLayout.CENTER, cnt);

			//focus.getStyle().setBgTransparency(100);
		}

		public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {

			ChatRoom person = (ChatRoom) value;
			name.setText(person.getName());
			email.setText(person.getDescription());
			//pic.setIcon(person.getPic());
			return this;
		}

		public Component getListFocusComponent(List list) {
			return focus;
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	


}
