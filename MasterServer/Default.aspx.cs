using System;
using System.Data;
using System.Configuration;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Web.UI.HtmlControls;

public partial class _Default : System.Web.UI.Page 
{
    protected void Page_Load(object sender, EventArgs e)
    {
        Guid g = Guid.NewGuid();
        string strG = g.ToString();
        Response.Write("<br>String1: " + strG);
        Guid d = new Guid(strG);
        Response.Write("<br>String2: "+d.ToString());
    }
}
