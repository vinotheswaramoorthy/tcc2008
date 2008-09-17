package mobile.forms;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import mobile.midlet.MainMenu;


public class RulesForm extends Form implements CommandListener {
	MainMenu midlet;
	private ChoiceGroup rules;
	private Command cmdBack;
	private Command cmdSave;
	private RecordStore rs;

	final private String REC_STORE = "mcrules";

	Alert alert;

	public RulesForm(MainMenu midlet) {
		super("");

		this.midlet = midlet;

		String[] choices = {"Nome", "Idade", "Sexo", "Frase", "Foto"};
		rules = new ChoiceGroup("RULES", Choice.MULTIPLE, choices, null);
		this.append(rules);

		cmdBack = new Command("Back", Command.BACK, 1);
		cmdSave = new Command("Save", Command.OK, 2);

		this.addCommand(cmdBack);
		this.addCommand(cmdSave);
		this.setCommandListener(this);

		loadRules();
	}

	public void commandAction(Command cmd, Displayable screen) {
		if(cmd == this.cmdBack)
		{
			System.out.println("<< Back Command");
			midlet.screenShow(null);
		}
		else if(cmd == this.cmdSave)
		{
			if(saveRules())
			{
				alert = new Alert("Edit Profile","Salvo com sucesso!", null,AlertType.INFO);
				alert.setTimeout(2000);
				midlet.screenShow(alert);
			}
		}
	}

	public boolean saveRules()
	{
		boolean success = false;
		try
		{
			rs = RecordStore.openRecordStore(REC_STORE , true);
	        // Write data into an internal byte array
			ByteArrayOutputStream strmBytes = new ByteArrayOutputStream();

			// Write Java data types into the above byte array
			DataOutputStream strmDataType = new DataOutputStream(strmBytes);

			byte[] record;
			boolean isNew = (rs.getNumRecords() != 5);
			for (int i = 0; i < rules.size(); i++) {

				// Write Java data types
	         	strmDataType.writeBoolean(rules.isSelected(i));
	         	// Clear any buffered data
	         	strmDataType.flush();
	         	// Get stream data into byte array and write record
         		record = strmBytes.toByteArray();

	         	if(isNew)
	         		rs.addRecord(record, 0, record.length);
	         	else rs.setRecord(i+1,record, 0, record.length);

		         // Toss any data in the internal array so writes
		         // starts at beginning (of the internal array)
		         strmBytes.reset();
			}

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
		return success;
	}

	public boolean loadRules()
	{
		boolean success = false;
	    try
	    {
	    	rs = RecordStore.openRecordStore(REC_STORE , true);
	    	// Allocate space to hold each record
	    	byte[] recData = new byte[50];
	    	// Read from the specified byte array
	    	ByteArrayInputStream strmBytes = new ByteArrayInputStream(recData);
	    	// Read Java data types from the above byte array
	    	DataInputStream strmDataType = new DataInputStream(strmBytes);

	    	for (int i = 1; i <= rs.getNumRecords(); i++)
	    	{

	    	  //System.out.println(rs.getNumRecords());
	    	  // Get data into the byte array
	    	  rs.getRecord(i, recData, 0);

	    	  // Read back the data types
	    	  rules.setSelectedIndex(i-1,strmDataType.readBoolean());
//	    	  System.out.println(rules.isSelected(i-1));
	    	  // Reset so read starts at beginning of array
	    	  strmBytes.reset();
	       }

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
	     return success;
	}
}
