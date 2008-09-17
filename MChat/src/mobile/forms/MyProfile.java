package mobile.forms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.StringItem;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import mobile.midlet.MainMenu;

public class MyProfile extends Form implements CommandListener {
	MainMenu midlet;
	private Command cmdBack;
	private RecordStore rs;
	final private String REC_STORE = "mcmyprofile";
	final private String REC_RULES = "mcrules";
	private StringItem name;
	private StringItem idade;
	private StringItem sex;
	private StringItem phrase;
	private String pic;
	private ImageItem picture;


	public MyProfile(MainMenu midlet) {
		super("");
		this.midlet = midlet;
		//loadProfile();
		name = new StringItem("Nome: ","",StringItem.PLAIN);
		idade = new StringItem("Idade: ","",StringItem.PLAIN);
		sex = new StringItem("Sexo: ","",StringItem.PLAIN);
		phrase = new StringItem("Frase: ","",StringItem.PLAIN);


		try {
			Image img = Image.createImage("/img.jpg");
			System.out.println("Passo 1");
			picture = new ImageItem(null,img,ImageItem.LAYOUT_CENTER,null);
			System.out.println("Passo 2");
			this.append(picture);
			System.out.println("Passo final");
		} catch (Exception e) {System.out.println("Erro: Imagem Default");}
		this.append(name);
		this.append(idade);
		this.append(sex);
		this.append(phrase);

		cmdBack = new Command("Back", Command.BACK, 1);
		this.addCommand(cmdBack);
		this.setCommandListener(this);
	}

	public void commandAction(Command cmd, Displayable display) {
		if(cmd == this.cmdBack)
		{
			System.out.println("<< Back Command");
			midlet.screenShow(null);
		}

	}

	public boolean loadProfile()
	{
		boolean success = false;
		boolean[] rules = resultRules();
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
		    	String r1 = strmDataType.readUTF();
		    	String r2 = strmDataType.readUTF();
		    	int r3 = strmDataType.readInt();
		    	String r4 = strmDataType.readUTF();
		    	String r5 = strmDataType.readUTF();

		    	name.setText(((rules[0])?r1:"...")+"\n");
		    	idade.setText(((rules[1])?r2:"...")+"\n");
		    	sex.setText(((rules[2])?((r3 == 0)?"masculino":"feminino"):"...")+"\n");
		    	phrase.setText(((rules[3])?r4:"...")+"\n");
		    	pic 	= (rules[4])?r5:"";
		    	strmBytes.reset();
		    	try {
					Image img = Image.createImage(pic);
					picture.setImage(img);
					System.out.println("Pic: "+pic);
				} catch (IOException e) {System.out.println("Error: "+pic);}


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

	private boolean[] resultRules()
	{
		boolean[] result = new boolean[5];
	    try
	    {
	    	rs = RecordStore.openRecordStore(REC_RULES , true);
	    	if(rs.getNumRecords() > 0)
	    	{
		    	byte[] recData = new byte[50];
		    	ByteArrayInputStream strmBytes = new ByteArrayInputStream(recData);
		    	DataInputStream strmDataType = new DataInputStream(strmBytes);

		    	rs.getRecord(1, recData, 0);
		    	result[0] =  strmDataType.readBoolean();
		    	strmBytes.reset();
		    	rs.getRecord(2, recData, 0);
		    	result[1] =  strmDataType.readBoolean();
		    	strmBytes.reset();
		    	rs.getRecord(3, recData, 0);
		    	result[2] =  strmDataType.readBoolean();
		    	strmBytes.reset();
		    	rs.getRecord(4, recData, 0);
		    	result[3] =  strmDataType.readBoolean();
		    	strmBytes.reset();
		    	rs.getRecord(5, recData, 0);
		    	result[4] =  strmDataType.readBoolean();
		    	strmBytes.reset();

		    	strmBytes.close();
		    	strmDataType.close();
		    	System.out.println(result[0]+"\n"+result[1]+"\n"+result[2]+"\n"+result[3]+"\n"+result[4]);
	    	}
     	}
     	catch (Exception e)
     	{
     		e.printStackTrace();
     	}
     	finally
     	{
     		try {
    			rs.closeRecordStore();
    		}
     		catch (RecordStoreNotOpenException e) {e.printStackTrace();}
    		catch (RecordStoreException e) {e.printStackTrace();}
     	}
     	return result;
	}

}
