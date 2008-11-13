package mobile.ui;

import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

import mobile.lib.Util;
import mobile.midlet.MainMID;

import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.layouts.BorderLayout;

public class Splash extends Form {

	private Image bgImage;
	private Form nextForm;
	public Splash(){
		//coloca o nome do form
		this.setTitle("");
		//configura o layout do form
		this.setLayout(new BorderLayout());
		
		try {
			bgImage = Image.createImage("/Images/splash1.png");
			
			this.setBgImage(bgImage);
			
		} catch (IOException e) {
			Util.Log("Erro ao carregar imagem.");
		}							
	}
	
	public void show() {
		super.show();	
	}
	
	public void finish(MainMID midlet, Form nextForm){		
		try {
			Thread.currentThread().sleep(2000);
		} catch (InterruptedException e) {}		
		nextForm.show();
	}
}
