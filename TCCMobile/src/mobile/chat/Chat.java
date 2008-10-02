package mobile.chat;

import javax.microedition.lcdui.*;

import mobile.midlet.MainMenu;

public class Chat extends Form implements CommandListener{

	private MainMenu parent;	//referencia para o menu
	
	private Command start;		//comando para iniciar o aplicativo
	private Command exit;		//comando para sair da aplicação
	
	public Chat(MainMenu parent) {
		super("CHAT");
		this.parent = parent;
		append("Teste inicial\n" +
		"do bate-papo");

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
