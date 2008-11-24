package mobile.filetransfer;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.midlet.MIDlet;

import mobile.lib.Util;
import mobile.midlet.MainMID;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Border;
import com.sun.perseus.j2d.Box;

public class FormSelFiles extends Form implements ActionListener{
 
	//referencia para o form que abriu este form
	private FormSharedFiles parent;
	//objeto para listar os arquivos e diretórios
	private List list;
	//vetor para carregar a lista de arquivos no list
	//private Vector items;

	private static final String BASE_DIRECTORY = "file://localhost/";
	
	//diretorio que contem todos os roots
	private static final String MEGA_ROOT = "/";
	
	/* separador de diretórios definido pela FileConnection */
    private static final String SEP_STR = "/";
    private static final char SEP = '/';

    /* special string denotes upper directory */
    private static final String UP_DIRECTORY = "..";
    
    //armazena o diretorio atual
    private String currDirName;

    //comando para compartilhar o arquivo
    private Command share = new Command("Compartilhar",4);
    //comando para descompartilhar o arquivo
    private Command unshare = new Command("Descompartilhar",3);;
	//comando para retornar para o parent
	private Command back = new Command("Voltar",1);

	
	/**
	 * Construtor
	 * @param parent
	 */
	public FormSelFiles(FormSharedFiles parent){
		//configura o nome da tela
		this.setTitle("Compartilhar Arquivos");
		//salva a referencia para a tela anterior
		this.parent = parent;
		
		this.setLayout(new BorderLayout());
		
		//configura o diretorio atual como raiz
        currDirName = MEGA_ROOT;
        		
		//carrega os diretórios raiz
		showCurrDir();
		
		//altera o tipo de transição
        this.setTransitionOutAnimator(CommonTransitions.createFade(400));
        //
        this.setSmoothScrolling(false);
		
		//insere o comando ao form
		this.addCommand(back);
		//insere o comando para descompartilhar o arquivo
		this.addCommand(unshare);
		//insere o comando para compartilhar o arquivo
		this.addCommand(share);
		//configura o form para receber os eventos
		this.setCommandListener(this);
	}
	
