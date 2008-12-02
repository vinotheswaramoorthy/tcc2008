package mobile.chat;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import mobile.lib.*;
import mobile.midlet.MainMenu;

public class Room extends Form implements CommandListener {

	private Chat parent;
	private Command exit;
	private Command send;
	private Board messageBoard;
	private TextField tfText;
	private MainMenu midlet;

	public Room(Chat parent, MainMenu midlet) {
		super("Room");
		this.parent = parent;
		this.midlet = midlet;

		// Comandos
		exit = new Command("Sair", Command.EXIT, 1);
		send = new Command("Enviar", Command.OK, 1);

		// Itens
		messageBoard = new Board("Mensagens", this.getWidth(),
				this.getHeight() - 80);
		tfText = new TextField("Falar: ", "", 70, Item.PLAIN);

		messageBoard.setBackgroundColor(0xffff00);
		messageBoard.setBorderColor(0xff0000);
		messageBoard.setFont(Font.getFont(Font.FACE_PROPORTIONAL,
				Font.STYLE_BOLD, Font.SIZE_LARGE));
		messageBoard.setTextColor(0xa0a000);
		this.append(messageBoard);
		this.append(tfText);

		addCommand(exit);
		addCommand(send);

		setCommandListener(this);

	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == send) {
			String msg = tfText.getString();
			messageBoard.AppendText(msg);

			midlet.send(Constants.APP_CHAT, Constants.EVENT_RECEIVED, msg);

			tfText.setString("");

		} else if (command == exit) {
			midlet.send(Constants.APP_CHAT, Constants.EVENT_LEAVE, "out");
			parent.show();
		}
	}

	public void handleAction(byte event, Object param1, Object param2) {
		Util.Log("invoke CHAT handleAction. action=" + event);

		if (event == Constants.EVENT_JOIN) {

			// a new user has join the chat room
			DevicePoint endpt = (DevicePoint) param1;
			ProtoPackage pkt = (ProtoPackage) param2;

			String msg = pkt.sender + " entrou na sala";

			// display the join message on screen
			messageBoard.AppendText(msg);

		} else if (event == Constants.EVENT_SENT) {
			// nothing to do
		} else if (event == Constants.EVENT_RECEIVED) {
			// a new message has received from a remote user
			DevicePoint endpt = (DevicePoint) param1;
			ProtoPackage pkt = (ProtoPackage) param2;
			// render this message on screen
			messageBoard.AppendText(pkt.getMsg());

		} else if (event == Constants.EVENT_LEAVE) {
			// a user has leave the chat room
			DevicePoint endpt = (DevicePoint) param1;
			String msg = endpt.remoteName + " sai da sala";
			// ProtoPackage packet = new ProtoPackage(Constants.CMD_TERMINATE,
			// endpt.remoteName, msg );
			// display the leave message on screen
			messageBoard.AppendText(msg);
		}
	}

}
