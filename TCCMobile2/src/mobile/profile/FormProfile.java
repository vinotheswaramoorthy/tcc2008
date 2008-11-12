package mobile.profile;

import mobile.ui.BaseForm;

import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;

public class FormProfile extends BaseForm{

	protected void execute(Form f) {

		f.setLayout(new BorderLayout());

		f.addComponent(BorderLayout.CENTER,new Label("Perfis"));				
	}

	public String getName() {
		return "Profile";
	}

	public void handleAction(byte action, Object param1, Object param2) {
		// TODO Auto-generated method stub
		
	}
}
