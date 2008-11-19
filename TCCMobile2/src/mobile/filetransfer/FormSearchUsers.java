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
	
	//objeto para listar os usuários encontrados
	private List list;
	
	//referencia para o midlet principal
	private MainMID midlet;

	//vector para armazenar os usuários encontrados
	private Vector usersVector = new Vector();
	
	//comando para retornar a tela anterior
	private Command back = new Command("Sair");
	//comando para procurar os usuários
	private Command searchUsers = new Command("Procurar Usuários");
	//comando para visualizar arquivos
	private Command getUserFiles = new Command("Vizualizar Arquivos");
	//comando para enviar arquivos para o usuário
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
		this.setTitle("Buscar Usuários");
		//configura a referencia para a tela anterior
		this.parent = parent;
		//configura o layout do form
		this.setLayout(new BorderLayout());
		
		//recebe a referencia para o midlet da aplicação
		this.midlet = mainMid;
		
		//adiciona o comando ao form 
		this.addCommand(back);
		//adiciona comando para enviar arquivos para o usuário
		this.addCommand(sendFile);
		//adiciona comando de listar usuários
		this.addCommand(getUserFiles);
		//adiciona comando para procurar usuários
		this.addCommand(searchUsers);
		
		//configura o form como listener de eventos
		this.setCommandListener(this);
		
		//carrega o list
		this.loadList();
		
		midlet.send(Constants.APP_FILETRANSFER, Constants.CMD_REQUESTUSERS, this.getUIID());
	}
	
	public void loadList(){
		//retira a referencia do list
		list = null;
		
		//chama o garbage colector para remover o objeto da memoria
		System.gc();
		//remove todos os componentes do form
		removeAll();
		
		//cria a lista com os itens do vector
		list = new List(usersVector);
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
        //redesenha o lista para cobrir o lista anterior
        this.repaint();
	}
	
	/**
	 * Tratamento dos eventos deste form
	 */
	public void actionPerformed(ActionEvent evt) {
		
		//log para mostrar que entrou no action performed do searchusers
		Util.Log("Entry actonPerfomed - Search Users");
		
		//verifica se o evento vem do comando de voltar
		if(back == evt.getSource()){
			//volta para a tela anterior
			parent.show();
		}
		
		//verifica se o evento vem do comando de procurar usuários
		if(searchUsers == evt.getSource()){
			midlet.send(Constants.APP_FILETRANSFER, Constants.CMD_REQUESTUSERS, "Searching Users");
		}
	}
	
	/**
	 * Evento disparado qdo recebe dados para o FileTransfer no bluetooth
	 */
	public void handleAction(byte action, Object param1, Object param2) {		
		//objeto para acessar as informações do parametro 1
		DevicePoint endpt = (DevicePoint) param1;
		//objeto para acessar as informações do parametro 2
		ProtoPackage pkt = (ProtoPackage) param2;
			
		//gera log para mostrar que entrou no evento de search users
		Util.Log("File Transfer - SearchUsers Received");
		//verifica se é uma resposta do frame de busca de usuários
		if(pkt.command == Constants.CMD_REQUESTUSERS){
			//verifica se o sender está procurando usuários
			if (pkt.msg.equals("Searching Users")){
				midlet.sendSingle(pkt.sender, Constants.APP_FILETRANSFER, Constants.CMD_REQUESTUSERS, "Fernando");
			}
			else {
				//verifica se este usuário já está na lista
				if((usersVector.indexOf(pkt.msg))==-1)
					//caso nao esteja, coloca ele na lista
					usersVector.addElement(pkt.msg);
				loadList();
			}
		}
		
	}

	
   class ButtonsList extends Button implements ListCellRenderer{
	   //objeto para carregar a icone do arquivo
	   private Image imageIcon;
   	
	   	public ButtonsList(){
	   		try {
	   			//carrega a imagem do resource
	   			imageIcon = Image.createImage("/icons/elvis1.png");
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
}
