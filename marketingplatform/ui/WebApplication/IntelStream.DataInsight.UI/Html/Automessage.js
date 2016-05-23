var automessageModel = null;

function initPageScript() {

    automessageModel = kendo.observable({
        automessageData: new kendo.data.DataSource({
            schema: {
                model: {
                    id: "id",
                    fields: {
                        id: { type: "number" },
                        name: { type: "string" },
                        workflow: { type: "string" },
                        sentiment: { type: "string" },
                        category: { type: "string" },
                        subcategory: { type: "string" },
                        sent: { type: "number" },
                        reply: { type: "number" },
                        message: { type: "string" },
                        date: { type: "datetime" },
                        user: { type: "string" },
                    }
                }
            },
            transport: {
                read: function (o) {
                    //pass the date
                    o.success(automessage_test_data);
                },
                create: function (o) {
                    var item = o.data;
                    //assign a unique ID and return the record
                    item.id = automessage_test_data.length + 1;
                    o.success(item);
                },
                update: function (o) {
                    o.success();
                },
                destroy: function (o) {
                    o.success();
                }
            }
        })
    });

    $("#automessage-grid").kendoGrid({
        toolbar: [
            {
                name: "create",
                text: "Create new Auto-Message"
            }
        ],
        filterable: true,
        autoSync: true,
        editable: {
            mode: "popup",
            createAt: "bottom",
            confirmation: "Are you sure to delete this Auto-message?",
            update: true,
            template: kendo.template($("#automessage-popup-editor").html()),
            window: {
                title: "Auto-message settings"
            }
        },
        columns:
        [
            {
                filterable: {extra: false},
                field: "name",
                title: "Auto-message Name",
                width: "170px"
            },
            {
                filterable: { multi: true },
                field: "workflow",
                title: "Workflow Type",
                width: "100px"
            },
            {
                filterable: false,
                title: "Rules",
                width: "250px",
                template: kendo.template($("#automessage-grid-column-rules").html())
            },
            {
                filterable: false,
                title: "Auto-Messaging Summary",
                width: "160px",
                template: kendo.template($("#automessage-grid-column-summary").html())
            },
            {
                filterable: false,
                field: "date",
                title: "Created Date",
                width: "125px",
                format: '{0:yyyy/MM/dd HH:mm:ss}'
            },
            {
                filterable: false,
                field: "user",
                title: "User",
                width: "100px"
            },
            {
                command: [
                    {
                        name: "edit",
                        className: "gridCommandButton"
                    },
                    {
                        name: "destroy",
                        className: "gridCommandButton"
                    }
                ],
                title: "&nbsp;",
                width: "140px"
            }
        ],
        save: function (data) {
            if (data.model.isNew()) {
                data.model.set("user", currentLoggerUser);
                data.model.set("date", new Date());
            }
        },
        edit: function (e) {
            if (e.model.isNew()) {
                $("a.k-grid-update")[0].innerHTML = "<span class='k-icon k-update'></span>Save";
            }
        }
    });

    kendo.bind($("#automessage-mainwindow"), automessageModel);
};

var automessagePopupHandlers = {

    addUserToken: function (e) {
        var textElem = $("#automessage-template-text");
        var pos = textElem.getCursorPosition();
        var currentText = textElem.val();
        textElem.val(currentText.substring(0, pos) + "###tweet_account###" + currentText.substring(pos, currentText.length));
    },

    selectQuestionarie: function (e) {
        var item = this.value();
        var textElem = $("#automessage-template-text");
        var pos = textElem.getCursorPosition();
        var currentText = textElem.val();
        textElem.val(currentText.substring(0, pos) + "###questionarie_" + item + "###" + currentText.substring(pos, currentText.length));
        this.value(null);
    },

    messsageTemplateSelection: function (e) {
        var tweetTemplate = '';
        var textElem = $("#automessage-template-text");
        textElem.val("");
        var category = $("#automessage-category").val();
        var subcategory = $("#automessage-subcategory").val();
        var sentiment = $("#automessage-sentiment").val();
        var workflowType = $("#automessage-workflow").val();
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

        textElem.val(tweetTemplate);
    }
};


//# sourceURL=Automessage.js