using System;
using System.Web;
using System.Collections;
using System.Web.Services;
using System.Web.Services.Protocols;
using System.Xml;


/// <summary>
/// Summary description for Services
/// </summary>
[WebService(Namespace = "http://tempuri.org/")]
[WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
public class Services : System.Web.Services.WebService
{

    [WebMethod]
    public string GetServerUsers(int serverID)
    {
        XmlDocument xmlDoc = new XmlDocument();

        //== ACTIVE LIST =================
        XmlElement list = xmlDoc.CreateElement("ActiveList");

        foreach (dsGen.ActiveListRow row in new Engine().RetrieveServerList(serverID))
        {

            //== USER ITEM ===================
            XmlElement userItem = xmlDoc.CreateElement("UserItem");

            XmlElement ndeUserID = xmlDoc.CreateElement("UserID");
            XmlText ndeUserIDValue = xmlDoc.CreateTextNode(row.UserID.ToString());
            ndeUserID.AppendChild(ndeUserIDValue);

            XmlElement ndeServerID = xmlDoc.CreateElement("ServerID");
            XmlText ndeServerIDValue = xmlDoc.CreateTextNode(row.ServerID.ToString());
            ndeServerID.AppendChild(ndeServerIDValue);

            XmlElement ndeApp = xmlDoc.CreateElement("Application");
            XmlText ndeAppValue = xmlDoc.CreateTextNode(row.Application);
            ndeApp.AppendChild(ndeAppValue);

            XmlElement ndeLastLogin = xmlDoc.CreateElement("LastActivated");
            XmlText ndeLastLoginValue = xmlDoc.CreateTextNode(row.LastActivated.ToString());
            ndeLastLogin.AppendChild(ndeLastLoginValue);

            // End USERITEM ==================
            userItem.AppendChild(ndeUserID);
            userItem.AppendChild(ndeServerID);
            userItem.AppendChild(ndeApp);
            userItem.AppendChild(ndeLastLogin);

            list.AppendChild(userItem);

        }
        // End ACTIVE LIST ==================        
        xmlDoc.AppendChild(list);

        return xmlDoc.OuterXml;

    }

    [WebMethod]
    public bool IsValidUser(string uid)
    {
        return new Engine.Users().CheckUser(uid);
    }

    [WebMethod]
    public string LoginUser(string login, string password)
    {
        return new Engine.Users().LoginUser(login, password);
    }

    [WebMethod]
    public string AddUser(string userName, string loginName, string password)
    {
        return new Engine.Users().AddUser(userName, loginName, password);
    }

    [WebMethod]
    public string IncludeDevice(string uid, int serverID, string application)
    {
        return new Engine().IncludeDevice(uid, serverID, application);
    }

    [WebMethod]
    public string ExcludeDevice(string uid)
    {
        return new Engine().ExcludeDevice(uid);
    }



}

