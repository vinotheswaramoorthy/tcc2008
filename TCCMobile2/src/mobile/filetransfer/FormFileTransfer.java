package mobile.filetransfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import mobile.lib.Constants;
import mobile.lib.DevicePoint;
import mobile.lib.ProtoPackage;
import mobile.lib.Util;
import mobile.ui.BaseForm;

import com.sun.lwuit.Button;
import com.sun.lwuit.Display;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.GridLayout;
import com.sun.lwuit.plaf.Style;

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

	//object para armazenar as caracter�sticas do arquivo
	FileInfo fileInfo = null;
	
	//buffer para constru��o dos frames a serem enviados
	private byte[] bufferFrames;
	//variavel para contagem dos frames transmitidos
	
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
		Util.Log(urlFile);
		//verifica se o arquivo realmente est� dispon�vel
		if(urlFile != null){
			//cria o objeto com as informa��es do arquivo
			fileInfo = mountPackages(fileName, userID);
			sendPackage();
		}		
	}
	
	public void sendPackage(){
		Util.Log("Enviando o bloco "+String.valueOf(fileInfo.lastBlockRead+1) + " de " + String.valueOf(fileInfo.numBlocks)); 
		//retirando o frame do arquivo
		String 	frame = new String(fileInfo.getBlock(fileInfo.getLastBlockRead()));
		//pega o comando
		byte 	cmd = fileInfo.getCommand(fileInfo.getLastBlockRead());

		switch(cmd) {
		case Constants.CMD_STARTSEND:
				Util.Log("Sending start command");
			break;
		case Constants.CMD_STOPSEND:
				Util.Log("Sending stop command");
			break;
		case Constants.CMD_TRANSFERING:
				Util.Log("Sending transfering command");
			break;
		}
		
		//enviando o frame para a mensagem para o usu�rio
		getMidlet().sendSingle(	fileInfo.getUserID(), 
								Constants.APP_FILETRANSFER,
								cmd, 
								frame);		
	}
	
	private FileInfo mountPackages(String fileName, String dest){
		Util.Log("MountPackages started");
		//vetor para armazenar os peda�os de 240 bytes do arquivo
		byte[][] packages = null;
		//recebe o tamanho do arquivo
		long fileSize = 0;
		//recebe o n�mero de blocos
		long numBlocks = 0;
		//para abrir o arquivo (JSR75)
		FileConnection fc = null;
		
		try {
			Util.Log(sharedTable.get(fileName).toString());
			//abre a conex�o com o arquivo utilizando o caminho armazenado no hashtable
			fc = (FileConnection)Connector.open(sharedTable.get(fileName).toString());
			Util.Log("Debug 2");
			//cria o stream para acessar o conteudo do arquivod
			InputStream is = fc.openInputStream();
			Util.Log("Debug 3");
			//pega o tamanho do arquivo
			fileSize = fc.fileSize();
			Util.Log("Debug 4");
			//verifica qtos blocos de 240 caracteres consegue montar
			numBlocks = fileSize / 240;
			Util.Log("Debug 5");
			//acrescenta no n�mero de blocos o start e o stop send
			numBlocks+=2;
			Util.Log("Debug 6");
			//verifica que h� um bloco menor que 240 caracteres
			if((fileSize % 240)>0){
				numBlocks++;
			}
			Util.Log("Debug 7");
			//cria o vetor de bytes
			packages = new byte[(int)numBlocks][];
			Util.Log("Debug 8");
			//cria o frame de inicializa��o e subtrai 2 blocos do start e do stop send	
			String startFrame = fileName + "|" + String.valueOf(fileSize) + "|" + String.valueOf(numBlocks-2);
			Util.Log("Debug 9");
			//acrescenta o frame ao vector
			packages[0] = startFrame.getBytes();
			Util.Log("Debug 10");
			//configura o input stream para passar o conteudo do arquivo de 240 em 240 caracteres
			is.mark(240);
			Util.Log("Debug 11");
			//retira os blocos de bytes e coloca no vector
			for(int index = 1;index < numBlocks-2;index++){
				//chama o garbage colector para liberar a memoria
				System.gc();
				//buffer para receber os bytes do inputstream
				byte[] bufferFrames = new byte[240];

				//carrega o conteudo no buffer
				is.read(bufferFrames);
				//coloca o conteudo do buffer no vector
				packages[index] = bufferFrames;
			}
			Util.Log("Debug 11");
			//verifica se haver� um ultimod bloco com menos de 240 caracteres
			if ((fileSize % 240)!=0){
				//pega a quantidade de bytes
				int length = (int)(fileSize % 240);
				//cria um array de bytes com a qtde exata de bytes que faltam para completar o arquivo
				bufferFrames = new byte[length];
				//carrega o conteudo no buffer
				is.read(bufferFrames);
				//coloca o conteudo do buffer no vector
				packages[(int)numBlocks-2] = bufferFrames;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Util.Log("Debug 12");
		//monta a string do ultimo frame
		String finalString = "Finish transmission";
		Util.Log("Debug step 2");
		//acrescenta a ultima string ao array de bytes
		packages[(int)numBlocks-1] = finalString.getBytes();
		Util.Log("Debug step 3");
		//cria o objeto para centralizar as informa��es do arquivo
		FileInfo fileInfo = new FileInfo(fileName,fileSize,packages, dest);
		Util.Log("Debug step 4");
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
				Util.Log("RequestFile received");
				sendFile(pkt.sender, pkt.msg);
			break;
			
			//case receba um frame de inicio de arquivo
			case Constants.CMD_STARTSEND:
				Util.Log("StartSend received");
				String[] info = Util.split(pkt.msg, "|");
				fileInfo = new FileInfo(info[0],Integer.parseInt(info[1]),Integer.parseInt(info[2]));
				getMidlet().sendSingle(pkt.sender, Constants.APP_FILETRANSFER, Constants.CMD_TRANSFERING_ACK, "0");
			break;
			//ou receba um frame de final de arquivo
			case Constants.CMD_STOPSEND:
				Util.Log("StopSend received");
				//salva o arquivo de dados
				fileInfo.saveFile();
			break;
			//ou um frame de tranferencia de arquivo
			case Constants.CMD_TRANSFERING:
				Util.Log("Transfering command received");
				byte[] 	block = pkt.msg.getBytes();
				fileInfo.putBlock(block);
				getMidlet().sendSingle(	pkt.sender, 
										Constants.APP_FILETRANSFER, 
										Constants.CMD_TRANSFERING_ACK, 
										String.valueOf(fileInfo.lastBlockRead));
			break;
			//caso receba uma confirma�ao de recebimento do frame
			case Constants.CMD_TRANSFERING_ACK:
				fileInfo.lastBlockRead++;
				sendPackage();
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
		//usu�rio que remoto da transmiss�o
		private String 		userID;
		//armazena o ultimo frame enviado
		private int			lastBlockRead = 0;
		
		//construtor
		public FileInfo(String fileName, long fileSize, byte[][] fileBlocks, String userID){
			//configura o nome do arquivo a ser carregado
			this.fileName = fileName;
			//carrega o tamanho do arquivo
			this.fileSize = fileSize;
			//pega o n�mero de blocos
			this.numBlocks = fileBlocks.length;
			//carrega os vetores do arquivo
			this.fileBlocks = fileBlocks;
			//configura o usu�rio id do usu�rio que ir� receber o arquivo
			this.userID = userID;
			//inicializa o indexador dos frames
			lastBlockRead = 0;
		}
		
		//construtor
		public FileInfo(String fileName, int fileSize, int numBlocks){
			//configura o nome do arquivo
			this.fileName = fileName;
			//configura o tamanho do arquivo
			this.fileSize = fileSize;
			//configura o n�mero de blocos que ir� receber
			this.numBlocks = numBlocks;
			//cria o vetor para receber os blocos
			this.fileBlocks = new byte[numBlocks][];
		}
		
		//retorna o identificador 
		public String getUserID(){
			return userID;
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
			return fileBlocks;
		}
		
		//insere um bloco no vetor
		public void putBlock(byte[] block){
			Util.Log("inserindo: " + new String(block));
			//insere o bloco recebido no vetor
			fileBlocks[lastBlockRead] = block;
			//incrementa o indexador dos blocos
			lastBlockRead++;
		}
		
		//retorna um elemento especifico do vetor
		public byte[] getBlock(int index){
			//atualiza o ultimo frame lido
			lastBlockRead = index;
			//retorna o frame solicitado
			return fileBlocks[index];
		}
		
		//retorna o ultimo bloco
		public int getLastBlockRead(){
			return lastBlockRead;
		}
		
		//retorna o comando dependendo do bloco
		public byte getCommand(int index){
			//variavel do comando
			byte cmd = 0;
			//verifica se � o primeiro do frame
			if(index == 0)
				//coloca o comando de inicio de comunica��o
				cmd = Constants.CMD_STARTSEND;
			//verifica se � o ultimo frame
			else if(index == numBlocks-1)
				//coloca o comando de finaliza��o de comunica��o
				cmd = Constants.CMD_STOPSEND;
			//caso seja um frame intermedi�rio
			else
				//coloca o comando de envio de frame intermedi�rio
				cmd = Constants.CMD_TRANSFERING;
			//retorna o comando 
			return cmd;
		}
		
		//salva o arquivos com os blocos recebidos
		public void saveFile(){
			try {
				//cria uma conex�o para salvar o arquivo
				FileConnection fc = (FileConnection)Connector.open("file://localhost/root1/"+fileName);
				//verifica se o arquivos j� existe
				if(!fc.exists())
					//caso n�o exista, cria o arquivo
					fc.create();
				//cria o output stream para passar o arrqy de bytes				
				OutputStream out = fc.openOutputStream();
				PrintStream print = new PrintStream(out);
				//salva os blocos no pelo output stream
				for(int index = 0; index < numBlocks; index++){
					//out.write(("Test").getBytes());//fileBlocks[index]);
					print.write(fileBlocks[index]);
				}
				//fecha o stream de dados
				out.close();
				//fecha o fileconnection
				fc.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}