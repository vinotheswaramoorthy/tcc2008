package mobile.filetransfer;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

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

public class SelectFiles implements CommandListener{
	
	//referencia para o menu anterior
	private final FileTransfer parent;
	
	//Abrir o arquivo
	private Command View_CMD = new Command("Abrir",Command.OK,1);
	//Compartilha o arquivo 
	private Command Share_CMD = new Command("Compartilhar",Command.OK,1);
	//Descompartilha o arquivo
	private Command UnShare_CMD = new Command("Descompartilhar",Command.OK,1);
	//comando para fechar o aplicativo
	private Command Exit_CMD = new Command("Exit",Command.EXIT,2);
	
	//diretorio que contem todos os roots
	private static final String MEGA_ROOT = "/";
	
	/* separador de diretórios definido pela FileConnection */
    private static final String SEP_STR = "/";
    private static final char SEP = '/';

    /* special string denotes upper directory */
    private static final String UP_DIRECTORY = "..";
    
    //armazena o diretorio atual
    private String currDirName;
    
    //vetor para os nomes dos arquivos disponibilizados
    private Vector sharedFiles;
    
    //imagem de diretorio
    private Image dirIcon;
    //imagem de arquivo
    private Image unshareFileIcon;
    //imagem de arquivo compartilhado
    private Image shareFileIcon;
    
	public SelectFiles(FileTransfer parent) {
		//cria a referencia para a tela anterior
		this.parent = parent;
		
		//configura o diretorio atual como raiz
        currDirName = MEGA_ROOT;

        try {
        	//cria o icone de pasta
            dirIcon = Image.createImage("/icons/dir.png");
        } catch (IOException e) {
            dirIcon = null;
        }

        try {
        	//cria o icone de arquivo descompartilhado
            unshareFileIcon = Image.createImage("/icons/file.png");
        } catch (IOException e) {
            unshareFileIcon = null;
        }		
        
        try{
        	//cria o icone de arquivo compartilhado
        	shareFileIcon = Image.createImage("/icons/shared1.png");
        } catch(IOException e){
        	shareFileIcon = null;
        }
        
        //instancia o vetor para armazenar os nomes dos arquivos compartilhados
        sharedFiles = new Vector();
        
        //apresenta os diretorios atuais
        showCurrDir();
	}
	
	public void commandAction(Command cmd, Displayable dsp) {
		//faz um cast com o displable recebido do evento
        List lst = (List)dsp;
        int index =  lst.getSelectedIndex();

        System.out.println(currDirName + lst.getString(lst.getSelectedIndex()));
        
		//verifica se o comando recebido é para abrir um diretório
		if (cmd == View_CMD) {
			//pega o nome do item selecionado
            final String currFile = lst.getString(lst.getSelectedIndex());
            //instancia um thread para abrir o arquivo ou diretório, pois é necessário que esteja
            // em um metodo start
            new Thread(new Runnable() {
            				public void run() {
            					//verifica se nome termina com uma barra ou se ".." para voltar ao diretorio anterior
            					if (currFile.endsWith(SEP_STR) || currFile.equals(UP_DIRECTORY)) {
            						traverseDirectory(currFile);
            					}
            				}	
            			}
           ).start();//inicializa a thread	
        }
		
		//verifica se o comando recebido é para terminar o aplicativo
        if (cmd == Exit_CMD){
        	//exibe a tela principal dos aplicativos
        	parent.mainMenu.showMainMenu();
        	return;
        }

        //verifica se o comando recebido é para compartilhar o arquivo
        if (cmd == Share_CMD) {
        	//altera o item atualmente selecionado
        	lst.set(index, lst.getString(index), shareFileIcon);       
        	//inclui o nome do arquivo compartilhado na lista
        	System.out.println(lst.getString(index));
        	//sharedFiles.add(lst.getString(index));
        	return;
        }
        
        //verifica se o comando recebido é para descompartilhar o item selecionado
        if (cmd == UnShare_CMD){
        	//altera o icone do arquivo atualmente selecionado
        	lst.set(index, lst.getString(index), unshareFileIcon);
        	//remove o nome do arquivo da lista de arquivos compatilhados
        	sharedFiles.removeElement(lst.getString(index));
        	return;
        }        
	}
	
