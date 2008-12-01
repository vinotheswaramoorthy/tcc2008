package mobile.midlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.sun.lwuit.*;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.animations.Transition;
import com.sun.lwuit.animations.Transition3D;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.GridLayout;
import com.sun.lwuit.plaf.Style;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.*;

import mobile.chat.FormChat;
import mobile.config.FormConfig;
import mobile.filetransfer.FormFileTransfer;
import mobile.lib.BTListener;
import mobile.lib.Constants;
import mobile.lib.DevicePoint;
import mobile.lib.GeneralServer;
import mobile.lib.MobConfig;
import mobile.lib.ProtoPackage;
import mobile.lib.Util;
import mobile.profile.FormProfile;
import mobile.ui.*;

public class MainMID extends MIDlet implements ActionListener, BTListener{

    private static final int EXIT_COMMAND = 1;
    private static final int RUN_COMMAND = 2;
    private static final int BACK_COMMAND = 3;
    private static final int ABOUT_COMMAND = 4;
    private static final Command runCommand = new Command("Executar", RUN_COMMAND);
    private static final Command exitCommand = new Command("Sair", EXIT_COMMAND);
    private static final Command backCommand = new Command("Voltar", BACK_COMMAND);
    private static final Command aboutCommand = new Command("Sobre", ABOUT_COMMAND);
    
    private static final BaseForm[] formApps = new BaseForm[]{
    	new FormChat(),
    	new FormFileTransfer(),
    	new FormProfile(),
    	new FormConfig()
    };
    private Hashtable formsHash = new Hashtable();
    
	private GeneralServer btServer;
	
	public Vector getDevices(){
		return btServer.getEndPoints();
	}
    
    private static Transition componentTransitions;
    private static Form mainMenu;
    private BaseForm currentForm;
    private int cols;
    private int elementWidth;
    
    public static Enumeration listRoots(){
    	return FileSystemRegistry.listRoots();
    }
    
    //retorna o diretório raiz
    public static String getRoot(){
    	return getRoot();
    }
    
