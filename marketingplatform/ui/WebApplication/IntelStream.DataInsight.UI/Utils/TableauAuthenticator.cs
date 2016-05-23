using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Numerics;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;
using System.Web;
using System.Xml;
using System.Xml.Linq;
using System.Xml.Serialization;
using Nexius.IntelStream.DataInsight.UI.Utils.Tableau;

namespace Nexius.IntelStream.DataInsight.UI.Utils
{
    public class TableauAuthenticator : IDisposable
    {
        private static readonly string _tableauServer;
        private static readonly string _tableauSite;
        private static readonly string _tableauTicketGeneratorURL;
        private static readonly bool _useTableauTicketGenerator;
        private static readonly string _tableauApiPath;
        private HttpClient _client;
        private string _securityToken = string.Empty;

        static TableauAuthenticator()
        {
            _tableauServer = ConfigurationManager.AppSettings["tableauServerAddress"];
            _tableauSite = ConfigurationManager.AppSettings["tableauSiteName"];
            _tableauTicketGeneratorURL = ConfigurationManager.AppSettings["ticketGeneratorURL"];
            _useTableauTicketGenerator = ConfigurationManager.AppSettings["useTicketGenerator"].ToLower().Equals("true");

            _tableauApiPath = "api/2.0/auth/";
        }

        public TableauAuthenticator()
        {
            _client = new HttpClient();
            _client.BaseAddress = new Uri(_tableauServer);
        }

        #region Authentication

        public async Task<bool> AuthenticateAsync(string userName, string password)
        {
            var authenticated = false;

            try
            {
                var request = new tsRequest()
                {
                    Item = new tableauCredentialsType()
                    {
                        name = userName,
                        password = password,
                        site = new siteType()
                        {
                            contentUrl = _tableauSite
                        }
                    }
                };

                var serializer = new XmlSerializer(typeof(tsRequest));
                using (var stream = new MemoryStream())
                {
                    serializer.Serialize(stream, request);
                    stream.Position = 0;
                    using (var content = new StreamContent(stream))
                    {
                        var loginTask = _client.PostAsync(_tableauApiPath + "signin", content)
                             .ContinueWith((x) =>
                             {
                                 if (x.Result.IsSuccessStatusCode)
                                 {
                                     authenticated = true;
                                     /*
                                     var resultContentTask = x.Result.Content.ReadAsStringAsync();
                                     serializer = new XmlSerializer(typeof(tsResponse));
                                     var sr = new StringReader(resultContentTask.Result);
                                     XmlTextReader xmlReader = new XmlTextReader(sr);
                                     var response = serializer.Deserialize(xmlReader) as tsResponse;
                                     if (response != null && response.Items.Length > 0)
                                     {
                                         _securityToken = (response.Items[0] as tableauCredentialsType).token;
                                     }*/
                                 }
                             });
                        await loginTask;
                    }
                }

            }
            catch (Exception ex)
            {
                Console.WriteLine("Invalid Login! - " + ex.Message + Environment.NewLine + "Trace -> " + ex.StackTrace);
                throw new Exception("Invalid User Name / Password");
            }
            return authenticated;
        }

        #endregion Authentication

        #region Ticket Gathering

        public async Task<string> GetTicket(string userName)
        {
            if (!_useTableauTicketGenerator)
            {
                return await GetTicketLocal(userName);
            }
            return await GetTicketFromGenerator(userName);
        }


        private async Task<string> GetTicketLocal(string userName)
        {
            var data = new MultipartFormDataContent();
            data.Add(new StringContent(userName), "\"username\"");
            data.Add(new StringContent(_tableauSite), "\"target_site\"");

            var ticketTask = _client.PostAsync("trusted/", data).ContinueWith(
                async (x) => Encoding.UTF8.GetString(await x.Result.Content.ReadAsByteArrayAsync()));
            await ticketTask;
            return await ticketTask.Result;
        }

        private async Task<string> GetTicketFromGenerator(string userName)
        {
            var url = _tableauTicketGeneratorURL + "/" + userName + "/" + _tableauSite;
            var ticketTask = _client.GetAsync(url).ContinueWith(
                async (x) =>
                    await x.Result.Content.ReadAsStringAsync());
            var ticket = await ticketTask.Result;
            return ticket;
        }

        #endregion

        public void Dispose()
        {
            if (_client != null)
            {
                _client.Dispose();
            }
        }
    }
    
}