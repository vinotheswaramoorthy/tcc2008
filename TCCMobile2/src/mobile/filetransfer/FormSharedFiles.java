package mobile.filetransfer;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.ListCellRenderer;

public class FormSharedFiles extends Form implements ActionListener {
	
	//referencia para o form que abriu este form
	private Form parent;
	//objeto para listar os arquivos e diretórios
	private List list;
	
	//vetor para carregar a lista de arquivos no list
	public Vector sharedItems;
	
	//lista com os nomes dos arquivos e seus endereços
	public Hashtable hashItems;
	
	//Form para selecionar os arquivos a disponibilizar
	private final FormSelFiles formSelFiles;
	
	//comando para sair da aplicação
	private Command exit = new Command("Sair");
	//comando para retirar o arquivo da lista de compartilhamento
	private Command unShare = new Command("Descompartilhar");
	//comando para abrir a tela de busca de arquivos
	private Command search = new Command("Procurar");
	
	/**
	 * Construtor
	 * @param parent
	 */
	public FormSharedFiles(Form parent, Hashtable sharedFiles){
		//configura o nome do form
		setTitle("Arquivos compartilhados");
		//configura o layout do form
		setLayout(new BorderLayout());
		//altera o tipo de transição
        this.setTransitionOutAnimator(CommonTransitions.createFade(400));
		
		//cria o form para selecionar os arquivos
		formSelFiles = new FormSelFiles(this);
		
		//configura a referencia para a tela anterior
		this.parent = parent;
		
		//cria o vetor para armazenar a lista de arquivos selecionados
		sharedItems = new Vector();
		//cria o hashtable para armazenar os caminhos dos arquivos
		hashItems = sharedFiles;
		
		//carrega a lista de arquivos compartilhados
		loadList();
		
		//adiciona o comando para sair da aplicação
		addCommand(exit);
		//adiciona o comando para descompartilhar arquivos
		addCommand(unShare);
		//adiciona o comando para procurar arquivos
		addCommand(search);
		
		//configura o form para receber os eventos
		setCommandListener(this);
	}
	
	/**
	 * Adiciona um item ao hash table e atualiza o vetor 
	 */
	public void put(Object key, Object value){
		//coloca a chave e o valor recebidos no hashtable
		hashItems.put(key, value);
		//carrega o enumeration com as chaves que estão no hashtable
		Enumeration e = hashItems.keys(); 
		//remove todos os elementos do vetor usado para montar a lista
		sharedItems.removeAllElements();
		//carrega o vetor com os elementos que estão no hashtable
		while(e.hasMoreElements()){
			sharedItems.addElement(e.nextElement());
		}
	}
	
	/**
	 * remove um item do hash e atualiza o vetor
	 * @param key
	 */
	public void remove(Object key){
		//remove o item indicado pela chave passada como argumento do hashtable
		hashItems.remove(key);
		//carrega a chave dos itens que ficaram no hashtable
		Enumeration e = hashItems.keys();
		//remove tdos os elementos do vetor usado para montar a lista
		sharedItems.removeAllElements();
		//carrega o vetor com os elementos que ficaram no hashtable
		while(e.hasMoreElements()){
			sharedItems.addElement(e.nextElement());
		}
	}
	
	/**
	 * retorna o vetor com os arquivos do hashtable
	 * @return
	 */
	public Vector getVectorItems(){
		return sharedItems;
	}
	
	/**
	 * recarrega a lista de arquivos e apresenta a lista atualizada na tela
	 */
	public void reshow(){
		this.show();
		loadList();
	}
	
	/**
	 * Carrega a lista de arquivos compartilhados
	 */
	public void loadList(){
		//retira a referencia do list
		list = null;
		
		//chama o garbage colector para remover o objeto da memoria
		System.gc();
		//remove todos os componentes do form
		removeAll();
		
		//cria a lista com os itens do vector
		list = new List(sharedItems);
        //configura para não mostrar a borda
        list.setBorderPainted(false);
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
        
        //remove todos os componentes da tela
        this.removeAll();
        //adiciona o list no form
        this.addComponent(BorderLayout.CENTER,list);
        
        //não sei o que esse método faz
        this.revalidate();
        this.repaint();
	}

   class ImageRenderer extends Label implements ListCellRenderer {
    	//
        private Image fileImage;
        /** Creates a new instance of AlternateImageRenderer */

        public ImageRenderer() {
            super("");
            try {
                fileImage = Image.createImage("/icons/File32.png");
            } catch (IOException ex) {
                System.out.println("Não foi possível carregar os icones");
            }
        }
        
        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            //coloca o nome do arquivo
        	setText(value.toString());
        	//carrega o icone de arquivo
            setIcon(fileImage);
            //altera a transparencia do item
            getStyle().setBgTransparency(0);
            return this;
        }

        public Component getListFocusComponent(List list) {
            //apaga o texto
            setText("");
            //configura o foco
            setFocus(true);
            //altera a transparencia do item
            getStyle().setBgTransparency(255);
            return this;
        }
    }
	
   class ButtonsList extends Button implements ListCellRenderer{
	   //objeto para carregar a icone do arquivo
	   private Image imageIcon;
   	
	   	public ButtonsList(){
	   		try {
	   			//carrega a imagem do resource
	   			imageIcon = Image.createImage("/icons/File32.png");
	   		} catch (IOException ex) {
	   			//exibe mensagem de erro
	   			System.out.println("Não foi possível carregar os icones");
	   		}
	   	}
	   	
		public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
			//configura o texto do botão
			setText(value.toString());
			//configura o alinhamento do texto e do icone para a esquerda
			setAlignment(LEFT);
			//configura para não desenhar a borda do botão
			setBorderPainted(false);
			//configura para que o botão para receber foco
			setFocusable(true);
			//passa o foco para o botão 
			setFocus(true);
			//altera a transparencia do botão
			getStyle().setBgTransparency(0);
			//coloca o icone carregado no botão
			setIcon(imageIcon);
			//retorna o botão montado
			return this;
		}
	
		public Component getListFocusComponent(List arg0) {
			//configura para ficar sem texto
			setText("");
			//coloca o foco no botão
			setFocus(true);
			//altera a transparencia do botão
			getStyle().setBgTransparency(255);
			//retorna o botão
			return this;
		}
   }   

	public void actionPerformed(ActionEvent evt) {
		if(evt.getSource() == exit){
			//volta para a tela principal do aplicativo
			parent.show();
		}
		if(evt.getSource() == unShare){
			//retira arquivo da lista de compartilhados
			remove(list.getSelectedItem());
		}
		if(evt.getSource() == search){
			//abre a tela de busca de arquivos
			formSelFiles.show();
		}
	}

}
