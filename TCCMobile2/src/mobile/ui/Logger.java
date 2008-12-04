package mobile.ui;

import mobile.lib.Util;
import mobile.midlet.MainMenu;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.layouts.BorderLayout;

public class Logger extends Form{
	
	private Form form;
	Command backCommand = new Command("Voltar"){
		public void actionPerformed(ActionEvent evt) {
			form.show();
			super.actionPerformed(evt);
		}
	};
	public Logger(Form form){
		setTitle("Logger...");
		
		this.form = form;
		
		this.setLayout( new BorderLayout() );		
		
		TextArea ta =new TextArea(5,20,TextArea.ANY);
		ta.setEditable(false);		
		
		for(int i=0;i<Util.msgs.size(); i++){
			ta.setText(ta.getText()+"\nL: "+Util.msgs.elementAt(i).toString());			
		}

		this.addComponent( BorderLayout.CENTER, ta );
		this.addCommand(backCommand);
	}
	
}
