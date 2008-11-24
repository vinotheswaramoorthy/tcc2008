package mobile.profile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import mobile.midlet.MainMID;

import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.layouts.BorderLayout;

public class FormProfileView extends Form{

	private Form parent;
	private MainMID midlet;
	
	//comando para retornar a tela anterior
	private Command back = new Command("Voltar");	
	//comando para enviar mensagem
	private Command send = new Command("Enviar");
	
	
	public FormProfileView(Form parent, MainMID midlet){
		this.parent = parent;
		this.midlet = midlet;
		
		this.setLayout(new BorderLayout());
		
		Label photo = new Label("");
		Label name = new Label("Nome:\tLeandro Ciola");
		Label sex = new Label("Sexo:\tMasculino... por enquanto");
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
	
		
		this.addComponent(BorderLayout.NORTH, container);
		this.addComponent(BorderLayout.CENTER, textArea);
		this.addComponent(BorderLayout.SOUTH,textField);
	}
	
}
