package mobile.midlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

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
import mobile.ui.*;

public class MainMID extends MIDlet implements ActionListener{

    private static final int EXIT_COMMAND = 1;
    private static final int RUN_COMMAND = 2;
    private static final int BACK_COMMAND = 3;
    private static final int ABOUT_COMMAND = 4;
    private static final Command runCommand = new Command("Run", RUN_COMMAND);
    private static final Command exitCommand = new Command("Exit", EXIT_COMMAND);
    private static final Command backCommand = new Command("Back", BACK_COMMAND);
    private static final Command aboutCommand = new Command("About", ABOUT_COMMAND);
    
    private static final BaseForm[] formApps = new BaseForm[]{
    	new FormChat(),
    	new FormChat(),
    	new FormChat()
    };
    private Hashtable formsHash = new Hashtable();
    
    private static Transition componentTransitions;
    private static Form mainMenu;
    private BaseForm currentForm;
    private int cols;
    private int elementWidth;
    
    
	protected void startApp() throws MIDletStateChangeException {
        try {
            Display.init(this);
            InputStream stream = getClass().getResourceAsStream("/resources.res");
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

	
	
	private void setMainForm(Resources r) {
        UIManager.getInstance().setResourceBundle(r.getL10N("localize", "en"));

        // application logic determins the number of columns based on the screen size
        // this is why we need to be aware of screen size changes which is currently
        // only received using this approach
        mainMenu = new MainScreenForm("BeeHive") {
            protected void sizeChanged(int w, int h) {
                super.sizeChanged(w, h);
                try {
                    setMainForm(Resources.open("/resources.res"));
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
            Image temp = r.getImage(formApps[i].getName() + "_sel.png");
            selectedImages[i] = temp;
            unselectedImages[i] = r.getImage(formApps[i].getName() + "_unsel.png");
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
                	mainMenu.replace(b, b, Transition3D.createRotation(200, true));
                    /*if (componentTransitions != null) {
                        mainMenu.replace(b, b, componentTransitions);
                    }*/
                }

                public void focusLost(Component cmp) {
                }
            });

            formsHash.put(b, formApps[i]);
            elementWidth = Math.max(b.getPreferredW(), elementWidth);
        }

        //Calculate the number of columns for the GridLayout according to the 
        //screen width
        cols = width / elementWidth;
        int rows = formApps.length / cols;
        mainMenu.setLayout(new GridLayout(rows, cols));

        //mainMenu.addComponent(BorderLayout.CENTER, mainContainer);

        mainMenu.addCommand(exitCommand);
        mainMenu.addCommand(aboutCommand);
        mainMenu.addCommand(runCommand);

        mainMenu.setCommandListener(this);
        mainMenu.show();
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
                Form aboutForm = new Form("About");
                aboutForm.setScrollable(false);
                aboutForm.setLayout(new BorderLayout());
                TextArea aboutText = new TextArea(getAboutText(), 5, 10);
                aboutText.setEditable(false);
                aboutForm.addComponent(BorderLayout.CENTER, aboutText);
                aboutForm.addCommand(new Command("Back") {

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
}
