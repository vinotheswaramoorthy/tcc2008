package mobile.filetransfer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.midlet.MIDlet;

import mobile.lib.Constants;
import mobile.lib.DevicePoint;
import mobile.lib.ProtoPackage;
import mobile.lib.Sender;
import mobile.lib.Util;
import mobile.midlet.MainMID;
import mobile.ui.BaseForm;

import com.sun.lwuit.Button;
import com.sun.lwuit.Component;
import com.sun.lwuit.Display;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.animations.Transition3D;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.layouts.GridLayout;
import com.sun.lwuit.plaf.Style;
import com.sun.lwuit.util.Resources;

public class FormFileTransfer extends BaseForm{
	
	//form para gerenciamento dos arquivos disponibilizados
	private FormSharedFiles formSharedFiles;
	//form para busca de usu�rios com o mesmo aplicativo ligado
	private FormSearchUsers formSearchUsers;
	
	//referencia para o form onde ser� apresentado
	private Form formRef;
	
	//bot�es para acessar os funcionalidades do aplicativo
	private Button btnUsers, btnShare;
	//objetos para carregar os icones
	private Image share, users;
	//objeto para manipular o style dos componentes
	private Style style;
	//objeto para receber os eventos dos botoes
	private ActionListener actionListener;
	
	//tabela com os arquivos compartilhados e os respectivos endere�os
	public Hashtable sharedTable = new Hashtable();

	//lista com os usu�rios encontrados
	public Hashtable usersTable = new Hashtable();
	
	//buffer para constru��o dos frames a serem enviados
	private byte[] bufferFrames;
	
	//n�o fa�o ideia do que esse m�todo faz
	public void cleanup() {
		
	}
	
	//configura o nome do form
	public String getName() {
		return "Compartilhar Arquivos";
	}
	
	//retorna o nome do icone que ser� colocado no bot�o do midlet
	public String getIconName() {
		return "FileTransfer";
	}

	//configura o texto do help do aplicativo
	protected String getHelp() {
		return  "Aplicativo de compartilhamento de arquivos."+
				"\nEste aplicativo permite disponibilizar arquivos e vizualizar arquivos de outros usu�rios";
	}

	protected void execute(Form f) {
		//pega a referencia para o form do midlet
		this.formRef = f;
		
		//bloco try prevendo problema na abertura dos arquivos
		try {
			//carrega o icone de compatilhamento de arquivo
			share = Image.createImage("/icons/share.png");
			//carrega o icone de busca de usu�rios
			users = Image.createImage("/icons/users.png");
		} catch (IOException e) {
			//imprime mensagem de erro caso nao consiga carregar os erros
			System.err.println("Erro ao carregar as imagens");
		}

		//verifica se o formSharedFiles j� foi instanciado
		if(formSharedFiles == null)
			//form para sele��o de arquivos a serem compartilhados
			formSharedFiles = new FormSharedFiles(f, sharedTable);
		//verifica se o formSearchUsers j� foi instanciado
		if(formSearchUsers == null)
			//form para busca de usu�rios com arquivos compartilhados
			formSearchUsers = new FormSearchUsers(this, getMidlet());
		
		//listener para receber os eventos do dos bot�es
		actionListener = new ActionListener(){
			//implementa��o dos tratamento dos eventos
			public void actionPerformed(ActionEvent evt) {
				//verifica se o evento recebido foi do bot�o de compartilhamento
				if(evt.getSource() == btnShare){
					formSharedFiles.show();
				}
				//verifica se o evento recebido foi do bot�o de busca de arquivos
				if(evt.getSource() == btnUsers){
					formSearchUsers.show();
				}
			}
		};
		
		//inicializa��o do bot�o para compartilhar arquivos
		btnShare = createButton("Compartilhar Arquivos",share);
		
		//inicializa��o do bot�o para procurar outro usu�rios
		btnUsers = createButton("Procurar Usu�rios",users);
		
		//altera o tipo de transi��o do form
        f.setTransitionOutAnimator(CommonTransitions.createFade(400));
        //retira o scroll do form
        f.setSmoothScrolling(false);


        //pega o largura da tela
        int width = Display.getInstance().getDisplayWidth();
        //pega a largura dos bot�es
        int elementWidth = Math.max(btnShare.getPreferredW(), 2);
        //calcula o n�mero de colunas de bot�es que cabem na tela
        int cols = width / elementWidth;
        //calcula o n�mero de linhas de bot�es que cabem na tela
        int rows = 2 / cols;
        
        //configura o layout do form
        f.setLayout(new GridLayout(rows, cols));        
        
		//adiciona o bot�o ao form
		f.addComponent(btnShare);
		//adiciona o bot�o ao form
		f.addComponent(btnUsers);
	}
	
