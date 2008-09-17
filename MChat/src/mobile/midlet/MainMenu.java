package mobile.midlet;


import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import mobile.forms.*;

public class MainMenu extends MIDlet implements CommandListener {

	Alert alert;

	String msg = "";
	private Display display;
	//private Command cmdExit;
	private Command cmdConfig;
	private Command cmdBackMain;
	private List menuMain;
	private List menuConfig;

	private Command cmdMyProfile;
	private Command cmdEditProf;
	private Command cmdEditRules;
	private Command cmdRoom;

	private RulesForm rules;
	private ProfileForm profile;
	private MyProfile myProfile;

	public MainMenu() {
		display = Display.getDisplay(this);

		//cmdExit 		= new Command("Exit", 			Command.EXIT, 	1);
		cmdConfig 		= new Command("Select", 		Command.SCREEN, 1);
		cmdBackMain		= new Command("Back",			Command.BACK, 	1);
		cmdEditProf		= new Command("Edit Profile",	Command.BACK, 	1);
		cmdEditRules	= new Command("Edit Rules",		Command.BACK, 	1);
		cmdRoom			= new Command("Roomm",			Command.BACK, 	1);

		/*****************************************/

		String[] optMain = {"My Profile", "My Scrapts", "Room", "Config"};
		menuMain = new List("MENU", List.IMPLICIT, optMain, null);
//		menuMain.addCommand(cmdConfig);
//		menuMain.addCommand(cmdMyProfile);
//		menuMain.addCommand(cmdRoom);
		menuMain.setCommandListener(this);

		/*****************************************/

		String[] optConfig = {"Edit Profile", "Edit Rules"};
		menuConfig = new List("CONFIG", List.IMPLICIT, optConfig, null);
		menuConfig.addCommand(cmdBackMain);
		menuConfig.addCommand(cmdEditProf);
		menuConfig.addCommand(cmdEditRules);
		menuConfig.setCommandListener(this);

		/*****************************************/

		myProfile 	= new MyProfile(this);
		profile 	= new ProfileForm(this);
		rules 		= new RulesForm(this);



	}

	protected void startApp() throws MIDletStateChangeException {
		display.setCurrent(menuMain);
//		Enumeration drives = FileSystemRegistry.listRoots();
//		msg = "The valid roots found are:\n";
//		while(drives.hasMoreElements()) {
//		msg += "\t"+(String) drives.nextElement();
//
//		}
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	public void commandAction(Command cmd, Displayable screen) {
		if(cmd == List.SELECT_COMMAND)
		{
			if(screen == this.menuMain)
			{
				System.out.println("entrei main");
				switch(menuMain.getSelectedIndex())
				{

					case 0: cmd = this.cmdMyProfile;System.out.println("Select: "+menuMain.getSelectedIndex()); break;
					case 1: cmd = this.cmdRoom;System.out.println("Select: "+menuMain.getSelectedIndex()); break;
					case 2: cmd = this.cmdRoom;System.out.println("Select: "+menuMain.getSelectedIndex()); break;
					case 3: cmd = this.cmdConfig;System.out.println("Select: "+menuMain.getSelectedIndex()); break;
				}
			}
			else if(screen == this.menuConfig)
			{
				System.out.println("entrei config");
				switch(menuConfig.getSelectedIndex())
				{
					case 0: cmd = this.cmdEditProf; break;
					case 1: cmd = this.cmdEditRules; break;
				}
			}
		}

		if(cmd == this.cmdConfig)
		{
			this.screenShow(menuConfig);
		}
		else if(cmd == this.cmdBackMain)
		{
			this.screenShow(null);
		}
		else if(cmd == this.cmdMyProfile)
		{
			System.out.println("entrei myProf");
			myProfile.loadProfile();
			this.screenShow(myProfile);
		}
		else if(cmd == this.cmdEditProf)
		{
			System.out.println("entrei edtProf");
			this.screenShow(profile);
		}
		else if(cmd == this.cmdEditRules)
		{
			System.out.println("entrei rules");
			this.screenShow(rules);
		}
		else if(cmd == this.cmdRoom)
		{
			//deleteALLRS();
			System.out.println("entrei room");
			alert = new Alert("...",msg,null, AlertType.INFO);
			alert.setTimeout(Alert.FOREVER);
			this.display.setCurrent(alert);

		}
	}


	public void screenShow(Displayable diplay) {
		if (diplay==null)
			display.setCurrent(menuMain);
		else
		display.setCurrent(diplay);
	}




//	private void deleteALLRS()
//	{
////		RecordStore rs;
////		try {
////			String rec[] = rs.listRecordStores();
////			for (int i = 0;i<rec.length;i++)
////			{
////				System.out.println(rec[i]);
////				//rs.closeRecordStore();
////				rs.deleteRecordStore(rec[i]);
////			}
////		} catch (Exception e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////			}
//	}

}
