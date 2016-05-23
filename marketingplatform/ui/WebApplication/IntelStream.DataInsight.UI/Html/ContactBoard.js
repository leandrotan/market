
// Functions...
var contactBoardModel = null;
var fieldValidator = null;
function initPageScript() {
    // Initialize tabs.
    $("#tabstrip").kendoTabStrip({
        animation: { open: { effects: "fadeIn" } }
    });   

    $("#contact-track-grid-toolbar").kendoToolBar({
        // Bulk actions.
        items: [
            {
                type: "button",
                id: "newTweetButton",
                text: "Send Tweet",
                icon: "pencil",
                click: newTweetClickHandler
            },
            { type: "separator" },
            {
                type: "button",
                id: "closeButton",
                text: "Close",
                icon: "close",
                click: closeClickHandler
            }
        ]
    });
    $("#contact-track-grid-acq-toolbar").kendoToolBar({
        // Bulk actions.
        items: [
            {
                type: "button",
                id: "newTweetButton",
                text: "Send Tweet",
                icon: "pencil",
                click: newTweetClickHandler
            },
            { type: "separator" },
            {
                type: "button",
                id: "closeButton",
                text: "Close",
                icon: "close",
                click: closeClickHandler
            }
        ]
    });

    createGrids();
    kendo.bind($("#contact-track-mainwindow"), contactBoardModel);
    fieldValidator = $("#close-modalwindow").kendoValidator({
        errorTemplate: "<span>*</span>"
    }).data("kendoValidator");
  
}

// --------- Handlers ....
function closeClickHandler() {

    $("#users-text-area").val("");
    $("#comments-text-area").val("");

    // Populate controls in "Close" pop up...
    var sourceGrid = $(getActiveGridId()).data("kendoGrid");
    var selectedRows = sourceGrid.select();
    if (!selectedRows || selectedRows.length < 1) {
        return;
    }
    var usersTextAreaElem = $("#users-text-area");
    selectedRows.each(function (index, row) {
        var selectedItem = sourceGrid.dataItem(row);
        if (selectedItem.user)
            usersTextAreaElem.val(usersTextAreaElem.val() + selectedItem.user + ",");
    });
    cleanTailingCommas(usersTextAreaElem);

    // Close pop up related...
    $("#close-modalwindow").kendoWindow({
        title: "Close",
        resizable: false,
        modal: true,
        visible: false
    });

    var kendoWindow = $("#close-modalwindow");
    kendoWindow.data("kendoWindow").open();
    kendoWindow.data("kendoWindow").center();

    if (!closeThreadEventsRegistered) {
        $("#close-cancel-button").click(function () {
            $("#errorMessage").hide();
            $("#close-modalwindow").data("kendoWindow").close();
        });

        $("#close-ok-button").click(function () {
            if (!fieldValidator.validate()) {
                $("#errorMessage").show();
                return;
            }

            var grid = $(getActiveGridId()).data("kendoGrid");
            var rows = grid.select();
            rows.each(function (index, row) {
                var selectedItem = grid.dataItem(row);
                if (selectedItem.status) {
                    selectedItem.set('status', 'closed');
                }
            });

            // Close pop up and refresh grid data...
            $("#errorMessage").hide();
            $("#close-modalwindow").data("kendoWindow").close();
        });
        closeThreadEventsRegistered = true;
    }
}

