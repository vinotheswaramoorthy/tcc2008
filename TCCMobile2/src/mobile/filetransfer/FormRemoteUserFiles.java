package mobile.filetransfer;

import java.util.Vector;

import mobile.midlet.MainMID;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;

public class FormRemoteUserFiles extends Form implements ActionListener {

	//referencia para a tela anterior
	private Form parent;
	
	//referencia para o midlet principal
	private MainMID midlet;
	
	//comando para voltar para a tela anterior
	private Command back = new Command("Voltar",1);
	//command para baixar o arquivo selecionado
	private Command download = new Command("Baixar",2);
	
	public FormRemoteUserFiles(String title, FormSearchUsers parent) {
		//configura o titulo da tela
		super("Arquivos de "+title);
		//configura o layout do form
		setLayout(new BorderLayout());
		//altera o tipo de transição
        this.setTransitionOutAnimator(CommonTransitions.createFade(400));
	
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

}