    /**
     * Mostra a lista de arquivo do diretório atual
     */
    void showCurrDir() {
    	//para listar os diretorios
    	Enumeration e;
    	//para abrir a conexão
        FileConnection currDir = null;
        //o list para mostrar os arquivos é local, pois sempre deve recarregar os menus
        List browser;

        try {
        	//verifica se o diretorio atual é o raiz
            if (MEGA_ROOT.equals(currDirName)) {
            	//pega a lista carregada no midlet
                e = parent.mainMenu.listRoots;
                //cria a lista para mostrar os arquivos
                browser = new List(currDirName, List.IMPLICIT);
            } else {
            	//pega a url do diretorio atual
                currDir = (FileConnection)Connector.open("file://localhost/" + currDirName);
                //cria a lista de diretorios e arquivos 
                e = currDir.list();
                //carrega a lista com o conteúdo do diretorio
                browser = new List(currDirName, List.IMPLICIT);
                // not root - draw UP_DIRECTORY
                browser.append(UP_DIRECTORY, dirIcon);
            }

            //varre até passar por todos os elementos da diretório
            while (e.hasMoreElements()) {
            	//pega o nome do elemento atual
                String fileName = (String)e.nextElement();
                //verifica se é um diretório
                if (isPath(fileName)){
                	//se termina com barra significa que é um diretório
                    browser.append(fileName, dirIcon); //coloca o icone de pasta
                } else {
                    //caso contrário é um arquivo
                    browser.append(fileName, unshareFileIcon);//coloca o icone de arquivo
                }
            }

            //adiciona o command que permite abrir o diretório
            browser.setSelectCommand(View_CMD);
            //adiciona o command que permite fechar o aplicativo
            browser.addCommand(Exit_CMD);

            
            //verifica se está no diretório raiz
            if (!MEGA_ROOT.equals(currDirName)) {
                browser.addCommand(Share_CMD);
                browser.addCommand(UnShare_CMD);
            }
            
            //configura o list como listener de eventos
            browser.setCommandListener(this);

            //verifica se o diretório apontado pelo fileconnection é nulo
            if (currDir != null) {
            	//caso seja nulo, fecha o file connection
                currDir.close();
            }
            
            //carrega o list no display
            Display.getDisplay(parent.mainMenu).setCurrent(browser);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    
    void traverseDirectory(String fileName) {
        /* neste caso o diretório apenas muda o endereço e apresenta
         */
        if (currDirName.equals(MEGA_ROOT)) {
            if (fileName.equals(UP_DIRECTORY)) {
                //caso seja o diretório raiz, nao tem para onde voltar
                return;
            }
            //configura o diretório atual com a url passada como argumento
            currDirName = fileName;
        //verifica se a url passada é para voltar para o diretório anterior
        } else if (fileName.equals(UP_DIRECTORY)) {
            //volta para o diretório superior
        	//pega o proximo nome da url antes de encontrar outra barra
            int i = currDirName.lastIndexOf(SEP, currDirName.length() - 2);

            //verifica se há um diretorio anterior
            if (i != -1) {
            	//altera o diretório atual para o encontrado na url
                currDirName = currDirName.substring(0, i + 1);
            } else {
            	//configura o diretório atual como o raiz
                currDirName = MEGA_ROOT;
            }
        } else {
        	//caso tenha entrado em um nivel abaixo no diretório
            currDirName = currDirName + fileName;
        }
        
        //mostra o diretorio atual
        showCurrDir();
    }
    
    private boolean isPath(String url){
        if (url.charAt(url.length() - 1) == SEP) 
        	return true;
        else
        	return false;
    }
}
 	