// ------------------------------------------------------------------------
// ------------------------------------------------------------------------
// ------------------------ AUTOMESSAGE TEST DATA -------------------------
// ------------------------------------------------------------------------
// ------------------------------------------------------------------------

var automessage_test_data = [
    {
        id: 1,
        name: "Auto-Message Rule Demo 1",
        workflow: "My Customers",
        sentiment: "Severe",
        category: "Network",
        subcategory: "Voice",
        sent: 122,
        reply: 51,
        message: "Dear ###tweet_account###, we thank you for your message; our technical team informed and will look into this issue",
        date: new Date("2015-06-01 10:35:14 AM"),
        user: "Demo User"
    },
    {
        id: 2,
        name: "Auto-Message Rule Demo 2",
        workflow: "My Customers",
        sentiment: "Severe",
        category: "Network",
        subcategory: "Coverage",
        sent: 89,
        reply: 22,
        message: "Dear ###tweet_account###, we will check with the concerned team and get back to you",
        date: new Date("2015-06-02 10:45:33 PM"),
        user: "Demo User"
    },
    {
        id: 3,
        name: "Auto-Message Rule Demo 3",
        workflow: "My Customers",
        sentiment: "Severe",
        category: "Network",
        subcategory: "Data",
        sent: 143,
        reply: 59,
        message: "Dear ###tweet_account###, we thank you for your message; would you please provide details about your handset and exact location",
        date: new Date("2015-06-02 10:12:13 AM"),
        user: "Demo User"
    },
    {
        id: 4,
        name: "Auto-Message Rule Demo 4",
        workflow: "My Customers",
        sentiment: "Negative",
        category: "Business Affairs",
        subcategory: "Handset",
        sent: 21,
        reply: 7,
        message: "Dear ###tweet_account###, we thank you for your message; would you please provide details about your mobile device",
        date: new Date("2015-06-03 08:34:21 AM"),
        user: "Demo User"
    },
    {
        id: 5,
        name: "Auto-Message Rule Demo 5",
        workflow: "My Customers",
        sentiment: "Positive",
        category: "Customer Care",
        subcategory: "Customer Care",
        sent: 10,
        reply: 8,
        message: "أهلا وسهلاً، نحن هنا دائماً للمساعدة",
        date: new Date("2015-06-03 03:18:17 PM"),
        user: "Demo User"
    },
    {
        id: 6,
        name: "Auto-Message Rule Demo 6",
        workflow: "Acquisition",
        sentiment: "Potential Churner",
        category: "Network",
        subcategory: "Voice",
        sent: 77,
        reply: 63,
        message: "Hi ###tweet_account###, we would like to inform you that you can follow us to learn about our new products and services.",
        date: new Date("2015-06-03 02:05:20 PM"),
        user: "Demo User"
    },
    {
        id: 7,
        name: "Auto-Message Rule Demo 7",
        workflow: "Acquisition",
        sentiment: "Potential Churner",
        category: "Network",
        subcategory: "Coverage",
        sent: 35,
        reply: 21,
        message: "Hi ###tweet_account###, did you know we have the best coverage in Lebanon? Follow us to receive our latest packages.",
        date: new Date("2015-06-03 02:08:27 PM"),
        user: "Demo User"
    },
    {
        id: 8,
        name: "Auto-Message Rule Demo 8",
        workflow: "Acquisition",
        sentiment: "Potential Churner",
        category: "Network",
        subcategory: "Data",
        sent: 98,
        reply: 73,
        message: "Hi ###tweet_account###, we would like to inform you that you can follow us to learn about our new products and services.",
        date: new Date("2015-06-03 02:15:54 PM"),
        user: "Demo User"
    },
    {
        id: 9,
        name: "Auto-Message Rule Demo 9",
        workflow: "Acquisition",
        sentiment: "Potential Churner",
        category: "Business Affairs",
        subcategory: "Pricing or Billing",
        sent: 25,
        reply: 10,
        message: "Hi ###tweet_account###, did you know that you can customize your plan and save money? Follow us to learn about our latest offers.",
        date: new Date("2015-06-03 04:52:51 PM"),
        user: "Demo User"
    }
];

var automessage_test_categories = [
    { parentID: 'Network' },
    { parentID: 'Customer Care' },
    { parentID: 'Business Affairs' }
];

