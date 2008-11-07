package mobile.chat;

import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextField;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Border;

import java.io.IOException;

import mobile.lib.Constants;
import mobile.lib.DevicePoint;
import mobile.lib.ProtoPackage;
import mobile.lib.Util;
import mobile.ui.*;

public class FormChat extends BaseForm {

    private String[][] CONTACTS_INFO = {
    	    {"Sala1","bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla"},
    	    {"Tidhar G.","Tidhar.Gilor@Sun.COM"},
    	    {"Iddo A.","Iddo.Arie@Sun.COM"},
    	    {"Ari S.","Ari.Shapiro@Sun.COM"}};
    
    public void cleanup() {    }

   public String getName() {
       return "Chat";
   }

   protected String getHelp() {
       return "Bate-papo com os seus amigos via bluetooth  " +
           "both are fully interchangable and seamless for the developer.";
   }

   protected void execute(Form f) {
	   f.setLayout(new BorderLayout());
       //disable the scroll on the Form.
       f.setScrollable(false);
       //Image contacts = null;
       //Image  persons[] = null;
      
       //some constants to help us parse the people image
       int contactWidth= 36;
       int contactHeight= 48;
       int cols = 4;

       /*try {
           //Resources images = UIDemoMIDlet.getResource("images");
           contacts = images.getImage("people.jpg");
           persons = new Image[CONTACTS_INFO.length];
           for(int i = 0; i < persons.length ; i++){
                   persons[i] = contacts.subImage((i%cols)*contactWidth, (i/cols)*contactHeight, contactWidth, contactHeight, true);                   
           }
       } catch (IOException ex) {
           ex.printStackTrace();
       }*/
       
       final Contact[] contactArray = new Contact[5 * CONTACTS_INFO.length];
       for (int i = 0; i < contactArray.length; i++) {
           int pos = i % CONTACTS_INFO.length;
           contactArray[i] = new Contact(CONTACTS_INFO[pos][0], CONTACTS_INFO[pos][1]);
       }
       f.addComponent(BorderLayout.NORTH, new Label("Escolha uma sala"));
       f.addComponent(BorderLayout.CENTER, createList(contactArray, List.VERTICAL, new ContactsRenderer()));
       
       //Criar sala
       Container cnt = new Container(new BoxLayout(BoxLayout.Y_AXIS));
       cnt.addComponent(new Label("Criar sala: "));
       cnt.addComponent(new TextField(10));
       f.addComponent(BorderLayout.SOUTH, cnt);
       
       //////////////////////////////////////////////////////////////
       getMidlet().send(Constants.APP_CHAT, Constants.EVENT_LISTCHAT, "listchats");
       ////////////////////////////////////////////////////////////
   }
   
   private List createList(Contact[] contacts, int orientation, ListCellRenderer renderer) {
       List list = new List(contacts);
       list.getStyle().setBgTransparency(0);
       list.setListCellRenderer(renderer);
       list.setOrientation(orientation);
       return list;
   }

   private Label createFont(Font f, String label) {
       Label fontLabel = new Label(label);
       fontLabel.getStyle().setFont(f);
       fontLabel.setFocusable(true);
       //fontLabel.setFocusPainted(false);
       fontLabel.getStyle().setBgTransparency(0);
       return fontLabel;
   }
   
   class Contact {

       private String name;
       private String email;
       //private Image pic;

       public Contact(String name, String email) {
           this.name = name;
           this.email = email;
           //this.pic = pic;
       }

       public String getName() {
           return name;
       }

       public String getEmail() {
           return email;
       }
   }
   
   class ContactsRenderer extends Container implements ListCellRenderer {

       private Label name = new Label("");
       private Label email = new Label("");
       private Label focus = new Label("");
       
       public ContactsRenderer() {
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

           Contact person = (Contact) value;
           name.setText(person.getName());
           email.setText(person.getEmail());
           //pic.setIcon(person.getPic());
           return this;
       }

       public Component getListFocusComponent(List list) {
           return focus;
       }
   }

	public void handleAction(byte action, Object param1, Object param2) {
		Util.Log("invoke CHAT handleAction. action=" + action);
		
		DevicePoint endpt = (DevicePoint) param1;
		ProtoPackage pkt = (ProtoPackage) param2;
		
		if( pkt.command == Constants.EVENT_LISTCHAT){
			
			getMidlet().sendSingle(pkt.receiver, 
					Constants.APP_CHAT, 
					Constants.EVENT_LISTCHAT_ACK,
					"MinhaSala");
			
		} else if( pkt.command == Constants.EVENT_LISTCHAT_ACK){
			if( pkt.msg.length()>0){
				Util.Log("New chat found: "+pkt.msg);
			}
		}
	
		
		
	}
   
}