function newTweetClickHandler() {

    $("#destination-text-area").val("");
    $("#tweet-text-area").val("");

    // Populate controls in "New Tweets" pop up.
    var sourceGrid = $(getActiveGridId()).data("kendoGrid");
    var selectedRows = sourceGrid.select();
    if (!selectedRows || selectedRows.length < 1) {
        return;
    }
    var destinationTextAreaElem = $("#destination-text-area");
    selectedRows.each(function (index, row) {
        var selectedItem = sourceGrid.dataItem(row);
        if (selectedItem.user) {
            destinationTextAreaElem.val(destinationTextAreaElem.val() + selectedItem.user + ",");
        }
    });
    cleanTailingCommas(destinationTextAreaElem);

    // New tweets pop up related...
    $("#new-tweets-modalwindow").kendoWindow({
        title: "Send new tweets",
        resizable: false,
        modal: true,
        visible: false
    });

    // ComboBoxes init.
    $("#category").kendoDropDownList({
        optionLabel: "Select category...",
        dataTextField: "parentID",
        dataValueField: "parentID",
        change: sentimentChangeHandler,
        dataSource: test_categories
    });

    $("#subcategory").kendoDropDownList({
        cascadeFrom: "category",
        optionLabel: "Select sub-category...",
        dataTextField: "value",
        dataValueField: "value",
        change: sentimentChangeHandler,
        dataSource: test_subcategories
    });

    $("#sentiment").kendoDropDownList({
        optionLabel: "Select sentiment...",
        dataTextField: "value",
        dataValueField: "value",
        change: sentimentChangeHandler,
        dataSource: test_sentiments
    });

    // Add tokens buttons.
    function insertToken(tokenValue) {
        var cursorPos = $('#tweet-text-area').prop('selectionStart'),
        value = $('#tweet-text-area').val(),
        textBefore = value.substring(0, cursorPos),
        textAfter = value.substring(cursorPos, value.length);
        $('#tweet-text-area').val(textBefore + tokenValue + textAfter);
    }

    $("#new-tweets-add-user-token").kendoButton({ imageUrl: 'Images/icon-at.png' });
    var addUserTokenButton = $("#new-tweets-add-user-token").data("kendoButton");
    addUserTokenButton.bind("click", function () {
        insertToken('###tweet_account###');
    });

    $("#new-tweets-add-questionarie-token").kendoDropDownList({
        optionLabel: "Add Questionarie Token",
        filter: "contains",
        dataTextField: "name",
        dataValueField: "value",
        change: function (e) {
            var item = this.value();
            insertToken('###questionarie_' + item + '###');
        },
        dataSource: test_questionaries
    });

    // Populate window.
    var kendoWindow = $("#new-tweets-modalwindow");
    kendoWindow.data("kendoWindow").open();
    kendoWindow.data("kendoWindow").center();

    if (!newTweetEventsRegistered) {
        $("#new-tweets-cancel-button").click(function () {
            $("#new-tweets-modalwindow").data("kendoWindow").close();
        });
        $("#new-tweets-ok-button").click(function () {
            alert('Tweet has been sent!');
            $("#new-tweets-modalwindow").data("kendoWindow").close();
        });
        newTweetEventsRegistered = true;
    }
}

function cleanTailingCommas(elem) {
    var text = elem.val();
    if (text && text.length > 0) {
        elem.val(text.substring(0, text.length - 1));
    }
}

function sentimentChangeHandler() {
    var tweetTemplate = '';
    $("#tweet-text-area").val("");
    var category = $("#category").val();
    var subcategory = $("#subcategory").val();
    var sentiment = $("#sentiment").val();
    var workflowType = $("#tabstrip").data("kendoTabStrip").select()[0].textContent;
    switch (sentiment) {
        case 'Severe':
            {
                switch (category) {
                    case 'Network':
                        {
                            switch (subcategory) {
                                case 'Voice':
                                    if (workflowType == "My Customers")
                                        tweetTemplate = 'Dear  ###tweet_account###, we thank you for your message; our technical team informed and will look into this issue';
                                    break;
                                case 'Coverage':
                                    if (workflowType == "My Customers")
                                        tweetTemplate = 'Dear  ###tweet_account###, we will check with the concerned team and get back to you';
                                    break;
                                case 'Data':
                                    if (workflowType == "My Customers")
                                        tweetTemplate = 'Dear ###tweet_account###, we thank you for your message; would you please provide details about your handset and exact location';
                                    break;
                            }
                            break;
                        }
                }
            }
        case 'Negative':
            {
                switch (category) {
                    case 'Business Affairs':
                        {
                            switch (subcategory) {
                                case 'Handset':
                                    if (workflowType == "My Customers")
                                        tweetTemplate = 'Dear ###tweet_account###, we thank you for your message; would you please provide details about your mobile device';
                                    break;
                            }
                            break;
                        }
                }
            }
            break;
        case 'Positive':
            {
                switch (category) {
                    case 'Customer Care':
                        {
                            switch (subcategory) {
                                case 'Customer Care':
                                    if (workflowType == "My Customers")
                                        tweetTemplate = 'أهلا وسهلاً، نحن هنا دائماً للمساعدة';
                                    break;
                            }
                            break;
                        }
                }
            }
            break;
        case 'PotentialChurner':
            {
                switch (category) {
                    case 'Network':
                        {
                            switch (subcategory) {
                                case 'Voice':
                                    if (workflowType == "Acquisitions")
                                        tweetTemplate = 'Hi ###tweet_account###, we would like to inform you that you can follow us to learn about our new products and services.';
                                    break;
                                case 'Coverage':
                                    if (workflowType == "Acquisitions")
                                        tweetTemplate = 'Hi ###tweet_account###, did you know we have the best coverage in Lebanon? Follow us to receive our latest packages';
                                    break;
                                case 'Data':
                                    if (workflowType == "Acquisitions")
                                        tweetTemplate = 'Hi ###tweet_account###, we would like to inform you that you can follow us to learn about our new products and services.';
                                    break;
                            }
                            break;
                        }
                    case 'Business Affairs':
                        {
                            switch (subcategory) {
                                case 'Pricing or Billing':
                                    if (workflowType == "Acquisitions")
                                        tweetTemplate = 'Hi ###tweet_account###, did you know that you can customize your plan and save money? Follow us to learn about our latest offers.';
                                    break;
                            }
                            break;
                        }
                }
            }
            break;
    }

    $("#tweet-text-area").val(tweetTemplate);
}

