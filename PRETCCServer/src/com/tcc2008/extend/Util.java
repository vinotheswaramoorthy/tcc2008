package com.tcc2008.extend; 



import java.io.ByteArrayOutputStream;

/**
 * Collection of useful routines for various purposes.
 * Includes a ready-to-use instance of a cryptographical 
 * random generator (<code>CrytoRandom</code>). 
 */
public class Util
{
   
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
	
	
	public static boolean equalArrays ( byte[] a, byte[] b )
	{
	   if ( a.length != b.length )
	      return false;
	   
	   for ( int i = 0; i < a.length; i++ )
	      if ( a[i] != b[i] )
	         return false;
	   return true;
	}
	
	
	public static boolean equalArrays ( byte[] a, byte[] b, int offset )
	{
	   if ( b.length < a.length + offset )
	      return false;
	   
	   for ( int i = 0; i < a.length; i++ )
	      if ( a[i] != b[i+offset] )
	         return false;
	   return true;
	}
	
	
	
	public static byte[] arraycopy ( byte[] b, int start, int length )
	{
	   byte[] copy;
	   
	   copy = new byte[ length ];
	   System.arraycopy( b, start, copy, 0, Math.min( length, b.length-start ));
	   return copy;
	}
	
	
	public static byte[] arraycopy ( byte[] b, int length )
	{
	   return arraycopy( b, 0, length );
	}
	
	public static byte[] arraycopy ( byte[] b )
	{
	   return arraycopy( b, 0, b.length );
	}
	
	public static int arrayHashcode ( byte[] b )
	{
	   long lv = 0;
	   for ( int i = 0 ; i < b.length; i++ )
	      lv += ((long)b[i] & 0xff) << (i % 32);
	   return (int) lv;
	}
}
