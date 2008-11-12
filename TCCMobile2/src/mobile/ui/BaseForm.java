package mobile.ui;

import mobile.midlet.MainMID;

import com.sun.lwuit.*;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.geom.Dimension;

public abstract class BaseForm {

    /**
     * returns the name of the demo to display in the list
     */
    public abstract String getName();
    
    private MainMID _midlet;
    protected MainMID getMidlet(){
    	
    	return _midlet;
    	
    }
    
    /**
     * Invoked by the main code to start the form
     */
    public final void run(final Command backCommand, MainMID midlet) {
        System.gc();
        final Form formItem = new Form(getName());
        
        this._midlet = midlet;
        
        //Adiciona o botão de HELP para todos os formulários
        formItem.addCommand(new Command("Help") {
            public void actionPerformed(ActionEvent evt) {
                Form helpForm = new Form("Help");
                helpForm.setLayout(new BorderLayout());
                TextArea helpText = new TextArea(getHelpImpl(), 5, 10);
                helpText.setEditable(false);
                helpForm.addComponent(BorderLayout.CENTER, helpText);
                Command c = new Command("Back") {
                    public void actionPerformed(ActionEvent evt) {
                    	formItem.show();
                    }
                };
                helpForm.addCommand(c);
                helpForm.setBackCommand(c);
                helpForm.show();
            }
        });
        
        formItem.addCommand(backCommand);
        formItem.setCommandListener(midlet);
        formItem.setBackCommand(backCommand);
        execute(formItem);
        formItem.show();
    }
    
    /**
     * Returns the text that should appear in the help command
     */
    private String getHelpImpl() {
        String h = getHelp();
        return UIManager.getInstance().localize(h, h);
    }

    /**
     * Returns the text that should appear in the help command
     */
    protected String getHelp() {
        // return a key value for localization
        String n = getClass().getName();
        return n.substring(n.lastIndexOf('.') + 1) + ".help";
    }
    
    /**
     * The demo should place its UI into the given form 
     */
    protected abstract void execute(Form f);

    /**
     * Helper method that allows us to create a pair of components label and the given
     * component in a horizontal layout with a minimum label width
     */
    protected Container createPair(String label, Component c, int minWidth) {
        Container pair = new Container(new BorderLayout());
        Label l =  new Label(label);
        Dimension d = l.getPreferredSize();
        d.setWidth(Math.max(d.getWidth(), minWidth));
        l.setPreferredSize(d);
        l.getStyle().setBgTransparency(100);
        pair.addComponent(BorderLayout.WEST,l);
        pair.addComponent(BorderLayout.CENTER, c);
        return pair;
    }
    
      /**
     * Helper method that allows us to create a pair of components label and the given
     * component in a horizontal layout
     */
     protected Container createPair(String label, Component c) {
         return createPair(label,c,0);
     }
    
     public void cleanup() {
     }
    
     public abstract void handleAction(byte action, Object param1, Object param2);
}
