package mobile.filetransfer;

import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

import mobile.midlet.MainMenu;

public class SelFiles extends Form implements CommandListener, Runnable{

	private Form parent;		//form que chamou esta tela
	private MainMenu main;		//referencia para o menu principal
	private Image dirIcon;		//icone de diretorio
	private Image fileIcon;		//icone de arquivo
	private Image[] iconList;	//lista de icones
	
	private Command exit;		//comando para fechar a tela
	private Command back;		//comando para voltar para a tela anterior
	
	//constante com do diretorio raiz
	private static final String ROOT = "/";
    //constante com o separador de diretórios
    private static final String SEP_STR = "/";
    // variavel para armazenar o diretorio atual
    private String currDirName;
	
    /**
     * Construtor da classe
     * @param Form parent
     * @param MIDLet main
     */
    public SelFiles(Form parent, MIDlet main) {
		//configura o titulo da tela
		super("Seleciona arquivos");
		
		//configura a tela de onde foi chamada
		this.parent = parent;
		//configura a referencia para a tela principal
		this.main = (MainMenu) main;
		
		//inicializa o diretório atual para o raiz
		this.currDirName = ROOT;
		
		//carrega a imagem de diretorio
		try {
			dirIcon = Image.createImage("/icons/dir.png");
		} catch (IOException e) {
			dirIcon = null;
		}
		
		//carrega a imagem de arquivo
		try {
			fileIcon = Image.createImage("/icons/file.png");
		} catch (IOException e) {
			fileIcon = null;
		}
		
		//cria a lista com as imagens
		iconList = new Image[]{dirIcon, fileIcon};
		
		//cria os comandos da tela
		exit = new Command("Fechar",Command.BACK,1);
		back = new Command("Voltar",Command.BACK,1);
		//adiciona os comandos a tela 
		addCommand(exit);
		addCommand(back);
		
		//configura o form para receber os eventos
		setCommandListener(this);
		
		//showCurrDir();
	}
    
    public void run(){
    	showCurrDir();
    }
    
    private void showCurrDir(){
    	Enumeration e;					//objeto para listar os arquivos
    	FileConnection currDir = null;	//objeto para abrir o diretório
    	ChoiceGroup browser;					//lista os arquivos e diretórios
    	
    	try{
    		//verifica se o diretório atual é o root
	    	if(ROOT.equals(currDirName)){
	    		//coloca a lista de diretorios no enumerator
	    		e = main.listRoots;
	    		//cria o lista para mostrar os diretórios
	    		browser = new ChoiceGroup(currDirName, ChoiceGroup.MULTIPLE);
	    	}
	    	else{
	    		//carrega a lista de arquivos do diretorio atual
	    		currDir = (FileConnection)Connector.open("file://localhost/" + currDirName);
	    		//coloca a lista carregada no enumerator
	    		e = currDir.list();
	    		//cria a lista para exibir os arquivos na tela
	    		browser = new ChoiceGroup("..",ChoiceGroup.MULTIPLE);
	    		//adiciona um item para voltar ao diretorio anterior
	    		browser.append("..", dirIcon);
	    	}
	    	
	    	//varre todos os itens do diretórios
            while (e.hasMoreElements()) {
            	//pega o próximo arquivo do enumerator 
                String fileName = (String)e.nextElement();
                //verifica se o elemento encontrado é um diretório
                if (fileName.charAt(fileName.length() - 1) == '/') {
                	// coloca o nome do diretório atual
                    browser.append(fileName, dirIcon);
                } else {
                    // aciona um arquivo
                    browser.append(fileName, fileIcon);
                }
            }
            //exibe a lista de arquivos e diretórios
            this.append(browser);
            //Display.getDisplay(main).setCurrent(browser);
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    	}
    }

	
	/**
	 * tratamento de eventos recebidos no form
	 */
	public void commandAction(Command command, Displayable displayable) {
		//verifica se evento recebido foi o exit
		if(command == exit){
			main.showMainMenu();		//volta para a tela principal da aplicação
		}
		//verifica se o evento recebido foi o back
		if(command == back){
			showCurrDir();
			//main.screenShow(parent);	//volta para a tela anterior
		}
	}
}
