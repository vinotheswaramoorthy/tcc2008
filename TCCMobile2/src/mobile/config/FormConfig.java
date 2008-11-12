package mobile.config;

import mobile.ui.BaseForm;

import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;

public class FormConfig extends BaseForm{

	protected void execute(Form f) {

		f.setLayout(new BorderLayout());
		
		f.addComponent(BorderLayout.CENTER,new Label("Configurações"));
		
	}

	public String getName() {
		return "Config";
	}

	public void handleAction(byte action, Object param1, Object param2) {
		// TODO Auto-generated method stub
		
	}

}