var automessage_test_subcategories = [
    { parentID: 'Network', value: 'Voice' },
    { parentID: 'Network', value: 'Data' },
    { parentID: 'Network', value: 'Coverage' },
    { parentID: 'Network', value: 'Messaging' },
    { parentID: 'Network', value: 'Roaming' },
    { parentID: 'Network', value: 'Other Network' },
    { parentID: 'Customer Care', value: 'Customer Care' },
    { parentID: 'Business Affairs', value: 'Shops' },
    { parentID: 'Business Affairs', value: 'Handset' },
    { parentID: 'Business Affairs', value: 'Pricing or Billing' },
    { parentID: 'Business Affairs', value: 'Products and Plans' },
    { parentID: 'Business Affairs', value: 'Contracts' },
    { parentID: 'Business Affairs', value: 'Other Business Affairs' }
];

var automessage_test_questionaries = [
    { value: 1, name: "User Device Questionarie" },
    { value: 2, name: "User Coverage Area Questionarie" },
    { value: 3, name: "User Shops Attendance Questionarie" },
    { value: 4, name: "User Satisfaction Questionarie" },
    { value: 5, name: "User Acquisition Questionarie" }
];

// ------------------------------------------------------------------------
// ------------------------------------------------------------------------
// -------------------- END AUTOMESSAGE TEST DATA -------------------------
// ------------------------------------------------------------------------
// ------------------------------------------------------------------------




// ------------------------------------------------------------------------
// ------------------------------------------------------------------------
// ---------------------- CONTACT BOARD TEST DATA -------------------------
// ------------------------------------------------------------------------
// ------------------------------------------------------------------------
var contact_board_test_data = [
   {
    "id": 1,
    "user": "@AhmedElqaddah",
    "status": "contacted",
    "auto_message_date": "2015/05/21 12:33:10",
    "auto_message_name_rules": "Customer Care/Customer Care",
    "questionnaire_name": "Customer Satisfaction",
    "questionnaire_reply_date": "2015-05-22 09:18:10",
    "date_last_tweet_sent": "2015/05/22 12:22:10",
    "date_last_tweet_received": "2015/05/22 09:22:10",
    "original_category": "Customer Care",
    "original_subcategory": "Customer Care",
    "original_score": "Negative",
    "last_tweet_received_score": "Neutral",
    "details": {
        "messages": [
        {
            "type": "IN",
            "from": "@hsabry_",
            "date": "2015/05/21 11:30:04",
            "content": "@touchLebanon major complaint to report.Been 3 years paying for data plan that was \"accidentally never activated\" Could b affecting other clients",
            "category": "Customer Care",
            "subcategory": "Customer Care",
            "score": "Negative",
            "replies": []
        },
        {
            "type": "AUTO-MESSAGE",
            "from": "@touchLebanon",
            "date": "2015/05/22 09:18:10",
            "content": "@hsabry_ would you please send your mobile number and complaint in details through the DM to be able to check ?",
            "questionnaire": {
                "name": "Customer Satisfaction",
                "reply_date": "2015/05/22 09:18:10",
                "content": [
                {
                    "label": "First Name",
                    "response": "Ahmed"
                },
                {
                    "label": "Last Name",
                    "response": "El Qaddah"
                },
                {
                    "label": "E-mail",
                    "response": "ahmedelqaddah@gmail.com"
                },
                {
                    "label": "Mobile Phone Number",
                    "response": "+9613111222"
                },
                {
                    "label": "Facebook Name",
                    "response": ""
                },
                {
                    "label": "Twitter Handle",
                    "response": "@hsabry_"
                },
                {
                    "label": "Account",
                    "response": "Postpaid"
                },
                {
                    "label": "Device manufacturermodel",
                    "response": "Nokia-1100"
                },
                {
                    "label": "Time in company",
                    "question": "Since how long you are using our services?",
                    "response": "More than 1 year"
                },
                {
                    "label": "Location",
                    "question": "Please provide your location",
                    "response": "Lebanon, Beirut, Achrafieh, St Louis Rd, BLG 2001"
                },
                {
                    "label": "Issue Details",
                    "question": "Please provide more details about your issue",
                    "response": ""
                },
                {
                    "label": "Service Recommendation",
                    "question": "Will you recommend this service provider to a friend or relative",
                    "response": "yes"
                },
                {
                    "label": "Satisfaction level",
                    "question": "Please indicate your level of satisfaction",
                    "response": "Satisfied"
                }
                ]
            },
            "replies": [
            {
                "type": "IN",
                "date": "2015/05/22 09:22:10",
                "content": "DM sent plz check",
                "category": "Customer Care",
                "subcategory": "Customer Care",
                "score": "Neutral"
            },
            {
                "type": "OUT",
                "from": "@touchLebanon",
                "date": "2015/05/22 12:22:10",
                "content": "@hsabry_  we tried to contact you but the number provided is incorrect; kindly, can you double check and send us DM"
            }
            ]
        }
        ],
        "comments": [
        {
            "author": "Operator1",
            "date": "2015/05/21 10:33:10",
            "content": "Auto-message correctly sent"
        },
        {
            "author": "Operator2",
            "date": "2015/05/23 10:30:10",
            "content": "User contacted after 2 days without response"
        }
        ]
    }
},
   {
    "id": 2,
    "user": "@الوهيدي™☆الكوماندوز☆",
    "status": "solved issue",
    "auto_message_date": "2015/05/22 8:33:10",
    "auto_message_name_rules": "Issue Customer Care",
    "questionnaire_name": "Customer Care/Customer Care",
    "date_last_tweet_sent": "2015/05/25 9:44:01",
    "date_last_tweet_received": "2015/05/26 11:18:52",
    "original_category": "Customer Care",
    "original_subcategory": "Customer Care",
    "original_score": "Neutral",
    "last_tweet_received_score": "Neutral",
    "details": {
        "messages": [],
        "comments": [
        {
            "author": "Operator11",
            "date": "2015/05/21 10:33:10",
            "content": "Auto-message correctly sent"
        }
        ]
    }
},
   {
    "id": 3,
    "user": "@MuradMohamed",
    "status": "contacted",
    "auto_message_date": "2015/05/20 8:00:10",
    "auto_message_name_rules": "Issue Network Coverage",
    "date_last_tweet_sent": "2015/05/21 9:44:01",
    "date_last_tweet_received": "2015/05/21 11:18:52",
    "original_category": "Network",
    "original_subcategory": "Coverage",
    "original_score": "Negative",
    "last_tweet_received_score": "Neutral",
    "details": {
        "messages": [],
        "comments": [
        {
            "author": "Operator11",
            "date": "2015/05/21 10:33:10",
            "content": "Auto-message correctly sent"
        }
        ]
    }
}
];

