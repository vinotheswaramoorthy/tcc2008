package com.tcc2008.extend;

import java.io.Serializable;
import java.util.UUID;



public class Protocol implements Serializable {
	private UUID IDFrom;
	private UUID IDTo;
	private String IDApp;
	private int command;
	private boolean isPersisted;
	private byte[] data;
	private String address;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public UUID getIDFrom() {
		return IDFrom;
	}
	public void setIDFrom(UUID from) {
		IDFrom = from;
	}
	public UUID getIDTo() {
		return IDTo;
	}
	public void setIDTo(UUID to) {
		IDTo = to;
	}
	public int getCommand() {
		return command;
	}
	public void setCommand(int command) {
		this.command = command;
	}
	public String getIDApp(){
		return IDApp;
	}
	public void setIDApp(String app) {
		IDApp = app;
	}
	public boolean isPersisted() {
		return isPersisted;
	}
	public void setPersisted(boolean isPersisted) {
		this.isPersisted = isPersisted;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public String toString(){
		String toView = "\n------------------------PACKAGE------------------------------"+
						"\nFROM:\t"+IDFrom+
						"\nTO:\t"+IDTo+
						"\nAPP:\t"+IDApp+
						"\nPERS:\t"+isPersisted+
						"\nCMD:\t"+command+
						"\nNDATA:\t"+data.length+
						"\nDATA:\t"+new String(data)+
						"\n-------------------------------------------------------------";
		return toView;
	}
	

}
