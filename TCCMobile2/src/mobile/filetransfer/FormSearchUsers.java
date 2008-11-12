package mobile.filetransfer;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.midlet.MIDlet;

import mobile.filetransfer.FormSharedFiles.ButtonsList;
import mobile.lib.Constants;
import mobile.lib.DevicePoint;
import mobile.lib.ProtoPackage;
import mobile.lib.Util;
import mobile.midlet.MainMID;
import mobile.ui.BaseForm;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.ListCellRenderer;

public class FormSearchUsers extends Form implements ActionListener {

	//referencia ao form que fez a chamada a esta tela
	private Form parent;
	
	//objeto para listar os usu�rios encontrados
	private List list;
	
	//referencia para o midlet principal
	private MainMID midlet;

	//vector para armazenar os usu�rios encontrados
	private Vector usersVector = new Vector();
	
	//comando para retornar a tela anterior
	private Command back = new Command("Sair");
	//comando para procurar os usu�rios
	private Command searchUsers = new Command("Procurar Usu�rios");
	//comando para visualizar arquivos
	private Command getUserFiles = new Command("Vizualizar Arquivos");
	//comando para enviar arquivos para o usu�rio
	private Command sendFile = new Command("Enviar Arquivo");
	
	/**
	 * Construtor
	 * @param parent
	 * @param mainMid
	 * @param usersVector
	 */
	public FormSearchUsers(Form parent, MainMID mainMid,Vector usersVector){
		//utiliza o outro construtor
		this(parent, mainMid);
		//cria referencia para o users vector do form principal do FileTransfer
		this.usersVector = usersVector;
	}
	
	/**
	 * Construtor
	 * @param parent
	 * @param mainMid
	 */
	public FormSearchUsers(Form parent, MainMID mainMid){
		//coloca o nome do form
		this.setTitle("Buscar Usu�rios");
		//configura a referencia para a tela anterior
		this.parent = parent;
		//configura o layout do form
		this.setLayout(new BorderLayout());
		
		//recebe a referencia para o midlet da aplica��o
		this.midlet = mainMid;
		
		//adiciona o comando ao form 
		this.addCommand(back);
		//adiciona comando para enviar arquivos para o usu�rio
		this.addCommand(sendFile);
		//adiciona comando de listar usu�rios
		this.addCommand(getUserFiles);
		//adiciona comando para procurar usu�rios
		this.addCommand(searchUsers);
		
		//configura o form como listener de eventos
		this.setCommandListener(this);
		
		//carrega o list
		this.loadList();
	}
	
	public void loadList(){
		//retira a referencia do list
		list = null;
		
		//chama o garbage colector para remover o objeto da memoria
		System.gc();
		//remove todos os componentes do form
		removeAll();
		
		usersVector.addElement("Elemento 1");
		usersVector.addElement("Elemento 2");
		
		//cria a lista com os itens do vector
		list = new List(usersVector);
        //configura para n�o mostrar a borda
        list.setBorderPainted(false);
        //altera o tipo de navega��o
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
        
        //n�o sei o que esse m�todo faz
        this.revalidate();
        //redesenha o lista para cobrir o lista anterior
        this.repaint();
	}
	
	/**
	 * Tratamento dos eventos deste form
	 */
	public void actionPerformed(ActionEvent evt) {
		//verifica se o evento vem do comando de voltar
		if(back == evt.getSource()){
			//volta para a tela anterior
			parent.show();
		}
		
		//verifica se o evento vem do comando de procurar usu�rios
		if(searchUsers == evt.getSource()){
			midlet.send(Constants.APP_FILETRANSFER, Constants.CMD_REQUESTUSERS, "Searching Users");
		}
	}
	
	/**
	 * Evento disparado qdo recebe dados para o FileTransfer no bluetooth
	 */
	public void handleAction(byte action, Object param1, Object param2) {		
		DevicePoint endpt = (DevicePoint) param1;
		ProtoPackage pkt = (ProtoPackage) param2;
			
		//verifica se � uma resposta do frame de busca de usu�rios
		if(pkt.command == Constants.CMD_REQUESTUSERS){
			//verifica se o sender est� procurando usu�rios
			if (pkt.msg == "Searching Users"){
				midlet.sendSingle("", Constants.APP_FILETRANSFER, Constants.CMD_REQUESTUSERS, "Fernando");
			}
			else {
				//verifica se este usu�rio j� est� na lista
				if((usersVector.indexOf(pkt.sender))==-1)
					//caso nao esteja, coloca ele na lista
					usersVector.addElement(pkt.sender);
			}
		}
		
	}

	
   class ButtonsList extends Button implements ListCellRenderer{
	   //objeto para carregar a icone do arquivo
	   private Image imageIcon;
   	
	   	public ButtonsList(){
	   		try {
	   			//carrega a imagem do resource
	   			imageIcon = Image.createImage("/icons/Soldier32.png");
	   		} catch (IOException ex) {
	   			//exibe mensagem de erro
	   			System.out.println("N�o foi poss�vel carregar os icones");
	   		}
	   	}
	   	
		public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
			//configura o texto do bot�o
			setText(value.toString());
			//configura o alinhamento do texto e do icone para a esquerda
			setAlignment(LEFT);
			//configura para n�o desenhar a borda do bot�o
			setBorderPainted(false);
			//configura para que o bot�o para receber foco
			setFocusable(true);
			//passa o foco para o bot�o 
			setFocus(true);
			//altera a transparencia do bot�o
			getStyle().setBgTransparency(0);
			//coloca o icone carregado no bot�o
			setIcon(imageIcon);
			//retorna o bot�o montado
			return this;
		}
	
		public Component getListFocusComponent(List arg0) {
			//configura para ficar sem texto
			setText("");
			//coloca o foco no bot�o
			setFocus(true);
			//altera a transparencia do bot�o
			getStyle().setBgTransparency(255);
			//retorna o bot�o
			return this;
		}
   }   
}