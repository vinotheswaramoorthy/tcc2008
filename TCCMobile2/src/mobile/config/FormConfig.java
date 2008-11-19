package mobile.config;

import javax.microedition.lcdui.Displayable;

import mobile.ui.BaseForm;

import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.RadioButton;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.layouts.GridLayout;
import com.sun.lwuit.layouts.Layout;
import com.sun.lwuit.plaf.UIManager;

public class FormConfig extends BaseForm{

	protected void execute(Form f) {

		f.setTitle("Configurações");
		
		f.setLayout(new BorderLayout());
		f.setScrollable(false);
		//f.addComponent(BorderLayout.CENTER,new Label("Configurações"));
		
		Container cntList = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		cntList.setScrollable(true);
		
		cntList.addComponent(getConfigItem("Nome"));
		cntList.addComponent(getConfigItem("Apelido"));
		cntList.addComponent(getConfigItem("Foto"));		
		
		Label lblSexo = new Label("Sexo");
		lblSexo.getStyle().setBgTransparency(0);		
		cntList.addComponent(lblSexo);
		
		RadioButton rbM = new RadioButton("Masculino");
		rbM.getStyle().setBgTransparency(0);
		cntList.addComponent(rbM);
		
		RadioButton rbF = new RadioButton("Feminino");
		rbF.getStyle().setBgTransparency(0);
		cntList.addComponent(rbF);
		
		Label lblDesc = new Label("Descrição");
		lblDesc.getStyle().setBgTransparency(0);
		cntList.addComponent(lblDesc);
		int max = Display.getInstance().getDisplayWidth();
		TextArea txtDesc = new TextArea();
		txtDesc.setRows(3);
		cntList.addComponent(txtDesc);
		
		
		
		f.addComponent(BorderLayout.CENTER,cntList);
	}
	
	private Component getConfigItem(String label){
	
		/*Container cnt = new Container(new BorderLayout());
		Label lbl = new Label(label);	
		lbl.getStyle().setBgTransparency(0);
		cnt.addComponent( BorderLayout.NORTH, lbl);
		int cols = (Display.getInstance().getDisplayWidth() - lbl.getWidth()) /11;
		TextField txt = new TextField(cols);
		cnt.addComponent( BorderLayout.CENTER, txt);*/
		
		TextArea c = new TextArea();
		c.getStyle().setBgTransparency(100);		
		Container cnt = createPair(label, c);
		
		return cnt;
	}

	public String getName() {
		return "Configurações";
	}
	
	public String getIconName() {
		return "Config";
	}
	

	public void handleAction(byte action, Object param1, Object param2) {
		// TODO Auto-generated method stub
		
	}

}
