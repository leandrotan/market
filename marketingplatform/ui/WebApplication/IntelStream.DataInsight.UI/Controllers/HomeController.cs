using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml.XPath;
using System.Web;
using System.Web.Mvc;
using System.Web.Security;
using System.Configuration;
using System.Numerics;
using System.Net.Http;
using Nexius.IntelStream.DataInsight.UI.Models;
using Nexius.IntelStream.DataInsight.UI.Utils;
using System.Xml.Linq;
using System.Threading.Tasks;
using System.Security.Cryptography;
using System.Text;
using System.Text.RegularExpressions;
using System.Xml;
using Newtonsoft.Json;

namespace Nexius.IntelStream.DataInsight.UI.Controllers
{
    [Authorize]
    public class HomeController : Controller
    {
        //
        // GET: /Home/Index
        public ActionResult Index(string returnUrl)
        {
            ViewBag.ReturnUrl = returnUrl;
            ViewBag.ViewMode = "home";

            if (ModelState.IsValid && User.Identity.IsAuthenticated)
            {
                var model = new ReportModel();
                var userNameParts = User.Identity.Name.Split('.');
                if (userNameParts.Length > 0)
                    model.UserName = userNameParts[0].First().ToString().ToUpper() + userNameParts[0].Substring(1);

                model.DashboardGroups = LoadDashboardData();
                ViewBag.Title = "Data Insight";
                return View(model);
            }

            ViewBag.Title = "Login";
            return View();
        }

        private string LoadDashboardData() {
            try
            {
                XmlDocument dashboardsXML = new XmlDocument();
                dashboardsXML.XmlResolver = null;
                dashboardsXML.Load(Server.MapPath("~/App_Data/dashboards.xml"));
                var jsonText = JsonConvert.SerializeXmlNode(dashboardsXML.DocumentElement, Newtonsoft.Json.Formatting.Indented, true);
                // in the xml to json conversion the xml node attributes are has "@" as prefix
                // removing the attribute prefix from json objects
                jsonText = Regex.Replace(jsonText, "(\"@)", "\"", RegexOptions.IgnoreCase);
                return jsonText;
            }
            catch(Exception e) {
                Console.WriteLine("Dashboard data error - " + e.Message);
                Console.WriteLine(e.StackTrace);
            }
            return string.Empty;
        }

        //
        // POST: /Home/GetTrustedURL
        [Authorize]
        [HttpPost]
        public async Task<ActionResult> GetTrustedURL()
        {
            var ticket = string.Empty;
            TableauAuthenticator ta = null;
            try
            {
                ta = new TableauAuthenticator();
                ticket = await ta.GetTicket(User.Identity.Name);
            }
            catch (Exception ex)
            {
                Console.WriteLine("Tablau Ticket Error - " + ex.Message);
                Console.WriteLine(ex.StackTrace);
            }
            finally
            {
                ta.Dispose();
            }
            var trustedUrl = ConfigurationManager.AppSettings["tableauServerAddress"]+"/trusted/"+ticket;
            var data = new { trustedUrl = trustedUrl };
            return Json(data);
        }

    }



}