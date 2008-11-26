package mobile.profile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import mobile.lib.Constants;
import mobile.lib.DevicePoint;
import mobile.ui.BaseForm;

import com.sun.lwuit.Button;
import com.sun.lwuit.ButtonGroup;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Border;

public class FormProfile extends BaseForm implements ActionListener{

	private Vector profilesVector = new Vector();

	private Hashtable usersTable = new Hashtable();
	
	//objeto para listar os usuários encontrados
	private List list;
	
	private Form currentForm;
	

	public void setProfile(Vector devicePoints){
		Enumeration e =devicePoints.elements();
		while(e.hasMoreElements()){
			DevicePoint dp = (DevicePoint)e.nextElement();
			usersTable.put(dp.remoteName, "");
			profilesVector.addElement( dp.remoteName  );			
		}		
	}
	
	private Command cmdSearch = new Command("Pesquisar"){
		public void actionPerformed(ActionEvent evt) {
			//limpa a hashtable para receber a nova lista
			usersTable.clear();
			//limpa o vector que será passado para o list
			profilesVector.removeAllElements();
			//envia o frame requisitando os usuários disponíveis
			///getMidlet().send(Constants.APP_PROFILE, Constants.CMD_REQUESTUSERS, "Searching Users");
			
			setProfile(getMidlet().getDevices());
			
			loadList(currentForm);
			super.actionPerformed(evt);
		}
	};

	protected void execute(Form f) {
		//configura o layout do form
		f.setLayout(new BorderLayout());
		this.setProfile(this.getMidlet().getDevices());
		usersTable.put("Ivan", "Ivan");
				
		loadList(f);
		
		f.addCommand(cmdSearch);			
		
		f.show();
		
		currentForm = f;
	}

	public void loadList(Form f){
		//retira a referencia do list
		list = null;
		
		//chama o garbage colector para remover o objeto da memoria
		System.gc();
		//apaga todos os elementos do vector
		profilesVector.removeAllElements();
		
		//carrega os usuário da tabela
		Enumeration e = usersTable.keys();
		//carrega os keys no vector
		while(e.hasMoreElements()){
			//adiciona a chave encontrada ao vector
			profilesVector.addElement(e.nextElement());
		}
		
		//cria a lista com os itens do vector	
		list = new List(profilesVector);
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
        f.removeAll();
        //adiciona o list no form
        f.addComponent(BorderLayout.CENTER,list);
        
        //não sei o que esse método faz
        f.revalidate();
        
        //redesenha o lista para cobrir o lista anterior
        f.repaint();
	}


	public String getName() {
		return "Amigos";
	}
	public String getIconName() {
		return "Profile";
	}
	
	public void handleAction(byte action, Object param1, Object param2) {
	}


	public void actionPerformed(ActionEvent evt) {

		//verifica se o evento vem do comando de voltar
		if(cmdSearch == evt.getSource()){
			
			
			
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

