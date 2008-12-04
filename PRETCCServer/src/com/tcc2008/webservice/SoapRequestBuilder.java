package com.tcc2008.webservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

public class SoapRequestBuilder {
	String WebServiceIP = "localhost";
	String Server = "";
	String WebServicePath = "";
	String SoapAction = "";
	String MethodName = "";
	String XmlNamespace = "";
	private Vector ParamNames = new Vector();
	private Vector ParamData = new Vector();

	public SoapRequestBuilder(String webServiceIP) {
		this.WebServiceIP = webServiceIP;
	}

	public void AddParameter(String Name, String Data) {
		ParamNames.addElement( (Object) Name);
		ParamData.addElement( (Object) Data);
	}

	public synchronized String sendRequest() {	
		
		String retval = "";
		Socket socket = null;
	    try {
	      socket = new Socket(Server, 80);
	    }
	    catch (Exception ex1) {
	      return ("Error: "+ex1.getMessage());
	    }
	
	    try {
	      OutputStream os = socket.getOutputStream();
	      boolean autoflush = false;
	      PrintWriter out = new PrintWriter(socket.getOutputStream(), autoflush);
	      BufferedReader in = new BufferedReader(new InputStreamReader(socket.
	          getInputStream()));
	
	      int length = 267 + (MethodName.length() * 2) + XmlNamespace.length()+ WebServiceIP.length() ;
	      for (int t = 0; t < ParamNames.size(); t++) {
	    	  String name = (String) ParamNames.elementAt(t);
	    	  String data = (String) ParamData.elementAt(t);
	    	  length += name.length() * 2;
	    	  length += data.length();
	    	  length += 7;
	      } 
	
	      // send an HTTP request to the web service
	      out.println("POST " + WebServicePath + " HTTP/1.1");
	      out.println("Host: "+ WebServiceIP +":80");
	      out.println("Content-Type: text/xml; charset=utf-8");
	      out.println("Content-Length: " + String.valueOf(length));
	      out.println("SOAPAction: \"" + SoapAction + "\"");
	      out.println("Connection: Close");
	      out.println();
	
	      out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
	      out.println("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
	      out.println("<soap:Body>");
	      out.println("<" + MethodName + " xmlns=\"" + XmlNamespace + "\">");
	      //Parameters passed to the method are added here
	      for (int t = 0; t < ParamNames.size(); t++) {
	        String name = (String) ParamNames.elementAt(t);
	        String data = (String) ParamData.elementAt(t);
	        out.println("<" + name + ">" + data + "</" + name + ">");
	      }
	      out.println("</" + MethodName + ">");
	      out.println("</soap:Body>");
	      out.println("</soap:Envelope>");
	      out.println();      
	      out.flush();
	      // Read the response from the server ... times out if the response takes
	      // more than 3 seconds
	      String inputLine;
	      StringBuffer sb = new StringBuffer(1000);
     

	      int wait_seconds = 5;
	      boolean timeout = false;
	      long m = System.currentTimeMillis();
	      while ( (inputLine = in.readLine()) != null && !timeout) {
	        sb.append(inputLine + "\n");
	        if ( (System.currentTimeMillis() - m) > (1000 * wait_seconds)) timeout = true;
	      }
	      in.close();
	
	      // The StringBuffer sb now contains the complete result from the
	      // webservice in XML format.  You can parse this XML if you want to
	      // get more complicated results than a single value.
	
	      if (!timeout) {
	        String returnparam = MethodName + "Result";
	        int start = sb.toString().indexOf("<" + returnparam + ">") +
	            returnparam.length() + 2;
	        int end = sb.toString().indexOf("</" + returnparam + ">");
	
	        //Extract a singe return parameter
	        retval = sb.toString().substring(start, end);
	      }
	      else {
	        retval="Error: response timed out.";
	      }
	
	      socket.close();
	    }
	    catch (Exception ex) {
	    	//System.out.println(ex.)
	    	ex.printStackTrace();
	      return ("Error: cannot communicate.");
	    }
	
	    return retval;
	}

	public String getWebServiceIP() {
		return WebServiceIP;
	}

	public void setWebServiceIP(String webServiceIP) {
		WebServiceIP = webServiceIP;
	}
}
