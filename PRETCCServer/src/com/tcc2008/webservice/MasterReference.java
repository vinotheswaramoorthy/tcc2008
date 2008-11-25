package com.tcc2008.webservice;

import java.util.Hashtable;

import com.tcc2008.extend.Utility;


public class MasterReference {
	
	public static boolean checkUID(String idFrom){
		
		return false;
	}	
	
	public static String getServerDestination(String idFrom, String idApplication){
		
		return "";
	}
	
	public static String getUID(String user, String password){
		String response = soapRequest(	"LoginUser", 
										new String[] {"login","password"} , 
										new String[] {user, password} );
	
		// Se houver erro na qtde de parametros
		if(response.contains("[ERROR PARAM]")) return "[ERROR PARAM]";
		
		// TODO: precisa terminar
		
		return "";
	}
	
	public static boolean updateLocation(String idFrom, String idApplication, String idServer){
		String response = soapRequest(	"LoginUser", 
				new String[] {"idFrom", "idApplication", "idServer"} , 
				new String[] {idFrom, idApplication, idServer} );

		
		
		return false;
	}
	
	public static String[] verifyIsAccessible(String[] listIdTo){
		
		return new String[0];
	}
	
	private static String soapRequest(String methodName, String[] paramName, String[] paramValue ){
		if (paramName.length != paramValue.length) return "[ERROR PARAM]";
		
		SoapRequestBuilder s = new SoapRequestBuilder();
	    s.Server = "127.0.0.1";
	  	    
	    s.MethodName = methodName;
	    s.XmlNamespace = "http://tempuri.org/";
	    s.WebServicePath = "/MasterServer/Services.asmx";
	    s.SoapAction = s.XmlNamespace+s.MethodName;
	    
	    Utility.Log("************* REQUEST WEBSERVICE ****************\n"+
	    			"METHOD NAME:\t" + methodName); 
	    
	    for(int i=0;i<paramName.length;i++){
	    	Utility.Log("PARAM NAME:\t"+ paramName[i]+"\nPARAM VALUE:\t"+paramValue[i]);
	    	s.AddParameter(paramName[i], paramValue[i]);
	    }
	    String response = s.sendRequest();
	    response = response.replace("&lt;", "<").replace("&gt;", ">");	    
	    Utility.Log(" -------     RESPONSE    -------\n"+
	    		response+
	    		"\n***********************************************");

		return response;
	}
	
	
}