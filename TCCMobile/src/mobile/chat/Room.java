package mobile.chat;

import javax.microedition.lcdui.*;

public class Room extends Form implements CommandListener {

	private Chat parent;
	private Command exit;
	private Command send;
	private Board messageBoard;
	private TextField tfText;

	

	public Room(Chat parent) {
		super("Room");
		this.parent = parent;
		
		// Comandos
		exit = new Command("Sair",Command.EXIT,1);
		send = new Command("Enviar",Command.OK,1);
		
		//Itens
		messageBoard = new Board("Mensagens", this.getWidth(), this.getHeight()-80);
		tfText = new TextField("Falar: ", "", 70, StringItem.PLAIN);
			
		
		
		
		this.append(messageBoard);
		this.append(tfText);
		
		
		addCommand(exit);
		addCommand(send);
		
		setCommandListener(this);
		
		
	}

	
	public void commandAction(Command command, Displayable displayable) {
		if(command == send)
		{
			messageBoard.AppendText(tfText.getString());
			tfText.setString(" ");
		}
		else if(command == exit)
		{
			parent.show();
		}
		
		
	}

}