	private Button createButton(String buttonName, Image buttonImage){
		Button button = new Button(buttonName,buttonImage){
			//configura como a imagem ficar� qdo o bot�o for acionado 
			public Image getPressedIcon(){
				//pega a imagem do bot�o
				Image i = getIcon();
				//retorna a imagem com 80% do seu tamanho original
				return i.scaled((int)(i.getWidth() * 0.8), (int) (i.getHeight() * 0.8));
			}
		};
		
		//recebe o controle de stylo do bot�o
		style = button.getStyle();
		//altera o tipo de borda do bot�o
		style.setBorder(null);
		//altera a transparencia do bot�o
		style.setBgTransparency(0);
		//altera a cor de fundo qdo o bot�o � selecionado
		style.setBgSelectionColor(0xffffff);
		//configura o alinhamento do bot�o;
		button.setAlignment(Label.CENTER);
		//configura o alinhamento do texto do bot�o para o centro
		button.setTextPosition(Label.BOTTOM);
		//configura o listener dos eventos deste bot�o
		button.addActionListener(actionListener);
		
		return button;
	}
	
	private Label createFont(Font f, String label) {
		//cria o label com o texto passado como parametro
		Label fontLabel = new Label(label);
		//configura a fonte de acordo com o passado como parametro
		fontLabel.getStyle().setFont(f);
		//habilita a possibilidade de receber foco
		fontLabel.setFocusable(true);
		//fontLabel.setFocusPainted(false);
		fontLabel.getStyle().setBgTransparency(0);
		//retorna o label montado
		return fontLabel;
   }

	public void sendFile(String userID, String fileName){
		//carrega o caminho do arquivo para verificar se ele existe
		String urlFile = sharedTable.get(fileName).toString();
		
		//verifica se o arquivo realmente est� dispon�vel
		if(urlFile != null){
			//cria o objeto com as informa��es do arquivo
			FileInfo fileInfo = mountPackages(urlFile);
		}
		
		
	}
	
