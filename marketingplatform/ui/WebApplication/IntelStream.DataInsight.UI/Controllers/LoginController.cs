using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using System.Web.Mvc;
using System.Web.Security;
using Nexius.IntelStream.DataInsight.UI.Models;
using Nexius.IntelStream.DataInsight.UI.Utils;

namespace Nexius.IntelStream.DataInsight.UI.Controllers
{
    public class LoginController : Controller
    {
        //
        // GET: /Login/Login
        public ActionResult Login(string returnUrl)
        {
            ViewBag.Title = "Login";
            ViewBag.ReturnUrl = returnUrl;
            return View();
        }

        //
        // POST: /Login/Login
        [AllowAnonymous]
        [HttpPost]
        public async Task<ActionResult> Login(UserModel model, string returnUrl)
        {
            if (ModelState.IsValid)
            {
                bool isLoggedIn = false;
                using (TableauAuthenticator authenticator = new TableauAuthenticator())
                {
                    try
                    {
                        isLoggedIn = await authenticator.AuthenticateAsync(model.UserName, model.Password);
                    }
                    catch (Exception) { }
                }

                if (isLoggedIn)
                {
                    FormsAuthentication.SetAuthCookie(model.UserName, model.RememberMe);
                    return RedirectToLocal(returnUrl);
                }
                else
                {
                    ModelState.AddModelError("", "Incorrect user name or password.");
                }
            }

            // If we got this far, something failed
            ViewBag.Title = "Login";
            return View(model);
        }


        private ActionResult RedirectToLocal(string returnUrl)
        {
            if (Url.IsLocalUrl(returnUrl))
            {
                return Redirect(returnUrl);
            }
            else
            {
                return RedirectToAction("Index", "Home");
                //return RedirectToAction("Index", "Mixed");
            }
        }

        private IEnumerable<string> GetErrorsFromModelState()
        {
            return ModelState.SelectMany(x => x.Value.Errors.Select(error => error.ErrorMessage));
        }

        //
        // POST: /Login/LogOut
        [Authorize]
        [HttpPost]
        public ActionResult LogOut()
        {
            FormsAuthentication.SignOut();
            return RedirectToAction("Login", "Login");
        }
    }
}