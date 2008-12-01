package com.tcc2008.extend;

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
	}
	
}
