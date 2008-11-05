package mobile.lib;

public interface BTListener
{


  /**
   * network activity action handler.
   * @param action must be one of GeneralServer.ACT_XXX field
   * @param param1 usually the EntPoint object that correspond to the action
   * @param param2 varies by action value
   */
  public void handleAction( byte action, Object param1, Object param2 );
}