	private FileInfo mountPackages(String fileName){
		//vetor para armazenar os peda�os de 240 bytes do arquivo
		Vector packages = new Vector();
		//recebe o tamanho do arquivo
		long fileSize = 0;
		//recebe o n�mero de blocos
		long numBlocks = 0;
		//para abrir o arquivo (JSR75)
		FileConnection fc = null;

		Util.Log(sharedTable.get(fileName).toString());
		
		try {
			//abre a conex�o com o arquivo utilizando o caminho armazenado no hashtable
			fc = (FileConnection)Connector.open(sharedTable.get(fileName).toString());
			//cria o stream para acessar o conteudo do arquivod
			InputStream is = fc.openInputStream();
			//pega o tamanho do arquivo
			fileSize = fc.fileSize();
			//verifica qtos blocos de 240 caracteres consegue montar
			numBlocks = fileSize / 240;
			
			//configura o input stream para passar o conteudo do arquivo de 240 em 240 caracteres
			is.mark(240);
			//retira os blocos de bytes e coloca no vector
			for(int index = 0;index < numBlocks;index++){
				System.gc();
				//buffer para receber os bytes do inputstream
				byte[] bufferFrames = new byte[240];

				//carrega o conteudo no buffer
				is.read(bufferFrames);
				//coloca o conteudo do buffer no vector
				packages.addElement(bufferFrames);

				Util.Log("Block: " + index + "NumBytes: " + ((byte[])packages.elementAt(index)).length +
										"\n" + new String((byte[])packages.elementAt(index)));
			}
			
			//verifica se haver� um ultimod bloco com menos de 240 caracteres
			if ((fileSize % 240)!=0){
				//cria um array de bytes com a qtde exata de bytes que faltam para completar o arquivo
				bufferFrames = new byte[(int)fileSize % 240];
				//carrega o conteudo no buffer
				is.read(bufferFrames);
				//coloca o conteudo do buffer no vector
				packages.addElement(bufferFrames);

				Util.Log("Block: final NumBytes: " + ((byte[])packages.elementAt(packages.size()-1)).length +
										"\n" + new String((byte[])packages.elementAt(packages.size()-1)));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//cria o objeto para centralizar as informa��es do arquivo
		FileInfo fileInfo = new FileInfo(fileName,fileSize,packages);
		//retorna o vetor com o arquivo dividido blocos de 240 bytes
		return fileInfo;
	}
	
	/**
	 * Evento disparado qdo recebe dados para o FileTransfer no bluetooth
	 */
	public void handleAction(byte action, Object param1, Object param2) {		
		//objeto para maniputar o end point recebido
		DevicePoint endpt = (DevicePoint) param1;
		//objeto para manipular o protopackage recebido
		ProtoPackage pkt = (ProtoPackage) param2;
		
		Util.Log("invoke FileTransfer handleAction. action=" + pkt.command);
		Util.Log("Remote user: "+pkt.sender);
		Util.Log("Message Received: " + pkt.msg);		
		
		//verifica qual foi o comando recebido
		switch(pkt.command){
			//caso seja uma requisi��o de usu�rios
			case Constants.CMD_REQUESTUSERS:
			//comando de envio de usu�rio
			case Constants.CMD_RETURNUSER:
			//ou caso seja um requisi��o de arquivos diponibilizados
			case Constants.CMD_REQUESTLIST:
			//retorno dos arquivos requisitados
			case Constants.CMD_RETURNLIST:
				//passa a requisi��o para o formSearchUsers onde ser� tratado
				formSearchUsers.handleAction(action, param1, param2);
			break;
			//caso seja uma requisi��o de um arquivo especifico
			case Constants.CMD_REQUESTFILE:
				
			break;
		}
		
	}

	
	/**
	 * chama o form, j� que esta classe nao estende diretamento o form
	 */
	public void show(){
		//metodo show() do form principal
		formRef.show();
	}
	
	//classe para centralizar as informa��es do arquivo
	class FileInfo{
		//armazena o nome do arquivo
		private String 		fileName;
		//armazena o tamanho do arquivo em bytes
		private long  		fileSize;
		//armazena o n�mero de blocos de 240 bytes
		private int 		numBlocks;

		//armazena os blocos de 240 bytes 
		private byte[][]  	fileBlocks;
		
		//construtor
		public FileInfo(String fileName, long fileSize, Vector fileBlocks){
			this.fileName = fileName;
			this.fileSize = fileSize;
			this.numBlocks = fileBlocks.size();
			fileBlocks.copyInto(this.fileBlocks);
		}
		
		//construtor
		public FileInfo(String fileName, int numBlocks){
			//configura o nome do arquivo
			this.fileName = fileName;
			//cria o array onde ser�o armazenados os bytes
			this.fileBlocks = new byte[numBlocks][240];
		}
		
		//insere o block na posi��o do array passado como parametro
		public void putBlock(int numBlock, byte[] block){
			this.fileBlocks[numBlock] = block;
		}
		
		//retorna o nome do arquivo
		public String getFileName(){
			return this.fileName;
		}
		
		//retorna o tamanho do arquivo
		public long getFileSize(){
			return this.fileSize;
		}
		
		//retorna o numero de blocos do arquivo
		public int getNumBlocks(){
			return this.numBlocks;
		}
		
		//retorna o array com os blocos do arquivo
		public byte[][] getFileBlocks(){
			return this.fileBlocks;
		}
	}
}