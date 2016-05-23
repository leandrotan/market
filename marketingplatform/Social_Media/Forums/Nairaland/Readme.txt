This is the first working Api for Nairaland. It is an Unofficial Api. It has most of the features of the main site and is written in OOP PHP.

The Api consists of 2 Classes: Nairaland.php and Read.php under Nairaland/api
A config file is available under the same directory

The Testing class is located under Nairaland/docs, named: demoDB.php

You can test this by placing the 2 folders under the wwwroot, and access it from your web browser using:
http://localhost/Nairaland/docs/demoDB.php?

Make sure enable windows features: CGI and ASP.NET under: Internet Information Services\World Wide Web Services\Application Development Features

DemoDB.php, will fetch all the posts from the Nairalan forum, that are related to the keywords and board specified in the config file
and will populate table test_nairalandforum under mariaDB