var contact_board_test_data_acquisition = [
   {
    "id": 1,
    "user": "@ezzatyoussef",
    "competitor": "alfa",
    "status": "interested",
    "auto_message_date": "2015/05/21 12:33:10",
    "auto_message_name_rules": "Acquisition Network Voice",
    "date_last_tweet_sent": "2015/05/28 11:30:10",
    "date_last_tweet_received": "2015/05/24 09:22:10",
    "original_category": "Network",
    "original_subcategory": "Voice",
    "original_score": "Potential Churner",
    "last_tweet_received_score": "Potential Churner",
    "details": {
        "messages": [
        {
            "type": "IN",
            "from": "@ezzatyoussef",
            "date": "2015/05/21 10:55:44",
            "content": "@AlfaTelecom I'm not happy with your service and thinking of moving to touch",
            "category": "Network",
            "subcategory": "Voice",
            "score": "Negative",
            "replies": [
            {
                "type": "AUTO-MESSAGE",
                "from": "@touchLebanon",
                "date": "2015/05/21 11:30:10",
                "content": "Hi Ezzat, we would like to inform you that you can follow us to learn about our new products and services."
            },
            {
                "type": "IN",
                "date": "2015/05/24 09:22:10",
                "content": "Thank you for your message @touchLebanon, I'm interested; I will send you my number via DM",
                "category": "Network",
                "subcategory": "Voice",
                "score": "Positive"
            }
            ]
        }
        ],
        "comments": [
        {
            "author": "Operator1",
            "date": "2015/05/21 13:22:00",
            "content": "Auto-message correctly sent"
        },
        {
            "author": "Operator2",
            "date": "2015/06/01 10:30:10",
            "content": "User needs to be contacted, DM has never been received"
        }
        ]
    }
},
   {
    "id": 2,
    "user": "@الوهيدي™☆الكوماندوز☆",
    "competitor": "alfa",
    "status": "contacted",
    "auto_message_date": "2015/05/21 12:34:05",
    "auto_message_name_rules": "Acquisition Network Voice",
    "date_last_tweet_sent": "2015/05/21 12:34:10",
    "date_last_tweet_received": "2015/05/21 12:40:10",
    "original_category": "Network",
    "original_subcategory": "Voice",
    "original_score": "Potential Churner",
    "last_tweet_received_score": "Potential Churner",
    "details": {
        "messages": [],
        "comments": [
        {
            "author": "Operator1",
            "date": "2015/05/21 13:23:00",
            "content": "Auto-message correctly sent"
        }
        ]
    }
},
    {
    "id": 3,
    "user": "@YahyaAlNajjar",
    "competitor": "alfa",
    "status": "offer sent",
    "auto_message_date": "2015/05/20 10:20:05",
    "auto_message_name_rules": "Acquisition Network Data",
    "date_last_tweet_sent": "2015/05/21 12:37:10",
    "date_last_tweet_received": "2015/05/21 12:48:10",
    "original_category": "Network",
    "original_subcategory": "Data",
    "original_score": "Potential Churner",
    "last_tweet_received_score": "Potential Churner",
    "details": {
        "messages": [],
        "comments": [
        {
            "author": "Operator1",
            "date": "2015/05/21 14:22:00",
            "content": "Auto-message correctly sent"
        }
        ]
    }
},
    {
    "id": 4,
    "user": "@طنش ..تعش..تنتعش",
    "competitor": "alfa",
    "status": "interested",
    "auto_message_date": "2015/05/21 12:36:18",
    "auto_message_name_rules": "Acquisition Network Voice",
    "date_last_tweet_sent": "2015/05/23 13:01:00",
    "date_last_tweet_received": "2015/05/28 08:42:10",
    "original_category": "Network",
    "original_subcategory": "Voice",
    "original_score": "Potential Churner",
    "last_tweet_received_score": "Potential Churner",
    "details": {
        "messages": [],
        "comments": [
        {
            "author": "Operator3",
            "date": "2015/05/21 15:15:00",
            "content": "Auto-message correctly sent"
        }
        ]
    }
},
    {
    "id": 5,
    "user": "@╰☆╮ッمش محاسب ╰☆╮",
    "competitor": "alfa",
    "status": "contacted",
    "auto_message_date": "2015/05/21 12:38:02",
    "auto_message_name_rules": "Acquisition Network Voice",
    "date_last_tweet_sent": "2015/05/21 14:30:00",
    "date_last_tweet_received": "2015/05/21 14:32:10",
    "original_category": "Network",
    "original_subcategory": "Voice",
    "original_score": "Potential Churner",
    "last_tweet_received_score": "Potential Churner",
    "details": {
        "messages": [],
        "comments": [
        {
            "author": "Operator3",
            "date": "2015/05/21 15:40:00",
            "content": "Auto-message correctly sent"
        }
        ]
    }
},
   {
    "id": 6,
    "user": "@سيمبا◔◡◔",
    "competitor": "alfa",
    "status": "offer sent",
    "auto_message_date": "2015/05/21 12:38:05",
    "auto_message_name_rules": "Acquisition Network Data",
    "date_last_tweet_sent": "2015/05/21 12:50:00",
    "date_last_tweet_received": "2015/05/21 12:48:10",
    "original_category": "Network",
    "original_subcategory": "Data",
    "original_score": "Potential Churner",
    "last_tweet_received_score": "Potential Churner",
    "details": {
        "messages": [],
        "comments": [
        {
            "author": "Operator3",
            "date": "2015/05/21 16:23:10",
            "content": "Auto-message correctly sent"
        }
        ]
    }
}
];