function createGrids() {
    contactBoardModel = kendo.observable({
        showDetailsWindow: function (e) {
            if (!e.data) 
                return false;
            $.ajaxSetup({ cache: false });
            var rowData = e.data;
            $(getActiveGridId()).data("kendoGrid").clearSelection();
            $(getActiveGridId()).data("kendoGrid").select("[data-uid='" + rowData.uid + "']");
            $("#contactBoardDetailsPH").load("Html/ContactBoardDetail.html", function () {
                showDetailsPopup(rowData);
            });
        },
        contactBoardData: new kendo.data.DataSource({
            data: contact_board_test_data,
            schema: {
                model: {
                    id: "id",
                    fields: getModelSchema("My Customers")
                }
            }
        }),
        contactBoardDataAcq: new kendo.data.DataSource({
            data: contact_board_test_data_acquisition,
            schema: {
                model: {
                    id: "id",
                    fields: getModelSchema("Acquisitions")
                }
            }
        }
        )
    });

    // My Customers grid.
    $("#contact-track-grid").kendoGrid({
        filterable: true,
        selectable: "multiple row",
        columns: getGridColumns("My Customers")
    }).data("kendoGrid");

    // Create Acquisitions grid.
    $("#contact-track-grid-acq").kendoGrid({
        filterable: true,
        selectable: "multiple row",
        columns: getGridColumns("Acquisitions")
    }).data("kendoGrid");

    kendo.bind($("#contact-track-mainwindow"), contactBoardModel);
}

function getGridColumns(workflowType) {
    var columns = [
        {
            filterable: { extra: false },
            field: 'user',
            title: 'User account',
            width: "100px"
        },
        {
            filterable: { multi: true },
            field: 'status',
            title: 'Status',
            width: "70px"
        },
        {
            filterable: false,
            field: 'auto_message_date',
            title: 'Date of auto-message',
            width: "100px",
            format: '{0:yyyy/MM/dd HH:mm:ss}'
        },
        {
            filterable: { multi: true },
            field: 'auto_message_name_rules',
            title: 'Auto-message Cat/Subcat',
            width: "130px"
        },
        {
            filterable:false,
            field: 'has_auto_message_questionnaire',
            title: 'Auto-message questionnaire',
            width: "140px",
            template: kendo.template($("#contact-track-grid-column-rules").html())
        },
        {
            filterable: false,
            field: 'date_last_tweet_sent',
            title: 'Our last tweet',
            width: "100px",
            format: '{0:yyyy/MM/dd HH:mm:ss}'
        },
        {
            filterable: false,
            field: 'date_last_tweet_received',
            title: "User's last tweet",
            width: "100px",
            format: '{0:yyyy/MM/dd HH:mm:ss}'
        },
        {
            template: '<div align=center><a data-role="button" name="viewDetails" data-icon="more" data-bind="click: showDetailsWindow">Details</a></div>',
            width: "85px"
        }
    ];
    if (workflowType == "Acquisitions") {
        var competitor = {
            filterable: {multi: true},
            field: 'competitor',
            title: 'Competitor',
            width: "75px",
            template: '<div align="center"><img alt="#=competitor#" title="#=competitor#" src="Images/logo_#=competitor#.png" style="width: 58px; height:58px; background-color: white;"/></div>'
        };
        columns.splice(1, 0, competitor);
    }
    return columns;
}

function getModelSchema(workflowType) {
    var schema = {
        user: { type: "string" },
        competitor: { type: "string" },
        status: { type: "string" },
        auto_message_name_rules: { type: "string" },
        has_auto_message_questionarie: { type: "string" },
        auto_message_date: { type: "datetime" },
        replied_auto_message: { type: "datetime" },
        date_last_tweet_sent: { type: "datetime" },
        date_last_tweet_received: { type: "datetime" }
    };
    if (workflowType == "Acquisitions")
        schema["competitor"] = { type: "string" };

    return schema;
}


function getActiveGridId() {
    var gridId;
    if (getSelectedTabName() == "Acquisitions")
        gridId = "#contact-track-grid-acq";
    else
        gridId = "#contact-track-grid";
    return gridId;
}

function getSelectedTabName() {
    return $("#tabstrip").data("kendoTabStrip").select()[0].textContent;
}

//# sourceURL=ContactBoard.js
