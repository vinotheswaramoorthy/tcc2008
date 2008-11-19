package mobile.profile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import mobile.ui.BaseForm;

import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.plaf.Border;

public class FormProfile extends BaseForm{

	private Vector profilesVector;
	
	protected void execute(Form f) {
		//configura o layout do form
		f.setLayout(new BorderLayout());
		
		Label photo = new Label("");
		Label name = new Label("Nome:\tLeanadro Ciola");
		Label sex = new Label("Sexo:\tMasculino.. por enquanto");
		Label mail = new Label("e-mail:\tleandrociola@yahoo.com.br");
		
		try {
			FileConnection fc = (FileConnection)Connector.open("file://localhost/root1/ciola.jpg");
			photo.setIcon(Image.createImage(fc.openInputStream()).scaled(55,55));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Container person = new Container();
		person.setLayout(new BorderLayout());
		person.addComponent(BorderLayout.NORTH, name);
		person.addComponent(BorderLayout.CENTER,sex);
		person.addComponent(BorderLayout.SOUTH,mail);
		person.setBorderPainted(false);
		
		Container container = new Container();
		container.setLayout(new BorderLayout());
		container.setBorderPainted(true);
		container.addComponent(BorderLayout.WEST, photo);
		container.addComponent(BorderLayout.CENTER, person);
		container.setWidth(100);
		
		TextArea textArea = new TextArea();
		textArea.setSmoothScrolling(true);
		textArea.setFocus(true);		
		
		TextField textField = new TextField();
		textField.setFocus(true);
		
		f.addComponent(BorderLayout.NORTH, container);
		f.addComponent(BorderLayout.CENTER, textArea);
		f.addComponent(BorderLayout.SOUTH,textField);
		f.show();
	}


	public String getName() {
		return "Amigos";
	}
	public String getIconName() {
		return "Profile";
	}
	
	public void handleAction(byte action, Object param1, Object param2) {
		// TODO Auto-generated method stub
		
	}
	
}

