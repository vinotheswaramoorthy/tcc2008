package mobile.forms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import mobile.midlet.MainMenu;

public class ProfileForm extends Form implements CommandListener {
	MainMenu midlet;
	Alert alert;

	private TextField tbxName;
	private TextField tbxIdade;
	private ChoiceGroup rblSexo;
	private TextField tbxPhrase;
	private TextField tbxPic;

	private Command cmdBack;
	private Command cmdSave;
	private RecordStore rs;
	final private String REC_STORE = "mcmyprofile";

	public ProfileForm(MainMenu midlet) {
		super("");

		this.midlet = midlet;

		tbxName 	= new TextField("Nome:", "", 30, TextField.ANY);
		tbxIdade 	= new TextField("Idade:", "", 4, TextField.ANY);
		rblSexo		= new ChoiceGroup("Sexo:", Choice.EXCLUSIVE);
		rblSexo.append("M",null);
		rblSexo.append("F",null);
		tbxPhrase 	= new TextField("Frase:", "", 125, TextField.ANY);
		tbxPic 		= new TextField("Foto:", "", 50, TextField.ANY);

		this.append(tbxName);
		this.append(tbxIdade);
		this.append(rblSexo);
		this.append(tbxPhrase);
		this.append(tbxPic);


		cmdBack = new Command("Back", Command.BACK, 1);
		cmdSave = new Command("Save", Command.OK, 2);

		this.addCommand(cmdBack);
		this.addCommand(cmdSave);
		this.setCommandListener(this);

		loadProfile();
	}

	public void commandAction(Command cmd, Displayable display) {
		if(cmd == this.cmdBack)
		{
			System.out.println("<< Back Command");
			midlet.screenShow(null);
		}
		else if(cmd == this.cmdSave)
		{
			if(saveProfile())
			{
				alert = new Alert("Edit Profile","Salvo com sucesso!", null,AlertType.INFO);
				alert.setTimeout(2000);
				midlet.screenShow(alert);
			}

		}
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

			strmDataType.writeUTF(tbxName.getString());
			strmDataType.writeUTF(tbxIdade.getString());
			strmDataType.writeInt(rblSexo.getSelectedIndex());
			strmDataType.writeUTF(tbxPhrase.getString());
         	strmDataType.writeUTF(tbxPic.getString());
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

		    	tbxName.setString(strmDataType.readUTF());
		    	tbxIdade.setString(strmDataType.readUTF());
		    	rblSexo.setSelectedIndex(strmDataType.readInt(), true);
		    	tbxPhrase.setString(strmDataType.readUTF());
		    	tbxPic.setString(strmDataType.readUTF());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
