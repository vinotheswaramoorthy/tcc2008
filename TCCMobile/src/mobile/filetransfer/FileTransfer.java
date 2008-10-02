package mobile.filetransfer;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import mobile.midlet.MainMenu;

public class FileTransfer extends Form implements CommandListener{

	private MainMenu parent;	//referencia para o menu
	
	private Command start;		//comando para iniciar o aplicativo
	private Command exit;		//comando para sair da aplicação
	
	public FileTransfer(MainMenu parent) {
		super("FileTransfer");
		this.parent = parent;
		
		append("Teste inicial\n" +
				"do compartilhamento de arquivos");
		
		start = new Command("Iniciar",Command.SCREEN,1);
		exit  = new Command("Sair",Command.EXIT,1);
		
		addCommand(start);
		addCommand(exit);
		
		setCommandListener(this);
	}

	public void commandAction(Command command, Displayable displayable) {
		if(command == exit)
			parent.showMainMenu();
	}

}
