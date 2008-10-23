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
        /// Encontra o usuário através do Unique ID
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
        /// Remove possíveis ativações do usuário atual na tabela
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
        /// Insere o usuário como um novo usuário ativo
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
        /// Encontra o usuário através do Unique ID
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
        /// Remove possíveis ativações do usuário atual na tabela
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
        /// Encontra Usuário cadastrado na tabela Users
        /// </summary>
        /// <param name="uid">Código Único</param>
        /// <returns>Usuário, throw se não encontrado.</returns>
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
        /// Verifica se o UID é válido.
        /// </summary>
        /// <param name="uid">Código Único</param>
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
        /// Recupera o UID do Usuário.
        /// </summary>
        /// <param name="login">Login do usuário.</param>
        /// <param name="password">Senha do usuário.</param>
        /// <returns>Retorna UID se for encontrado ou código de erro se não for encontrado.</returns>
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
        /// Inclui um novo usuário no sistema
        /// </summary>
        /// <param name="userName">Nome do Usuário</param>
        /// <param name="loginName">Login do usuário</param>
        /// <param name="password">Senha do usuário</param>
        /// <returns>Retorna o UID se for inserido com sucesso, ou vazio se não for incluido.</returns>
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
