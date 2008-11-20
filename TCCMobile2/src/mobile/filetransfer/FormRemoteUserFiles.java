package mobile.filetransfer;

import java.io.IOException;
import java.util.Vector;

import mobile.filetransfer.FormSearchUsers.ButtonsList;
import mobile.midlet.MainMID;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.List;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.ListCellRenderer;

public class FormRemoteUserFiles extends Form implements ActionListener {

	//referencia para a tela anterior
	private Form parent;
	
	//objeto para listar os arquivos disponibilizado pelo usuário remoto
	private List list;
	
	//referencia para o midlet principal
	private MainMID midlet;
	
	//comando para voltar para a tela anterior
	private Command back = new Command("Voltar",1);
	//command para baixar o arquivo selecionado
	private Command download = new Command("Baixar",2);
	
	public FormRemoteUserFiles(String title, FormSearchUsers parent, String[] files) {
		//configura o titulo da tela
		super("Arquivos de "+title);
		//configura o layout do form
		setLayout(new BorderLayout());
		//altera o tipo de transição
        this.setTransitionOutAnimator(CommonTransitions.createFade(400));
        
        //cria a lista com os nomes dos arquivos recebidos
        createListFiles(files);
        
        //passa a referencia da tela anterior
        this.parent = parent;
        //passa a referencia para o midlet da aplicação
        this.midlet = parent.getMainMid();
        
        //coloca o comando de voltar no form
        this.addCommand(back);
        //adiciona o comando para baixar o arquivo selecionado
        this.addCommand(download);
        
        //configura o form como listener de eventos
        this.setCommandListener(this);
	}
	
	public void createListFiles(String[] files){
		//destroi o objeto com a lista anterior
		list = null;
		
		//chama o garbage coletor para liberar a memoria
		System.gc();
		
		//cria a lista com os itens do vector
		list = new List(files);
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

	public void actionPerformed(ActionEvent evt) {
		//verifica se o evento recebido é o de retornar para a tela anterior
		if(back == evt.getSource()){
			//volta para a tela anterior
			parent.show();
		}
		//verifica se o evento recebido é o de baixar o arquivo selecionado
		if(download == evt.getSource()){
			
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

	
}