	protected void startApp() throws MIDletStateChangeException {
        try {
            Display.init(this);
            InputStream stream = getClass().getResourceAsStream("/icons.res");
            Resources r2;
            if (stream == null) {
                return;
            } else {
                r2 = Resources.open(stream);
                stream.close();
            }

            //resources are built during the build process
            //open the build.xml file to figure out how to construct the 
            //resource files
            Resources r1 = Resources.open("/businessTheme.res");
            UIManager.getInstance().setThemeProps(r1.getTheme(r1.getThemeResourceNames()[0]));

    		////////////////////////////////////////////////////////////////////////    	                       
            //START BlueTooth SERVER             
    		btServer = new GeneralServer();    		
    		////////////////////////////////////////////////////////////////////////
    		
    		//////////////////////////////////////////////////////////////////////
    		MobConfig.reloadProfile();
    		//////////////////////////////////////////////////////////////////////
            
            setMainForm(r2);
        } catch (Throwable ex) {
            ex.printStackTrace();
            Dialog.show("Exception", ex.getMessage(), "OK", null);
        }

	}
    
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	
	private Transition buttonTrans = Transition3D.createStaticRotation(500, false);
	private void setMainForm(Resources r) {
        UIManager.getInstance().setResourceBundle(r.getL10N("localize", "en"));

        final Splash frmSplash = new Splash();
        frmSplash.show();
        
        
		////////////////////////////////////////////////
		btServer.init("", this);
		
		btServer.query();
		////////////////////////////////////////////////
		
        // application logic determins the number of columns based on the screen size
        // this is why we need to be aware of screen size changes which is currently
        // only received using this approach
        mainMenu = new MainScreenForm("BeeHive") {
            protected void sizeChanged(int w, int h) {
                super.sizeChanged(w, h);
                try {
                    setMainForm(Resources.open("/icons.res"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        //mainMenu.setLayout(new BorderLayout());
        int width = Display.getInstance().getDisplayWidth(); //get the display width 

        elementWidth = 0;

        mainMenu.setTransitionOutAnimator(CommonTransitions.createFade(400));
        mainMenu.setSmoothScrolling(false);

        Image[] selectedImages = new Image[formApps.length];
        Image[] unselectedImages = new Image[formApps.length];

        final ButtonActionListener bAListner = new ButtonActionListener();
        for (int i = 0; i < formApps.length; i++) {
            Image temp = r.getImage(formApps[i].getIconName() + "_Sel.png");
            temp.scale(48,48);
            selectedImages[i] = temp;
            unselectedImages[i] = r.getImage(formApps[i].getIconName() + "_unsel.png");
            unselectedImages[i].scale(48, 48);
            final Button b = new Button(formApps[i].getName(), unselectedImages[i]) {

                public Image getPressedIcon() {
                    Image i = getIcon();
                    return i.scaled((int) (i.getWidth() * 0.8), (int) (i.getHeight() * 0.8));
                }
            };
            b.setRolloverIcon(selectedImages[i]);
            Style s = b.getStyle();
            s.setBorder(null);
            s.setBgTransparency(0);
            s.setBgSelectionColor(0xffffff);
            b.setAlignment(Label.CENTER);
            b.setTextPosition(Label.BOTTOM);
            mainMenu.addComponent(b);
            b.addActionListener(bAListner);
            b.addFocusListener(new FocusListener() {

                public void focusGained(Component cmp) {                	
                    /*if (componentTransitions != null) {
                        mainMenu.replace(b, b, componentTransitions);
                    }*/
                }

                public void focusLost(Component cmp) {
                	//TODO: Testar codigo para efeito de rotacao no botao
                	//mainMenu.replace(b, b, buttonTrans);
                }
            });

            formsHash.put(b, formApps[i]);
            elementWidth = Math.max(b.getPreferredW(), elementWidth);
        }

        //Calculate the number of columns for the GridLayout according to the 
        //screen width
        cols = width / elementWidth;
        int rows = formApps.length / cols;
        mainMenu.setLayout(new GridLayout(2, 2));

        //mainMenu.addComponent(BorderLayout.CENTER, mainContainer);

        mainMenu.addCommand(exitCommand);
        mainMenu.addCommand(aboutCommand);
        mainMenu.addCommand(runCommand);

        mainMenu.setCommandListener(this);

        //mainMenu.show();             
        frmSplash.finish(this, mainMenu);  
    }
	
	
	
	
    public static void setTransition(Transition in, Transition out) {
        mainMenu.setTransitionInAnimator(in);
        mainMenu.setTransitionOutAnimator(out);
    }

    public static void setMenuTransition(Transition in, Transition out) {
        mainMenu.setMenuTransitions(in, out);
        UIManager.getInstance().getLookAndFeel().setDefaultMenuTransitionIn(in);
        UIManager.getInstance().getLookAndFeel().setDefaultMenuTransitionOut(out);
    }

    public static void setComponentTransition(Transition t) {
        componentTransitions = t;
        mainMenu.setSmoothScrolling(false);
    }

    public static Transition getComponentTransition() {
        return componentTransitions;
    }



    private class ButtonActionListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            currentForm = ((BaseForm) (formsHash.get(evt.getSource())));
            currentForm.run(backCommand, MainMID.this);
        }
    }



    public void actionPerformed(ActionEvent evt) {
        Command cmd = evt.getCommand();
        switch (cmd.getId()) {
            case RUN_COMMAND:
                currentForm = ((BaseForm) (formsHash.get(mainMenu.getFocused())));
                currentForm.run(backCommand, this);
                break;
            case EXIT_COMMAND:
                notifyDestroyed();
                break;
            case BACK_COMMAND:
            	currentForm.cleanup();
                mainMenu.refreshTheme();
                mainMenu.show();

                // for series 40 devices
                System.gc();
                System.gc();
                break;
            case ABOUT_COMMAND:
                Form aboutForm = new Form("Sobre");
                aboutForm.setScrollable(false);
                aboutForm.setLayout(new BorderLayout());
                TextArea aboutText = new TextArea(getAboutText(), 5, 10);
                aboutText.setEditable(false);
                aboutForm.addComponent(BorderLayout.CENTER, aboutText);
                aboutForm.addCommand(new Command("Voltar") {

                    public void actionPerformed(ActionEvent evt) {
                        mainMenu.show();
                    }
                });
                aboutForm.show();
                break;
        }
    }
    
    private String getAboutText() {
        return "BEEHIVE bla bla bla bla " +
                "Interface (UI) in Java ME. " +
                "This demo contains inside additional different sub-demos " +
                "to demonstrate key features, where each one can be reached " +
                "from the main screen. For more details about each sub-demo, " +
                "please visit the demo help screen. For more details, please " +
                "contact us at dev.ivan@gmail.com.";
    }
    
    
    ///////////////////////////////////////////////////////////////
    
    public void handleAction(byte action, Object param1, Object param2) {				
		
		DevicePoint endpt = (DevicePoint) param1;
		ProtoPackage pkt = (ProtoPackage) param2;
		
		if( currentForm!=null){
			Util.Log("MaindMID handleAction =  "+ currentForm.getClass().getName());
		}
		
		if( pkt.application == Constants.APP_GENERAL ) {			
			//General must be handled HERE!	
			
			if( pkt.command == Constants.CMD_UPDATEINFO){
				Util.Log("Received UpdateInfo: "+pkt.msg);
				endpt.setNickname(pkt.msg);
			}
			else if( pkt.command == Constants.CMD_FINDROUTE ){
				Vector endPoints = btServer.getEndPoints();
				if( pkt.getData().length<222 ) //222 é o limite, pois os dois últimos endereços (32B) serão do destinatário e desse pacote 
				{
					boolean founded = false;
					//Recupera dispositivos próximos ao dispositivo local
					Enumeration e = endPoints.elements();
					while(e.hasMoreElements()){
						DevicePoint dp = (DevicePoint)e.nextElement();  // Próximo dispositivo
						
						//Identificacao do dispositivo procurado
						String[] deviceList = Util.split(pkt.msg,"|");
						String destName = "";
						if( deviceList.length>0) destName = deviceList[0];
						
						if( dp.remoteName.equalsIgnoreCase(destName)){							
							sendSingle(endpt.remoteName, 
										Constants.APP_GENERAL, 
										Constants.CMD_FINDROUTE_ACK,
										pkt.msg
									);
							founded = true;
						}
					}
					//Se o dispostivo não está na lista do dispositivo atual
					//   envio um broadcast novamente procurando pelo dispositivo
					if(!founded){
						//Se eu já estiver na rota não DEVO continuar
						//    --> Evita loops entre os pacotes
						String[] deviceList = Util.split(pkt.msg,"|");
						if( deviceList.length==2 ){
							String[] routeList = Util.split(deviceList[1],";");
							boolean alreadyInRoute = false;
							for(int i=0;i<routeList.length;i++){
								if( btServer.localName.equalsIgnoreCase(routeList[i]) )
									alreadyInRoute = true;
							}
							//Chequei que não estou na rota, então continuo
							if( !alreadyInRoute){
								send(Constants.APP_GENERAL,
										Constants.CMD_FINDROUTE,
										pkt.msg + ";" +btServer.localName);
							}
						}
					}
				}
			}
			else if (pkt.command == Constants.CMD_FINDROUTE_ACK){
				if( pkt.msg!="" ){
					
					//Divide os dados em 2 (_oprocurado__|____rota_______)
					String[] msgData = Util.split(pkt.msg, "|");
					if( msgData.length>=2) //Deve ter alguma rota, senão está incorreto
					{
						//No 1 argumento tem o dispositivo que está sendo procurado
						String foundDevice = msgData[0];
						//No 2 argumento tem uma lista dos dispositivos em rota
						String[] deviceRoute = Util.split(msgData[1], ";");
						
						if(deviceRoute.length==1){
							//Estou na ultima rota, so a adiciona
							btServer.insertRoute(foundDevice, endpt);													
						}
						else if( deviceRoute.length>1){
							//Adiciona a rota para o dispositivo procurado
							btServer.insertRoute(foundDevice, endpt);

							//Penultimo dispositivo deve receber a rota também
							String lastDevice = deviceRoute[deviceRoute.length-2];

							//Monta o pacote de devolução tirando o último dispositivo (atual)
							String newData = "";
							for(int i=0;i<deviceRoute.length-1;i++){
								newData = deviceRoute[i] + ";";
							}
							sendSingle(lastDevice,Constants.APP_GENERAL, Constants.CMD_FINDROUTE_ACK, newData);
						}
					}									
				}
			}
			
		}
		else if( pkt.application == Constants.APP_CHAT && currentForm!=null && currentForm.getClass().getName().endsWith("FormChat")){
			//TODO: Checar se o form atual eh do Chat... 			
			Util.Log("Pkt will be handle a CHAT action "+ currentForm.getClass().getName());
			currentForm.handleAction(action, param1, param2);
			
		}
		else if( pkt.application == Constants.APP_PROFILE && currentForm!=null && currentForm.getClass().getName().endsWith("FormProfile")){
			Util.Log("Pkt will be handle a PROFILE action "+ currentForm.getClass().getName());
			currentForm.handleAction(action, param1, param2);			
		}
		else if( pkt.application == Constants.APP_FILETRANSFER ){
			FormFileTransfer formFileTransfer = (FormFileTransfer)formApps[1];
			formFileTransfer.handleAction(action, param1, param2);			
		} 			  
	}

    public String getMyDeviceName(){
    	return btServer.localName;
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
	
	public void sendSingle(String deviceName, byte app, byte cmd, String msg){
		
		ProtoPackage senderPkt = new ProtoPackage(
					app,
					cmd, 
					btServer.localName,
					deviceName,
					msg
				); 
		
		Util.Log("Testando o send single " + senderPkt.msg);
		
		btServer.sendPacket(senderPkt, deviceName);		
	}
	///////////////////////////////////////////////////////////////

}
