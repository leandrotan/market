function showDetailsPopup(selectedItem) {
	//verify if item is "my customer" or "acquisition"to filter out statuses
	var status = [{ name: "contacted" }, { name: "solved issue" }
                , { name: "not solved issue" }, { name: "closed" } ];
	if (selectedItem && selectedItem.hasOwnProperty("competitor")
			&& selectedItem.competitor && selectedItem.competitor!="") {
		status = [{ name: "contacted" }, { name: "interested" }
                , { name: "offer sent" }, { name: "aquired" }
                , { name: "not interested" }, { name: "closed" }];
	}
    var viewModel = kendo.observable({
        statuses: status,
        addComment: function () {
            var tempData = {
                author: currentLoggerUser,
                date: new Date().toJSON().replace('T', ' ').slice(0, -5),
                content: $("#newContent").val()
            };
            this.get("selectedItem").details.comments.push(tempData);
            $("#newContent").val("");
        },
        showQuestionnaire: function (event) {
            if (!event.data.questionnaire.reply_date) {
				alert('The questionnaire is not responded.');
                return false;
            }
            kendo.bind('#questionnaireWindow', event.data);
            var myWin = $("#questionnaireWindow");
            if (!myWin.data("kendoWindow")) {
                myWin.kendoWindow({
                    animation: { close: { effects: "fade:out" } },
                    width: "420px",
                    height: "440px",
                    title: "Questionnaire Response",
                    modal: true,
                    resizable: false,
                    visible: false
                });
                myWin.data("kendoWindow")
                    .center()
                    .open();
            } else {
                // reopening window
                myWin.data("kendoWindow")
                    .center()
                    .open(); // open the window
            }

        },
        selectedItem: selectedItem
    });   

    var kwin = $("#detailsWindow").kendoWindow({
        animation: { close: { effects: "fade:out" } },
        width: "680px",
        height: "580px",
        title: "Details",
        modal: true,
        resizable: false,
        visible: false,
        deactivate: function () {
            this.destroy();
        }
    }).data("kendoWindow").center().open();

     kendo.bind('#detailsWindow', viewModel);
}

function toggleMessageReplies(src) {
    $(src).siblings('.messageReplies').toggle();
}
function renderTemplate(templateId, data) {
    return kendo.Template.compile($('#' + templateId).html())(data);
}

//# sourceURL=ContactBoardDetail.js