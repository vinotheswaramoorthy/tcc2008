package mobile.filetransfer;

import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.io.file.FileConnection;
import javax.microedition.io.*;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;

import mobile.midlet.MainMenu;

public class FileTransfer extends Form implements CommandListener{

	private MainMenu main;		//referencia para o menu
	private Command start;		//comando para iniciar o aplicativo
	private Command exit;		//comando para sair da aplicação
	
	private SelFiles selFile;	//objeto para selecionar os arquivos
	
	private List fileTransferMenu;	//list o menu principal do aplicativo
	private Command cmdSelFiles		= new Command("SelFiles", Command.SCREEN, 1);	
	private Command cmdSearchUser	= new Command("SearchUser", Command.SCREEN, 1);
	private Command cmdSendFiles	= new Command("SendFiles", Command.SCREEN, 1);
		
	//constantes do menu principal
	private final int	SELECT_FILES = 0;	//constante para chamar a tela de procura de arquivos
	private final int 	SEARCH_USERS = 1;	//constante para chamar a tela de procura de usuários
	private final int 	SEND_FILES = 2;		//constante para chamar a tela de enviar arquivos
	
	public FileTransfer(MainMenu parent) {
		//chama o construtor do form
		super("FileTransfer");
		//cria o vinculo com o form anterior
		this.main = parent;
				
		//texto para apresentação inicial
		append("Teste inicial\n" +
				"do compartilhamento de arquivos");
	
		//comando para iniciar a execução do programa
		start = new Command("Iniciar",Command.SCREEN,1);
		//commando para voltar para o menu principal
		exit  = new Command("Sair",Command.EXIT,1);
		
		//adiciona os comandos os form
		addCommand(start);
		addCommand(exit);
		
		//cria a lista de opções do aplicativo
		showFileTransferMenu();
		
		//configura o form como listener de eventos
		setCommandListener(this);
	}

	public void showFileTransferMenu(){
		//cria a lista de opções
		String[] optFileTransfer = {"Selecionar arquivos","Procurar usuários","Enviar arquivos"};
		//verifica se o object fileTranferMenu é nulo
		if(fileTransferMenu == null){
			fileTransferMenu = new List("FileTranfer",List.IMPLICIT,optFileTransfer,null);	//cria a lista
		}
		main.screenShow(fileTransferMenu);
		fileTransferMenu.setCommandListener(this);
	}
		
	public void commandAction(Command command, Displayable displayable) {
		//verifica se o commando recebido veio de um list
		if(command == List.SELECT_COMMAND){
			//pega o item da lista que foi selecionado
			switch(fileTransferMenu.getSelectedIndex()){
				case SELECT_FILES://caso seja a seleção de arquivos	
						selFile = new SelFiles(this,main);
						main.screenShow(selFile);
					break;
				case SEARCH_USERS://caso seja a procura de arquivos
					
					break;
				case SEND_FILES://caso seja o envio de arquivos

					break;
			}
		}
		
		//verifica se é o comando para abrir a tela de seleção de arquivos
		if(command == cmdSelFiles)
			System.out.println("Teste");
		
		//verifica se é o comando para finalizar a aplicação
		if(command == exit)
			main.showMainMenu();	//chama o comando para voltar para o menu principal
		
		//verifica se o comando recebido é o start
		if(command == start)
			main.screenShow(fileTransferMenu);	//chama a tela de opções
	}
}
