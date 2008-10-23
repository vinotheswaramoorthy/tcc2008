using System;
using System.Data;
using System.Configuration;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Web.UI.HtmlControls;

/// <summary>
/// Summary description for Engine
/// </summary>
public class Engine
{    
    public dsGen.ActiveListDataTable RetrieveActiveList()
    {
        dsGen.ActiveListDataTable activeList = new dsGenTableAdapters.ActiveListTableAdapter().GetData();
        return activeList;
    }

    public dsGen.ActiveListDataTable RetrieveServerList(int serverID)
    {
        dsGen.ActiveListDataTable activeList = new dsGenTableAdapters.ActiveListTableAdapter().GetDataByServerID(serverID);
        return activeList;
    }

    /// <summary>
    /// 
    /// </summary>
    /// <param name="uid"></param>
    /// <param name="serverID"></param>
    /// <param name="application"></param>
    /// <returns></returns>
    public string IncludeDevice(string uid, int serverID, string application)
    {
        ///////////////////////////////////////////////////////////////////
        /// Encontra o usu�rio atrav�s do Unique ID
        dsGen.UsersRow user;
        try
        {
            user = new Users().FindUser(uid);
        }
        catch
        {
            return "InvalidUID";
        }
        ///////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////
        /// Remove poss�veis ativa��es do usu�rio atual na tabela
        dsGenTableAdapters.ActiveListTableAdapter taActiveList = new dsGenTableAdapters.ActiveListTableAdapter();
        try
        {
            taActiveList.DeleteByUserID(user.UserID);
        }
        catch
        {
            return "FailedToRemoveList";
        }

        ///////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////
        /// Insere o usu�rio como um novo usu�rio ativo
        try
        {
            new dsGenTableAdapters.ActiveListTableAdapter().Insert(user.UserID, serverID, application, DateTime.Now);
            return "Success";
        }
        catch(Exception ex)
        {
            return ex.Message;
        }
        //////////////////////////////////////////////////////////////////
    }

    public string ExcludeDevice(string uid)
    {
        ///////////////////////////////////////////////////////////////////
        /// Encontra o usu�rio atrav�s do Unique ID
        dsGen.UsersRow user;
        try
        {
            user = new Users().FindUser(uid);
        }
        catch
        {
            return "InvalidUID";
        }
        ///////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////
        /// Remove poss�veis ativa��es do usu�rio atual na tabela
        dsGenTableAdapters.ActiveListTableAdapter taActiveList = new dsGenTableAdapters.ActiveListTableAdapter();
        try
        {
            taActiveList.DeleteByUserID(user.UserID);
            return "Success";
        }
        catch
        {
            return "FailedToRemoveList";
        }

        ///////////////////////////////////////////////////////////////////
    }

    public class Users
    {
        /// <summary>
        /// Encontra Usu�rio cadastrado na tabela Users
        /// </summary>
        /// <param name="uid">C�digo �nico</param>
        /// <returns>Usu�rio, throw se n�o encontrado.</returns>
        public dsGen.UsersRow FindUser(string uid)
        {
            dsGen.UsersDataTable udt = new dsGenTableAdapters.UsersTableAdapter().GetDataByUID(uid);
            if (udt.Count == 1)
            {
                return udt[0];
            }
            throw new Exception("User not found.");
        }

        /// <summary>
        /// Verifica se o UID � v�lido.
        /// </summary>
        /// <param name="uid">C�digo �nico</param>
        /// <returns>Verdadeiro se o UID for encontrado.</returns>
        public bool CheckUser(string uid)
        {
            dsGen.UsersDataTable udt = new dsGenTableAdapters.UsersTableAdapter().GetDataByUID(uid);
            if (udt.Count == 1)
            {
                return true;
            }

            return false;
        }

        /// <summary>
        /// Recupera o UID do Usu�rio.
        /// </summary>
        /// <param name="login">Login do usu�rio.</param>
        /// <param name="password">Senha do usu�rio.</param>
        /// <returns>Retorna UID se for encontrado ou c�digo de erro se n�o for encontrado.</returns>
        public string LoginUser(string login, string password)
        {
            dsGen.UsersDataTable udt = new dsGenTableAdapters.UsersTableAdapter().GetDataByLogin(login);
            if( udt.Count ==0 )
            {
                return "ErrorSystem1";
            }
            else if (udt.Count > 1)
            {
                return "ErrorSystem2";
            }
            else if (udt.Count == 1)
            {
                if (udt[0].Password.Equals(password))
                {
                    return udt[0].UID;
                }
                else
                {
                    return "InvalidPassword";
                }
            }
            else
            {
                return "ErrorUnknow";
            }
        }

        /// <summary>
        /// Inclui um novo usu�rio no sistema
        /// </summary>
        /// <param name="userName">Nome do Usu�rio</param>
        /// <param name="loginName">Login do usu�rio</param>
        /// <param name="password">Senha do usu�rio</param>
        /// <returns>Retorna o UID se for inserido com sucesso, ou vazio se n�o for incluido.</returns>
        public string AddUser(string userName, string loginName, string password)
        {
            string newGuid = Guid.NewGuid().ToString();

            try
            {
                new dsGenTableAdapters.UsersTableAdapter().Insert(
                    userName,
                    loginName,
                    password,
                    newGuid
                    );


                return newGuid;
            }
            catch
            {
                return String.Empty;
            }

        }


    }


    public class Server
    {

    }

}
