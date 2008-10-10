
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.obex.*;


/**
 * Exemplo de um Servidor Serial RFCOMM Bluetooth em J2SE utilizando a Pilha BlueCove e um
 * dispositivo USB TDK Styla Bluetooth que aceita conex�es de um MIDlet.
 *
 * Este exemplo de servidor somente atende um Serial Port Profile Server
 * e espera o cliente MIDlet para se conectar. Uma vez o cliente aceito, ele l�
 * uma string da conex�o stream vinda do cliente, mostra esta string na tela,
 * ent�o envia esta string de volta para o cliente informando que recebeu OK.
 * Ap�s ele ent�o espera pela pr�xima conex�o de cliente novamente.
 *
 * Como a inten��o do Servidor Serial n�o � Descobrir clientes, ele n�o implementa
 * um Discovery Agent.
 *
 * @author Maiquel Goelzer
 */

public class Servidor_Serial implements Runnable {
    // Define o Dispositivo Local e o Agente
    LocalDevice dispositivo;
    
    // Aqui � definido o Identificador Universal UUID do Servi�o Serial que ser� iniciado
    // O UUID � baseado nas normas ISO/IEC 11578
    public UUID uuid = new UUID("102030405060708090A1B1C1D1E1F111", false);
    //public UUID uuid = new UUID("11111111111111111111111111111123",false);
    
    
    // Aqui se define o tipo de Classe de Servi�o que ser� iniciada,
    // Estes tipos s�o pr�-definidos pelo sistema de classes de servi�o Bluetooth
    // mas podem ser especificado tamb�m conforme abaixo utilizamos o c�digo da
    // classe de servi�o de telefone que � padronizado pelo c�digo 0x400000.
    private int SERVICO_WEBMOBILE = 0x400000;
    
    // Controle de Saida para espera de clientes
    public boolean acabou = false;
    
    // Aqui � definida a conex�o Bluetooth para o servidor serial
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
            // GIAC - General Inquiry Access Code - Significa que o dispositivo sempre ficar� disponivel para descoberta
            // LIAC - Limited Inquiry Access Code - Significa que o dispositivo ficar� por um tempo disponivel para ser
            // descoberto, ap�s este tempo que � de aproximadamente 1 minutos ele se torna Indisponivel.
            dispositivo.setDiscoverable(DiscoveryAgent.GIAC);
            
            // Inicia uma thread para servir as conex�es
            
            Thread t = new Thread( this );
            t.start();
            // Tratamento de excess�es caso ocorra alguma falha em capturar o dispositivo local ou definir o
            // o modo de descoberta do DiscoveryAgent
        } catch ( BluetoothStateException e ) {
            e.printStackTrace();
        }
        
    }
    
    public void run() {
        // Nome do Servi�o que ser� passado na URL de conex�o do Servidor Serial
        String nomeServico = "WebMobileRFCOMM";
        
        
        // Define a conex�o stream que ser� utilizada para o dispositivo remoto.
        StreamConnection conexao = null;
        try {
            while (!acabou){
                
                String url = "btspp://localhost:" + uuid.toString() +";name="+ nomeServico;
                System.out.println("--------------------------------------------------------------------------------------------------");
                System.out.println("URL - Endere�o Servidor : " + url );
                
                // Cria um objeto para conex�o do servidor, Create a server connection object, using a
                // utilizando o Serial Port Profile, especificando a URL e o UUID e definindo
                // o nome de servi�o WebMobileRFCOMM
                conexaoServidorStream =  (StreamConnectionNotifier)Connector.open( url );
                
                // Carrega o template do Service Record que � utilizado abaixo para definir os
                // atributos do dispositivo para o servi�o que ser� criado
                ServiceRecord template = dispositivo.getRecord( conexaoServidorStream );
                
                // C�digo padr�o para definir o atributo do ServiceRecrod como ServiceAvailability (0x0008)
                // Este atributo infica que o servi�o esta disponivel para ser utilizado
                // 0xFF Indica que o servi�o esta totalmente disposinivel
                template.setAttributeValue( 0x0008, new DataElement( DataElement.U_INT_1, 0xFF ) );
                
                
                // Os servi�os em Bluetooth trabalham com 2 tipos de categorias:
                // Major e Minor - O Major define se o servi�o do dispositivo � um Dispositivo M�vel Celular,
                // Audio, GPS, Rede, etc. J� o Minor define os detalhes mais especificos da categoria Major.
                // Os conjuntos de c�digos que definem cada dispositivo s�o especificados pela implementa��o
                // da pilha protocolos Bluetooth, existem valor Hexadecimais padr�o de identifica��o e tamb�m
                // pode-se criar um servi�o em uma �rea Hexadecimal reservada para defini��o de categorias.
               // template.setDeviceServiceClasses( SERVICO_WEBMOBILE );
                
                
                
                System.out.println("Servidor --> Esperando pela Conex�o do Cliente...");
                
                // Come�a a aceitar conex�es clientes, este m�todos fica bloqueado enquanto
                // um cliente estiver conectado.
                conexao = conexaoServidorStream.acceptAndOpen();
                
                System.out.println("Servidor --> Foi aceita a conex�o de um cliente, lendo dados...");
                
                // Retorna o objeto do dispositivo remoto
                RemoteDevice dispositivoRemoto = RemoteDevice.getRemoteDevice( conexao );
                                
                // Carrega a entrada Stream do dispositivo Remoto
                DataInputStream entrada = conexao.openDataInputStream();
                
                // L� as entradas no padr�o UTF para uma String
                String dados1 = entrada.readUTF();
                String dados2 = entrada.readUTF();
                System.out.println("Recebendo --> MAC do Cliente : ( '"+dados2+"' )");
                System.out.println("Recebendo --> Dados do Cliente :  ( '"+dados1+"' )");
                
                // Carrega a saida Stream UTF para enviar de volta para o Dispositivo a confirma��o de recebido OK
                // juntamente com o dado enviado.
                DataOutputStream saida = conexao.openDataOutputStream();
                
                saida.writeUTF("Endere�o Servidor: "+dispositivo.getBluetoothAddress());
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

