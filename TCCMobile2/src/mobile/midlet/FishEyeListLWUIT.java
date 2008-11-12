package mobile.midlet;
import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.sun.lwuit.Button;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.layouts.GridLayout;
import com.sun.lwuit.plaf.Border;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;


public class FishEyeListLWUIT extends MIDlet implements ActionListener {
	private Image[] icones;
	private Image bgImage;
	private String[] textos;
	private Form fishEyeList;

	public FishEyeListLWUIT() {
		icones = new Image[5];
		textos = new String[5];
		


		
		try {
			icones[0] = Image.createImage("/icons/mnchat.png");
			textos[0] = "Bate-Papo";
			icones[1] = Image.createImage("/icons/mnfiles.png");
			textos[1] = "Rede de Arquivos";
			icones[2] = Image.createImage("/icons/mnfriends.png");
			textos[2] = "Rede de Amigos";
			icones[3] = Image.createImage("/icons/mnconfig.png");
			textos[3] = "Configuração";
			icones[4] = Image.createImage("/semaforo.png");
			textos[4] = "Permissões";

			bgImage = Image.createImage("/Images/background.png");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {}

	protected void pauseApp() {}

	protected void startApp() throws MIDletStateChangeException {
		Display.init(this);
		try {
			  Resources r = Resources.open("/businessTheme.res");
			  UIManager.getInstance().setThemeProps(r.getTheme("businessTheme"));
			} catch (IOException ioe) {
			  System.out.println("Couldn't load theme.");
			}
		fishEyeList = this.montaFishEyeList(icones, textos);

		fishEyeList.show();
		

			
	}

	public void actionPerformed(ActionEvent evt) {
		//Obtendo botão que disparou o evento
		Button b = (Button) evt.getSource();
		//Verificando pelo titulo qual botão é
		if ( b.getText().equals("Calendários")) {
			Alert a = new Alert("Selecionado", "Calendários", null, AlertType.INFO);
			javax.microedition.lcdui.Display.getDisplay(this).setCurrent(a);
		} else if ( b.getText().equals("Internet")) {
			Alert a = new Alert("Selecionado", "Internet", null, AlertType.INFO);
			javax.microedition.lcdui.Display.getDisplay(this).setCurrent(a);
		} else if ( b.getText().equals("Redes")) {
			Alert a = new Alert("Selecionado", "Redes", null, AlertType.INFO);
			javax.microedition.lcdui.Display.getDisplay(this).setCurrent(a);
		} else if ( b.getText().equals("RSS")){
			Alert a = new Alert("Selecionado", "RSS", null, AlertType.INFO);
			javax.microedition.lcdui.Display.getDisplay(this).setCurrent(a);
		} else if ( b.getText().equals("Permissões")){
			Alert a = new Alert("Selecionado", "Permissões", null, AlertType.INFO);
			javax.microedition.lcdui.Display.getDisplay(this).setCurrent(a);
		} else {
			Alert a = new Alert("Erro!!", "Botão não programado!!!!", null, AlertType.ERROR);
			javax.microedition.lcdui.Display.getDisplay(this).setCurrent(a);
		}	
		//definindo focus
		fishEyeList.setFocused(b);
	}

	public Form montaFishEyeList(Image icone[], String[] label) {
		Form fishEyeForm = new Form("FishEye List");
		fishEyeForm.setLayout(new BoxLayout(BoxLayout.Y_AXIS));				
		fishEyeForm.setScrollable(true);
		//fishEyeForm.setLayout(new GridLayout(128,128));
//		try {
//			fishEyeForm.setBgImage(Image.createImage("/Images/background.png"));			
//		} catch (IOException e) {
//			System.out.println("falha no bg");
//		}
		
		//criando os botões
		Button b = null;
		for ( int i = 0; i < icone.length; i++ ) {
			b = new Button(label[i], icone[i].scaled(32, 32));
			b.setSize(new Dimension(128,128));
			b.getStyle().setBgTransparency(0);
			b.getStyle().setBorder(Border.createEmpty());
			b.addActionListener(this);			
			int iconHeight = 32;//icone[i].getHeight();
			int iconWidth = 32;// icone[i].getWidth();
			b.setRolloverIcon(icone[i].scaled((int)(iconWidth*1.3), 
					(int)(iconHeight*1.3)));
			b.setPressedIcon(icone[i].scaled((int)(iconWidth*0.8), 
					(int)(iconHeight*0.8)));
			fishEyeForm.addComponent(b);
		}
		
		return fishEyeForm;
	}
}