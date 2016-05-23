using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Optimization;

namespace Nexius.IntelStream.DataInsight.UI
{
    public class BundleConfig
    {
        public static void RegisterBundles(BundleCollection bundles)
        {
            bundles.Add(new ScriptBundle("~/bundles/apis").Include(
                        "~/Scripts/jquery-1.10.2.min.js",
                        "~/Scripts/bootstrap.min.js",
                        "~/Scripts/modernizr-2.6.2.js",
                        "~/Scripts/tableau_v8.js",
                        "~/Scripts/jsonselect.js"
                        ));

            bundles.Add(new ScriptBundle("~/bundles/kendo").Include(
                        "~/Scripts/kendo/kendo.all.min.js",
                        "~/Scripts/kendo/kendo.aspnetmvc.min.js"));

            bundles.Add(new ScriptBundle("~/bundles/wicom").Include(
                        "~/Scripts/app/custom-jquery-extensions.js",
                        "~/Scripts/app/reports.js"));

            bundles.Add(new StyleBundle("~/Content/bootstrap/css").Include(
                        "~/Content/bootstrap.min.css",
                        "~/Content/bootstrap-theme.min.css"));

            bundles.Add(new StyleBundle("~/Content/kendo/css").Include(
                        "~/Content/kendo/kendo.common-bootstrap.min.css",
                        "~/Content/kendo/kendo.bootstrap.min.css",
                        "~/Content/kendo/kendo.bootstrap.mobile.min.css",
                        "~/Content/kendo/kendo.dataviz.min.css",
                        "~/Content/kendo/kendo.dataviz.bootstrap.min.css"));

            bundles.Add(new StyleBundle("~/Content/wicom/css").Include(
                        "~/Content/Site.css"));


            bundles.IgnoreList.Clear();
        }
    }
}

