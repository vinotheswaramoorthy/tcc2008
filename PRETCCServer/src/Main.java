import java.util.Vector;


public class Main {

	public static void main(String[] args) 
	{
		/****************************************************************/
		
		// Fila de Pacotes recebidos, esperando para ser redirecionado
		Vector<Protocol> queueRX = new Vector<Protocol>();
		
		// Fila de Pacotes p/ envio, esperando para ser enviados
		Vector<Protocol> queueTX = new Vector<Protocol>();
		
		// Fila de Pacotes de Atualização
		Vector<Protocol> queueUpdate = new Vector<Protocol>();
		
		// Repositório de Pacotes inacessiveis
		Vector<Protocol> repository = new Vector<Protocol>();
		
		/****************************************************************/
		
		BTRFCOMMService serviceRecptor = new BTRFCOMMService();
		serviceRecptor.startServer();
		
		PackageRXService rxService = new PackageRXService(serviceRecptor.buffer , queueRX);
		RedirectService redirectService = new RedirectService(queueRX, queueTX, queueUpdate, repository);
		UpdateService updateService = new UpdateService(queueUpdate, queueTX);
		RestoreService restoreService = new RestoreService(repository);
		
		
		
		
		
		
	}

}
