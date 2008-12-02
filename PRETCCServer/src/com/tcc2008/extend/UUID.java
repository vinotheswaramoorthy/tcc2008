package com.tcc2008.extend;

import java.io.Serializable;



public final class UUID implements Cloneable, Comparable, Serializable
{
	private final byte []		uidValue	= new byte[ 16 ];

	/**
	 * Constructs the UUID from a 16 byte array parameter (identical).
	 *   
	 * @param uuid the 16 bytes array to use as the UUID
     * @throws IllegalArgumentException
	 */
	public UUID( byte [] uuid )
	{
		if ( uuid == null || uuid.length != uidValue.length )
			throw new IllegalArgumentException();

      System.arraycopy( uuid, 0, uidValue, 0, uidValue.length );
	}  // constructor

    /**
     * Constructs the UUID from a hexadecimal text representation of
     * a 16 byte UUID value.
     *   
     * @param ids 32 char hexadecimal text value of the UUID
     * @throws IllegalArgumentException
     * @since 0-4-0        
     */
    public UUID( String ids )
    {
       byte[] uuid;
       
       uuid = Util.hexToBytes( ids );
       if ( uuid == null || uuid.length != uidValue.length )
           throw new IllegalArgumentException( "illegal UUID string: " + ids );

       System.arraycopy( uuid, 0, uidValue, 0, uidValue.length );
    }  // constructor

	/**
	 * Compares this <code>UUID</code> object to another one and determines
    * equality of both. 
	 * 
	 * @param obj a <code>UUID</code> object to compare to
    * @return <b>true</b> if and only if all bytes of the 16 byte UUID value 
    *         are equal
	 */
	public boolean equals( Object obj )
	{
      byte[] b1, b2;

      b1 = this.uidValue;;
      b2 = ((UUID)obj).uidValue;
      return Util.equalArrays( b1, b2 );
	}

	/** A hashcode coherent with <code>equals()</code>.
	 */ 
   public int hashCode()
   {
      return Util.arrayHashcode( uidValue );
   }

   //  * @since 2-1-0
	public int compareTo ( Object o )
   {
       UUID obj;
       int i;
       
       obj = (UUID)o;
       i = 0;
       while ( i < uidValue.length && uidValue[ i ] == obj.uidValue[ i ] )
          i++;
       if ( i == uidValue.length )
          return 0;
       return uidValue[ i ] - obj.uidValue[ i ];
   }

   /**
	 * Returns a byte array containing a copy of the 16 byte value
    * of this UUID.
	 * 
	 * @return byte array (length 16)
	 */
	public byte [] getBytes()
	{
		return (byte[]) uidValue.clone();
	}

    /**
     * Returns a hexadezimal representation of the 16 byte value
     * of this UUID.
     * @return String
     */
    public String toHexString ()
    {
       return Util.bytesToHex( uidValue );
    }
    
   /**
    * Makes a deep clone of this UUID object.
    */
   public Object clone ()
   {
      try {  return super.clone();  }
      catch ( CloneNotSupportedException e )
      {
         return null;
      }
   }
   
	/**
	 * Converts this UUID into human-readable form.  The string has the format:
	 * {01234567-89ab-cdef-0123-456789abcdef}.
	 * 
	 * @return <code>String</code> representation of this <code>UUID</code>
	 */
	public String toString()
	{
		return toString( uidValue );
	}

	/**
	 * Converts a <code>uuid</code> value into human-readable form.  The resulting
    * string has the format: {01234567-89ab-cdef-0123-456789abcdef}.
	 * 
	 * @param uuid the 16 byte array to convert; must be of length 16! 
	 * @return <code>String</code> representation of the parameter <code>UUID</code>
    *         value
	 */
	public static String toString( byte[] uuid )
	{
		if ( uuid.length != 16 )
			throw new IllegalArgumentException();

		StringBuffer sb = new StringBuffer();

		sb.append( Util.bytesToHex(uuid, 0, 4) ); 
		sb.append( '-' );
		sb.append( Util.bytesToHex(uuid, 4, 2) );
		sb.append( '-' );
		sb.append( Util.bytesToHex(uuid, 6, 2) );
		sb.append( '-' );
		sb.append( Util.bytesToHex(uuid, 8, 2) );
		sb.append( '-' );
		sb.append( Util.bytesToHex(uuid, 10, 6) );
		
		return sb.toString();
	}
}
