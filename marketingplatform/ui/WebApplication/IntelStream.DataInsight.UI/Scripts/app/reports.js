/// <reference path="~/Scripts/jquery-1.10.2.min.js" />
/// <reference path="~/Scripts/kendo/kendo.all.min.js" />


function ReportingApp(reportingSettings) {
    var that = this;

    var _tableauViz = reportingSettings.tableauViz;
    var _vizWrapper = reportingSettings.vizWrapper;

    var _workbook = null;
    var _activeSheet = null;

    this.setViz = function (viewurl) {

        var that = this;

        if (_tableauViz) {
            _tableauViz.dispose();
        }

        var w = $(_vizWrapper).width();
        var h = $(_vizWrapper).height();
        $(_vizWrapper).html("");

        var options = {
            width: w,
            height: h,
            hideTabs: true,
            hideToolbar: false,
            onFirstInteractive: function () {
                _workbook = _tableauViz.getWorkbook();
                _activeSheet = _workbook.getActiveSheet();
                //that.initializeReport();
            }
        };
        $.ajax({
            type: "POST",
            cache: false,
            url: AppPath + "Home/GetTrustedURL",
            data: {},
            success: function (data) {
                trustedUrl = data.trustedUrl;               
                _tableauViz = new tableauSoftware.Viz(_vizWrapper, trustedUrl + viewurl, options);
            }
        });
    };

    this.initializeReport = function () {
        //nothing to do for now
    };

    this.disposeViz = function () {
        if (_tableauViz) {
            _tableauViz.dispose();
        }
    }
}
