package com.tcc2008.extend; 

import java.util.Vector;



public class Utility 
{			
	/**
	 * Retorna parte de um array de bytes
	 * 
	 * @param array array de onde sera extraida a parte 
	 * @param offset indice do array onde comecara a parte  
	 * @param length tamanho da parte que sera extraida 
	 * @return a parte extraida em bytes
	 */
	public static byte[] wrap(byte[] array, int offset, int length){
		byte[] arrayWrapped = new byte[length];

		for(int i=0;i<length;i++) arrayWrapped[i] = array[i+offset]; 

		return arrayWrapped;
	}

	/**
	 * Log do sistema
	 * 
	 * @param message mensagem do Log
	 */
	public static void Log(String message) {
		System.out.println(message);
		System.out.println("==========================================================================");
	}

	/** 
	 * Gera um pacote de envio simples para o servidor, 
	 * onde o uid do destinatario ja eh conhecido
	 * 
	 * @param idFrom uid de origem
	 * @param idTo uid de destino
	 * @param idApplication id da Aplicacao(max 16 char)
	 * @param isPersisted se o pacote pode ser armazenado caso nao encontre o destino
	 * @param data dados da aplicacao
	 * @return pacote em bytes para ser enviado para o servidor de redirecionamento
	 */
	public static byte[] genPackageToSend(String idFrom, String idTo, String idApplication, boolean isPersisted, byte[] data){

		Protocol proto = new Protocol();
		proto.setCommand(Dictionary.CMD_SEND);
		proto.setIDFrom(new UUID(idFrom));
		proto.setIDTo(new UUID(idTo));
		proto.setIDApp(new UUID(idApplication));
		proto.setPersisted(isPersisted);
		proto.setData(data);

		return genPackage(proto);
	}
	
	/** 
	 * Gera um pacote de envio simples para o servidor, 
	 * como nao ha destinatario, o pacote será entregue a 
	 * todos que estao com a aplicaçao ativada.	  
	 * 
	 * @param idFrom uid de origem
	 * @param idApplication id da Aplicacao(max 16 char)
	 * @param isPersisted se o pacote pode ser armazenado caso nao encontre o destino
	 * @param data dados da aplicacao
	 * @return pacote em bytes para ser enviado para o servidor de redirecionamento
	 */
	public static byte[] genPackageToSend(String idFrom, String idApplication, boolean isPersisted, byte[] data){

		Protocol proto = new Protocol();
		proto.setCommand(Dictionary.CMD_SEND);
		proto.setIDFrom(new UUID(idFrom));
		proto.setIDApp(new UUID(idApplication));
		proto.setPersisted(isPersisted);
		proto.setData(data);

		return genPackage(proto);
	}

	/**
	 * Gera pacote de solicitacao de uid a partir de usuario e senha cadastrado no servidor central
	 * 
	 * @param user usuario cadastrado no servidor central
	 * @param password senha cadastrada no servidor central
	 * @return pacote em bytes para ser enviado para o servidor de redirecionamento
	 */
	public static byte[] genPackageGetUID(String user, String password){
		byte[] data = (user+";"+password).getBytes();
		Protocol proto = new Protocol();
		proto.setCommand(Dictionary.CMD_GETUUID);
		proto.setData(data);
		return genPackage(proto);
	}

	/**
	 * Gera pacote de atualizacao da localizacao da origem
	 * 
	 * @param idFrom uid da origem
	 * @param idApplication id da Aplicacao
	 * @return pacote em bytes para ser enviado para o servidor de redirecionamento
	 */
	public static byte[] genPackageUpdateLocation(String idFrom, String idApplication){

		Protocol proto = new Protocol();
		proto.setCommand(Dictionary.CMD_UPDATELOCAL);
		proto.setIDFrom(new UUID(idFrom));
		proto.setIDApp(new UUID(idApplication));

		return genPackage(proto);
	}

	/**
	 * Gera pacote em bytes a partir de um objeto Protocol
	 * 
	 * @param proto objeto Protocol representando o pacote
	 * @return pacote em bytes
	 */
	public static  byte[] genPackage(Protocol proto){

		// 2 soh/stx + 48 ids + 1 pers+ 1 cmd + 2nchar + N data+ 1 Etx + 1 bcc + 1 eot
		int pkgSize = 57 + proto.getData().length; 

		byte[] pkg = new byte[pkgSize];

		pkg[0] = (byte) Dictionary.SOH;
		pkg[1] = (byte) Dictionary.STX;

		byte[] idFrom = proto.getIDFrom().getBytes();			
		for(int i=0;i<16;i++)
		{
			pkg[i+2] = idFrom[i];
		}

		byte[] idTo = proto.getIDTo().getBytes();
		for(int i=0;i<16;i++)
		{
			pkg[i+18] = idTo[i];
		}

		byte[] idApp = proto.getIDApp().getBytes();
		for(int i=0;i<16;i++)
		{
			pkg[i+34] = idApp[i];
		}

		int ndata = proto.getData().length;

		pkg[50] = (byte)(proto.isPersisted()?1:0);
		pkg[51] = (byte) proto.getCommand();
		pkg[52] = (byte) (ndata/256);
		pkg[53] = (byte) (ndata%256);

		for(int i=0;i<ndata;i++)
		{
			pkg[i+54] = proto.getData()[i];
		}

		pkg[ndata+54] = 0x03; 	//ETX
		pkg[ndata+55] = 0;		//BCC
		pkg[ndata+56] = 0x04; 	//EOT

		return pkg;
	}

	/**
	 * Analizador sequencia de bytes que gera pacotes em objetos Protocol. 
	 * Utilizado para analizar os bytes recebidos e tranforma-los em Protocol`s
	 * 
	 * @param packges sequencia de bytes
	 * @return array de objetos Protocol
	 */
	public static Protocol[] genProtocols(byte[] packges){
		Vector<Protocol> queue = new Vector<Protocol>();

		int index = 0;
		while(packges.length > index)
		{				
			if(packges[index] == Dictionary.SOH && packges.length > (index+1) && packges[index+1] == Dictionary.STX)
			{ 
				index+=2;
				//Encontrar o tamanho do DATA
				// 16 IDOrigem/IDDestino/IDAplicacao = 48  + 1 PER + 1 CMD 
				int nChar = ((int) packges[index+50]) + packges[index+51];

				// Avançar para encontrar o EOT
				// 2 NCHAR  1 ETX  1 BCC  
				if(packges[index+nChar+54] != Dictionary.EOT) continue;

				//Falta testar o BCC 
				/* TESTE DO BCC AQUI */


				UUID idFrom 	= new UUID(Utility.wrap(packges, index, 16));
				UUID idTo 		= new UUID(Utility.wrap(packges, index+=16, 16));
				UUID idApp 		= new UUID(Utility.wrap(packges, index+=16, 16));
				boolean isPersisted = ((int) packges[index+=16]) != 0 ;
				byte cmd		= packges[++index];

				//coloca na posição do data
				index+=3;

				Protocol proto = new Protocol();
				proto.setIDFrom(idFrom);
				proto.setIDTo(idTo);
				proto.setIDApp(idApp);
				proto.setPersisted(isPersisted);
				proto.setCommand(cmd);
				proto.setData(Utility.wrap(packges, index, nChar));


				// 1 ETX  1 BCC  1 EOT
				index += nChar + 3;
				if (packges[index-1] == Dictionary.EOT) 
				{
					queue.add(proto);
				}
			}
			else index++;
		}

		return (Protocol[]) queue.toArray();
	}
}