var test_categories = [
    { parentID: 'Network' },
    { parentID: 'Customer Care' },
    { parentID: 'Business Affairs' }
];

var test_subcategories = [
    { parentID: 'Network', value: 'Voice' },
    { parentID: 'Network', value: 'Data' },
    { parentID: 'Network', value: 'Coverage' },
    { parentID: 'Network', value: 'Messaging' },
    { parentID: 'Network', value: 'Roaming' },
    { parentID: 'Network', value: 'Other Network' },
    { parentID: 'Customer Care', value: 'Customer Care' },
    { parentID: 'Business Affairs', value: 'Shops' },
    { parentID: 'Business Affairs', value: 'Handset' },
    { parentID: 'Business Affairs', value: 'Pricing or Billing' },
    { parentID: 'Business Affairs', value: 'Products and Plans' },
    { parentID: 'Business Affairs', value: 'Contracts' },
    { parentID: 'Business Affairs', value: 'Other Business Affairs' }
];

var test_sentiments = [
    { value: 'Positive' },
    { value: 'Neutral' },
    { value: 'Negative' },
    { value: 'Severe' },
    { value: 'PotentialChurner' }
];

var test_questionaries = [
    { value: 1, name: "User Device Questionarie" },
    { value: 2, name: "User Coverage Area Questionarie" },
    { value: 3, name: "User Shops Attendance Questionarie" },
    { value: 4, name: "User Satisfaction Questionarie" },
    { value: 5, name: "User Acquisition Questionarie" }
];

var test_competitors = [
    { parentID: 'Acquisition', value: 'EtisalatMisr' },
    { parentID: 'Acquisition', value: 'VodafoneEgypt' }
];

// ------------------------------------------------------------------------
// ------------------------------------------------------------------------
// ------------------- END CONTACT BOARD TEST DATA ------------------------
// ------------------------------------------------------------------------
// ------------------------------------------------------------------------
