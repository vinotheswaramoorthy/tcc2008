package mobile.chat;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import mobile.lib.Constants;
import mobile.lib.GeneralServer;
import mobile.midlet.MainMenu;

public class Chat extends Form implements CommandListener {

	private MainMenu parent; // referencia para o menu

	private Command s // comando para iniciar o aplicativo
	private Command exit; // comando para sair da aplicação
	private Room room;
	private StringItem lbRoom;
	private StringItem lbNick;

	public Chat(MainMenu parent) {
		super("CHAT");
		this.parent = parent;
		append("Teste inicial\n" + "do bate-papo");

		// Formularios
		room = new Room(this, parent);

		// Itens
		lbRoom = new StringItem("Sala", "", Item.PLAIN);
		lbNick = new StringItem("Apelido", "", Item.PLAIN);

		// Comandos
		start = new Command("Iniciar", Command.SCREEN, 1);
		exit = new Command("Sair", Command.EXIT, 1);

		addCommand(start);
		addCommand(exit);

		setCommandListener(this);

	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == start){			
			parent.screenShow(room);
			
			parent.send(Constants.APP_CHAT, Constants.EVENT_JOIN, "Fulano");
		}
		else if (command == exit)
			parent.showMainMenu();
	}

	public void show() {
		parent.screenShow(this);
	}

	public void handleAction(byte event, Object param1, Object param2){
		
		room.handleAction(event, param1, param2);
	}
}
