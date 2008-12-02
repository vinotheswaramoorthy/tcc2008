package com.tcc2008.extend; 

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Vector;

public class Utility 
{			
	public static byte[] wrap(byte[] array, int offset, int length){
		byte[] arrayWrapped = new byte[length];
		
		for(int i=0;i<length;i++) arrayWrapped[i] = array[i+offset]; 
		
		return arrayWrapped;
	}

	public static void Log(String message) {
		System.out.println(message);
		System.out.println("==========================================================================");
	}
	
	public static byte[] hexToBytes ( String hex )
	{
	   ByteArrayOutputStream out;
	   int i, pos;
	   
	   if ( hex.length() % 2 != 0 )
	      throw new IllegalArgumentException( "hex string must be even" );
	   
	   out = new ByteArrayOutputStream( hex.length() / 2 );
	   pos = 0;
	   while ( pos < hex.length() )
	   {
	      i = Integer.parseInt( hex.substring( pos, pos+2 ), 16 );
	      out.write( i );
	      pos += 2;
	   }
	   return out.toByteArray();
	}  // hexToBytes

	public static String bytesToHex( byte [] b, int offset, int length )
	{
	   StringBuffer   sb;
	   String         result;
	   int i, top;

	   if ( b == null )
	      return "void";
	   
	   top = offset + length;
	   if ( length < 0 || top > b.length )
	      throw new IllegalArgumentException();

	   sb = new StringBuffer();
	   for ( i = offset; i < top; i++ )
	   {
	      sb.append( byteToHex( b[i] ) );
	   }
	   result = sb.toString();
	   return result;
	}

	public static String bytesToHex( byte [] b )
	{
	   if ( b == null )
	      return "void";
	   return bytesToHex( b, 0, b.length );
	}
	
	public static String byteToHex ( int v )
	{
	   String hstr;
	   hstr = Integer.toString( v & 0xff, 16 );
	   
	   return hstr.length() == 1 ? "0" + hstr : hstr;
	}
	
}