	/**
	 * Tratamento dos eventos gerados neste form
	 */
	public void actionPerformed(ActionEvent evt) {
		//atende o comando para retornar para a tela anterior
		if(back == evt.getSource()){
			parent.reshow();
		}
		
		//atende o comando para compartilhar o arquivo
		if(share == evt.getSource()){
			//chama o método para compartilhar o arquivo
			shareCommand(evt.getSource());
		}
		
		if(unshare == evt.getSource()){
			//chama o metodo para descompartilhar o arquivo
			unShareCommand(evt.getSource());
		}
		
		if (evt.getSource() == list) {
			//pega o nome do item selecionado
            final String currFile = (String)list.getSelectedItem();
            
            //verifica se o item selecionado é uma pasta
            if (Util.isPath(currFile)){
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
            else{
            	//verifica se o arquivo selecionado já está compartilhado
            	if(parent.getVectorItems().indexOf(list.getSelectedItem()) != -1){
            		//caso já esteja compartilhado, descompartilha
            		unShareCommand(list.getSelectedItem());
            	}
            	else{
            		//caso não esteja compartilhado, compartilha o arquivo
            		shareCommand(list.getSelectedItem());
            	}	
            }
		}
	}
	
	private void shareCommand(Object source){
		//comando para fechar o alert
		Command ok = new Command("Ok");
		//verifica se o item seleciona é uma pasta
		if(Util.isPath(list.getSelectedItem().toString())){
            Dialog.show("Erro ao compartilhar arquivo", "Não é possível compartilhar uma pasta", ok,
                   new Command[]{ok}, Dialog.TYPE_ERROR, null, 
                    0, CommonTransitions.createFade(400));
		}
		else{
			//coloca o novo item na lista de arquivos disponibilizados
			parent.put(list.getSelectedItem(),BASE_DIRECTORY + currDirName + list.getSelectedItem());
			//atualiza a tela para trocar o icone normal com o de arquivo compartilhado
			showCurrDir();
		}	
	}
	
	private void unShareCommand(Object source){
		//remove o arquivo da lista de arquivos compartilhados
		parent.remove(list.getSelectedItem());
		//atualiza a  tela para trocar o icone de arquivo compartilhado com o de arquivo normal
		showCurrDir();
		//imprime log com o nome do arquivo que foi compartilhado
	}
	
	 /**
     * Mostra a lista de arquivo do diretório atual
     */
    void showCurrDir() {
    	//enumeration para mostrar os itens na pasta
    	Enumeration listDir;
    	//Vector para carregar os arquivos do enumeration
    	Vector vectorEnum = new Vector();
    	//para abrir a conexão
        FileConnection currDir = null;
        
        try {
        	//verifica se o diretorio atual é o raiz
            if (MEGA_ROOT.equals(currDirName)) {
            	//pega a lista carregada no midlet
           		listDir = MainMID.listRoots();            	
            } else {
            	//pega a url do diretorio atual
                currDir = (FileConnection)Connector.open(BASE_DIRECTORY + currDirName);
                //cria a lista de diretorios e arquivos 
                listDir = currDir.list();
                //adiciona updirectory
                vectorEnum.addElement("..");
            }  
            
            //carrega os itens do enumeration em um vector
            while(listDir.hasMoreElements())
            	vectorEnum.addElement(listDir.nextElement());
                       
            //cria a lista passando o vector com a lista como argumento
            list = new List(vectorEnum);
            //configura para não mostrar a borda
            list.setBorderPainted(false);
            list.setInputOnFocus(true);
            //altera o tipo de navegação
            list.setFixedSelection(List.FIXED_NONE_CYCLIC);
            //permite o scroll
            list.setSmoothScrolling(true);
            //configura o fundo do list como transparente
            list.getStyle().setBgTransparency(0);
            //adiciona o descritor do list
            list.setListCellRenderer(new ButtonsList());
            //configura o list como listener de eventos
            list.addActionListener(this);
            //requisita o foco para o para o list
            list.requestFocus();
            
            
            //retira todos os componentes da tela
            this.removeAll();
            //coloca o list na tela
    		this.addComponent(BorderLayout.CENTER, list);
    		//nao sei o que esse metodo faz
            this.revalidate();
            this.repaint();
    		
            //verifica se o diretório apontado pelo fileconnection é nulo
            if (currDir != null) {
            	//caso seja nulo, fecha o file connection
                currDir.close();
            }
            
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }    
	
    void traverseDirectory(String fileName) {
 
    	// verifica se está no raiz dos diretórios
        if (currDirName.equals(MEGA_ROOT)) {
            if (fileName.equals(UP_DIRECTORY)) {
                //caso seja o diretório raiz, nao tem para onde voltar
            	return;
            }
            //configura o diretório atual com a url passada como argumento
            currDirName = fileName;
        } 
        //verifica se a url passada é para voltar para o diretório anterior
        else if (fileName.equals(UP_DIRECTORY)) {
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
    
    class ButtonsList extends Button implements ListCellRenderer{

    	//vetor para armazenar os icones
    	private Image[] images;
    	
    	public ButtonsList(){
    		//cria o vetor
            images = new Image[3];
            try {
            	//carrega a imagem da pasta
                images[0] = Image.createImage("/icons/Folder32.png");
                //carrega a imagem do arquivo 
                images[1] = Image.createImage("/icons/File32.png");
                //carrega a imagem do arquivo compartilhado
                images[2] = Image.createImage("/icons/FileShared32.png");
            } catch (IOException ex) {
            	//imprime mensagem de erro no prompt
                System.out.println("Não foi possível carregar os icones");
            }
    	}
    	
		public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            //coloca nome do arquivo no texto do botão
			setText(value.toString());
			//alinha o conteúdo do texto à esquerda
            setAlignment(LEFT);
            //configura para não apresentar as linhas da borda
            setBorderPainted(false);
            //configura o botão para que seja selecionavel
            setFocusable(true);
            //coloca o foto no botão
            setFocus(true);
            //verifica se o item seleciona é uma pasta ou comando para ir ao diretorio anterior
            if (Util.isPath(value.toString()) || value.toString() == UP_DIRECTORY) {
                //configura o foco
            	setFocus(true);
            	//coloca a imagem da pasta no botão
                setIcon(images[0]);
                //configura o fundo do botão como transparente
                getStyle().setBgTransparency(0);
            } else {
            	//verifica se este arquivo já está comparilhado
            	if(parent.getVectorItems().indexOf(value) != -1){
            		//configura o botão para receber foco
            		setFocus(true);
            		//coloca a imagem de arquivo compartilhado
            		setIcon(images[2]);
            	}
            	else{
            		//configura para o botão para não receber foco
            		setFocus(true);
            		//configura o icone para mostrar o icone de arquivo
            		setIcon(images[1]);
            	}
            	//altera a transparencia do botão
            	getStyle().setBgTransparency(0);
            }
            return this;
		}

		public Component getListFocusComponent(List arg0) {
            //altera o texto do button
            setText("");
            //configura o foco
            setFocus(true);
            //configura a transparencia do botão
            getStyle().setBgTransparency(255);
            //retorna o botão
            return this;
		}
    }
    
    class ImageRenderer extends Label implements ListCellRenderer {
    	//
        private Image[] images;
        /** Creates a new instance of AlternateImageRenderer */

        public ImageRenderer() {
            super("");
            images = new Image[3];
            try {
                images[0] = Image.createImage("/icons/Folder32.png");
                images[1] = Image.createImage("/icons/File32.png");
                images[2] = Image.createImage("/icons/FileShared32.png");
            } catch (IOException ex) {
                System.out.println("Não foi possível carregar os icones");
            }
        }
        
        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            setText(value.toString());
            if (Util.isPath(value.toString()) || value.toString() == UP_DIRECTORY) {
                setFocus(true);
                setIcon(images[0]);
                getStyle().setBgTransparency(0);
            } else {
            	if(parent.sharedItems.indexOf(value) == -1){
            		setFocus(false);
            		setIcon(images[2]);
            	}
            	else{
            		setFocus(false);
            		setIcon(images[1]);
            	}
            	getStyle().setBgTransparency(0);
            }
            return this;
        }

        public Component getListFocusComponent(List list) {
            //setIcon(images[1]);
            setText("");
            setFocus(true);
            getStyle().setBgTransparency(200);
            return this;
        }

    }
    
    class DirectoryList extends Container implements ListCellRenderer {

        public Label fileName = new Label("");
        public Label icon = new Label("");
        private Image[] images;
        
        private Label focus = new Label("");
        
        public DirectoryList() {
        	images = new Image[3];
            try {
                images[0] = Image.createImage("/icons/Folder32.png");
                images[1] = Image.createImage("/icons/File32.png");
                images[2] = Image.createImage("/icons/FileShared32.png");
            } catch (IOException ex) {
                System.out.println("Não foi possível carregar os icones");
            }

        	setLayout(new BorderLayout());
            addComponent(BorderLayout.WEST, icon);
            Container cnt = new Container(new BorderLayout());
            fileName.getStyle().setBgTransparency(0);
            //name.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
            cnt.addComponent(BorderLayout.CENTER ,fileName);
            addComponent(BorderLayout.CENTER, cnt);
            getStyle().setBgTransparency(100);
            getStyle().setBgSelectionColor(200, true);
            //focus.getStyle().setBgTransparency(100);
        }

        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {

            fileName.setText(value.toString());
            if (Util.isPath(value.toString()) || value.toString() == UP_DIRECTORY) {
                setFocus(true);
                icon.setIcon(images[0]);
                getStyle().setBgTransparency(0);
            } else {
            	if(parent.sharedItems.indexOf(value) == -1){
            		setFocus(false);
            		icon.setIcon(images[2]);
            	}
            	else{
            		setFocus(false);
            		icon.setIcon(images[1]);
            	}
            	getStyle().setBgTransparency(0);
            }
            return this;
        }

        public Component getListFocusComponent(List list) {
            return focus;
        }     

    }

}
