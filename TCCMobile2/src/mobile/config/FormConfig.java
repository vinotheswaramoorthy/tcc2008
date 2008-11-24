package mobile.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;

import javax.microedition.lcdui.Displayable;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import mobile.lib.Constants;
import mobile.lib.MobConfig;
import mobile.ui.BaseForm;

import com.sun.lwuit.ButtonGroup;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
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

	private RecordStore rs;
	final private String REC_STORE = "beehiveprofile";
	
	private TextArea txtName;
	private TextArea txtNickname;
	private TextArea txtPic;
	private RadioButton rbM;
	private RadioButton rbF;
	private TextArea txtDesc;
	
	protected void execute(Form f) {

		f.setTitle("Configurações");
		
		f.setLayout(new BorderLayout());
		f.setScrollable(false);
		//f.addComponent(BorderLayout.CENTER,new Label("Configurações"));
		
		Container cntList = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		cntList.setScrollable(true);
		
		txtName = new TextArea();
		txtName.getStyle().setBgTransparency(100);
		
		txtNickname = new TextArea();
		txtNickname.getStyle().setBgTransparency(100);
		
		txtPic = new TextArea();
		txtPic.getStyle().setBgTransparency(100);
		
		cntList.addComponent(createPair("Nome",txtName));
		cntList.addComponent(createPair("Apelido",txtNickname));
		cntList.addComponent(createPair("Foto",txtPic));		
		
		Label lblSexo = new Label("Sexo");
		lblSexo.getStyle().setBgTransparency(0);		
		cntList.addComponent(lblSexo);
		
		Container radioButtonsPanel = new Container(new BoxLayout(BoxLayout.Y_AXIS));		
		rbM = new RadioButton("Masculino");
		rbM.getStyle().setBgTransparency(0);
		radioButtonsPanel.addComponent(rbM);

		rbF = new RadioButton("Feminino");
		rbF.getStyle().setBgTransparency(0);
		radioButtonsPanel.addComponent(rbF);
		
		ButtonGroup bgSexo = new ButtonGroup();
		bgSexo.add(rbM);
		bgSexo.add(rbF);
		
		cntList.addComponent(radioButtonsPanel);
		
		Label lblDesc = new Label("Descrição");
		lblDesc.getStyle().setBgTransparency(0);
		cntList.addComponent(lblDesc);
		int max = Display.getInstance().getDisplayWidth();
		txtDesc = new TextArea();
		txtDesc.setRows(3);
		cntList.addComponent(txtDesc);			
		
		f.addComponent(BorderLayout.CENTER,cntList);
		
		Command cmdSave = new Command("Salvar"){
			public void actionPerformed(ActionEvent evt) {
				super.actionPerformed(evt);
				saveProfile();
				MobConfig.reloadProfile();				
				getMidlet().send(Constants.APP_GENERAL, Constants.CMD_UPDATEINFO, txtNickname.getText());
				Dialog.show("Alerta","Registros salvos com sucesso.","Ok","Cancelar"); 
			}
		};
		f.addCommand(cmdSave);
		
		loadProfile();
	}	

	public String getName() {
		return "Configurações";
	}
	
	public String getIconName() {
		return "Config";
	}
	

	public void handleAction(byte action, Object param1, Object param2) {		
	}
	
	public boolean saveProfile()
	{
		boolean success = false;
		try
		{
			RMSOpen();
	        // Write data into an internal byte array
			ByteArrayOutputStream strmBytes = new ByteArrayOutputStream();
			// Write Java data types into the above byte array
			DataOutputStream strmDataType = new DataOutputStream(strmBytes);

			strmDataType.writeUTF(txtName.getText());
			strmDataType.writeUTF(txtNickname.getText());
			strmDataType.writeUTF(txtPic.getText());	
			strmDataType.writeUTF(txtDesc.getText());
			strmDataType.writeBoolean(rbM.isSelected());
			strmDataType.writeBoolean(rbF.isSelected());			
         	strmDataType.flush();
         	byte[] record = strmBytes.toByteArray();

     		//System.out.println(rs.getNumRecords());
         	if(rs.getNumRecords() == 0)
         		rs.addRecord(record, 0, record.length);
         	else rs.setRecord(1,record, 0, record.length);

     		strmBytes.reset();
			strmBytes.close();
			strmDataType.close();
			success = true;
		}
		catch (Exception e)
		{
	       e.printStackTrace();
     	}
		finally
		{
			RMSClose();
		}
		return success;
	}

	public boolean loadProfile()
	{
		boolean success = false;
	    try
	    {
	    	RMSOpen();
	    	if(rs.getNumRecords() > 0)
	    	{
		    	byte[] recData = new byte[50];
		    	ByteArrayInputStream strmBytes = new ByteArrayInputStream(recData);
		    	DataInputStream strmDataType = new DataInputStream(strmBytes);

		    	//System.out.println(rs.getNumRecords());
		    	rs.getRecord(1, recData, 0);
				
				txtName.setText( strmDataType.readUTF() );
				txtNickname.setText(  strmDataType.readUTF() );
				txtPic.setText(  strmDataType.readUTF() );
				txtDesc.setText( strmDataType.readUTF() );
				rbM.setSelected( strmDataType.readBoolean() );
				rbF.setSelected( strmDataType.readBoolean() );
				
		    	strmBytes.reset();
		    	strmBytes.close();
		    	strmDataType.close();
		    	success = true;
	    	}
     	}
     	catch (Exception e)
     	{
     		e.printStackTrace();
     	}
     	finally
     	{
     		RMSClose();
     	}
     	return success;
	}

	
	private void RMSOpen() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException
	{
		rs = RecordStore.openRecordStore(REC_STORE , true);
	}

	private void RMSClose()
	{
		try {
			rs.closeRecordStore();
		} catch (RecordStoreNotOpenException e) {
			e.printStackTrace();
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
	}

}
