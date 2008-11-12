package mobile.filetransfer;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import mobile.midlet.MainMenu;

public class FileTransfer implements CommandListener{

	//referencia para o menu principal da solução
	public MainMenu mainMenu;
	
	//lista de opções do menu principal do aplicativo
	private static final String[] elements = new String[]{"Selecionar Arquivos","Procurar Usuários"};	
	
	//objeto list para colocar as opções na tela
	public final List fileTransferMenu = new List("File Transfer Menu", List.IMPLICIT, elements, null);
	
	//tela para seleção dos arquivos
	private SelectFiles selectFiles;
	
	//commando para finalizar a aplicação
	private final Command Exit_CMD = new Command("Fechar", Command.EXIT,2);
	//comando para selecionar uma opção
	private final Command Ok_CMD = new Command("Ok", Command.SCREEN,1);
	
	public FileTransfer(MainMenu parent){
		//configura a referencia para o menu principal
		this.mainMenu = parent;
		
		//adiciona o comando para fechar a aplicação
		fileTransferMenu.addCommand(Exit_CMD);
		//adiciiona o comando para acionar a opção
		fileTransferMenu.addCommand(Ok_CMD);
		
		//configura a lista como recepção de eventos
		fileTransferMenu.setCommandListener(this);

		//apresenta o menu no file transfer
		Display.getDisplay(parent).setCurrent(fileTransferMenu);
	}
	
	public void commandAction(Command cmd, Displayable dsp) {
		//verifica se o comando acionado foi o exit
		if(cmd == Exit_CMD){
			//volta para o menu principal das aplicações
			mainMenu.showMainMenu();
		}
		
		//verifica qual dos itens da lista foi acionado
		switch(fileTransferMenu.getSelectedIndex()){
			//verifica se o evento foi no SelectFiles
			case 0:
				//mensagem de debug
				System.out.println("Seleção de arquivos");
				//instancia a tela de seleção de arquivos
				selectFiles = new SelectFiles(this);
				break;
			//verifica se o evento foi no busca de usuários
			case 1:
				//mensagem de debug
				System.out.println("Busca de usuários");
				break;
		}
	}
}
