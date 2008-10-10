
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.obex.*;


/**
 * Exemplo de um Servidor Serial RFCOMM Bluetooth em J2SE utilizando a Pilha BlueCove e um
 * dispositivo USB TDK Styla Bluetooth que aceita conexões de um MIDlet.
 *
 * Este exemplo de servidor somente atende um Serial Port Profile Server
 * e espera o cliente MIDlet para se conectar. Uma vez o cliente aceito, ele lê
 * uma string da conexão stream vinda do cliente, mostra esta string na tela,
 * então envia esta string de volta para o cliente informando que recebeu OK.
 * Após ele então espera pela próxima conexão de cliente novamente.
 *
 * Como a intenção do Servidor Serial não é Descobrir clientes, ele não implementa
 * um Discovery Agent.
 *
 * @author Maiquel Goelzer
 */

public class Servidor_Serial implements Runnable {
    // Define o Dispositivo Local e o Agente
    LocalDevice dispositivo;
    
    // Aqui é definido o Identificador Universal UUID do Serviço Serial que será iniciado
    // O UUID é baseado nas normas ISO/IEC 11578
    public UUID uuid = new UUID("102030405060708090A1B1C1D1E1F111", false);
    //public UUID uuid = new UUID("11111111111111111111111111111123",false);
    
    
    // Aqui se define o tipo de Classe de Serviço que será iniciada,
    // Estes tipos são pré-definidos pelo sistema de classes de serviço Bluetooth
    // mas podem ser especificado também conforme abaixo utilizamos o código da
    // classe de serviço de telefone que é padronizado pelo código 0x400000.
    private int SERVICO_WEBMOBILE = 0x400000;
    
    // Controle de Saida para espera de clientes
    public boolean acabou = false;
    
    // Aqui é definida a conexão Bluetooth para o servidor serial
    public StreamConnectionNotifier conexaoServidorStream;
    
    
    public static void main(String[] args ) {
        
        new Servidor_Serial().inicia_servidor();
        
    }
    
    public void inicia_servidor() {
        try {
            // Inicializa a Pilha de Protocolos Java Bluetooh
            // e captura o objeto dispositivo local Bluetooth
            dispositivo = LocalDevice.getLocalDevice();
            // Existem 2 formas de definir o tempo de descoberta de um dispositivo
            // GIAC - General Inquiry Access Code - Significa que o dispositivo sempre ficará disponivel para descoberta
            // LIAC - Limited Inquiry Access Code - Significa que o dispositivo ficará por um tempo disponivel para ser
            // descoberto, após este tempo que é de aproximadamente 1 minutos ele se torna Indisponivel.
            dispositivo.setDiscoverable(DiscoveryAgent.GIAC);
            
            // Inicia uma thread para servir as conexões
            
            Thread t = new Thread( this );
            t.start();
            // Tratamento de excessões caso ocorra alguma falha em capturar o dispositivo local ou definir o
            // o modo de descoberta do DiscoveryAgent
        } catch ( BluetoothStateException e ) {
            e.printStackTrace();
        }
        
    }
    
    public void run() {
        // Nome do Serviço que será passado na URL de conexão do Servidor Serial
        String nomeServico = "WebMobileRFCOMM";
        
        
        // Define a conexão stream que será utilizada para o dispositivo remoto.
        StreamConnection conexao = null;
        try {
            while (!acabou){
                
                String url = "btspp://localhost:" + uuid.toString() +";name="+ nomeServico;
                System.out.println("--------------------------------------------------------------------------------------------------");
                System.out.println("URL - Endereço Servidor : " + url );
                
                // Cria um objeto para conexão do servidor, Create a server connection object, using a
                // utilizando o Serial Port Profile, especificando a URL e o UUID e definindo
                // o nome de serviço WebMobileRFCOMM
                conexaoServidorStream =  (StreamConnectionNotifier)Connector.open( url );
                
                // Carrega o template do Service Record que é utilizado abaixo para definir os
                // atributos do dispositivo para o serviço que será criado
                ServiceRecord template = dispositivo.getRecord( conexaoServidorStream );
                
                // Código padrão para definir o atributo do ServiceRecrod como ServiceAvailability (0x0008)
                // Este atributo infica que o serviço esta disponivel para ser utilizado
                // 0xFF Indica que o serviço esta totalmente disposinivel
                template.setAttributeValue( 0x0008, new DataElement( DataElement.U_INT_1, 0xFF ) );
                
                
                // Os serviços em Bluetooth trabalham com 2 tipos de categorias:
                // Major e Minor - O Major define se o serviço do dispositivo é um Dispositivo Móvel Celular,
                // Audio, GPS, Rede, etc. Já o Minor define os detalhes mais especificos da categoria Major.
                // Os conjuntos de códigos que definem cada dispositivo são especificados pela implementação
                // da pilha protocolos Bluetooth, existem valor Hexadecimais padrão de identificação e também
                // pode-se criar um serviço em uma área Hexadecimal reservada para definição de categorias.
               // template.setDeviceServiceClasses( SERVICO_WEBMOBILE );
                
                
                
                System.out.println("Servidor --> Esperando pela Conexão do Cliente...");
                
                // Começa a aceitar conexões clientes, este métodos fica bloqueado enquanto
                // um cliente estiver conectado.
                conexao = conexaoServidorStream.acceptAndOpen();
                
                System.out.println("Servidor --> Foi aceita a conexão de um cliente, lendo dados...");
                
                // Retorna o objeto do dispositivo remoto
                RemoteDevice dispositivoRemoto = RemoteDevice.getRemoteDevice( conexao );
                                
                // Carrega a entrada Stream do dispositivo Remoto
                DataInputStream entrada = conexao.openDataInputStream();
                
                // Lê as entradas no padrão UTF para uma String
                String dados1 = entrada.readUTF();
                String dados2 = entrada.readUTF();
                System.out.println("Recebendo --> MAC do Cliente : ( '"+dados2+"' )");
                System.out.println("Recebendo --> Dados do Cliente :  ( '"+dados1+"' )");
                
                // Carrega a saida Stream UTF para enviar de volta para o Dispositivo a confirmação de recebido OK
                // juntamente com o dado enviado.
                DataOutputStream saida = conexao.openDataOutputStream();
                
                saida.writeUTF("Endereço Servidor: "+dispositivo.getBluetoothAddress());
                saida.writeUTF("Dados: "+ dados1 + " ACK (Recebido OK pelo Servidor) ");
                saida.flush();
                
                System.out.println("Dados ( '"+dados1+"' ) foi retornado ( ACK ) + MAC Servidor");
                
                
                // Fecha o stream e a conexao do servidor e a partir dai espera por um novo cliente Bluetooth
                saida.close();
                conexaoServidorStream.close();
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getClass().getName()+" "+e.getMessage());
        }
        
        
        
    }
}

