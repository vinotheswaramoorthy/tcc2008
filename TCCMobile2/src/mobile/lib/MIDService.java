package mobile.lib;
import javax.bluetooth.UUID;


public class MIDService {

	  public static final UUID BEEHIVE_UUID = 
	      new UUID("F0E0D0C0B0A000908070605040302010", false);
	  
	  private String mName;
	  private String mAddress;
	  private String mURL;
	  
	  public MIDService(String name, String address,
	      String url) {
	    mName 	 = name;
	    mAddress = address;
	    mURL 	 = url;
	  }
	  
	  public String getName() { return mName; }
	  public String getAddress() { return mAddress; }
	  public String getURL() { return mURL; }
	  
	  public boolean equals(Object o) {
	    if (o == this) return true;
	    if (o instanceof MIDService == false) return false;
	    
	    MIDService bcs = (MIDService)o;
	    //Device Name
	    if (bcs.getName().equals(mName) == false) 		return false;
	    //Address
	    if (bcs.getAddress().equals(mAddress) == false) return false;
	    //URL
	    if (bcs.getURL().equals(mURL) == false) 		return false;

	    return true;
	  }    
	  
	  public String toString() {
	    return "[BeeHiveService:" + mName + ":" + mAddress
	        + ":" + mURL + "]";
	  }
	
}
