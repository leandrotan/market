<?php
/*
This is a very simple one page demo showing the use of the Nairaland api.
The page knows what to do next by the action value that is sent via the GET method
The link to work on is also passed through the GET method
Other neccesary information are also passed as GET request to make everything to work in one file.
Look more closely at the while loop with the Read class fetch method in its conditional part and also watch the Elements keys being used.
All major methods are used.
*/
ini_set('MAX_EXECUTION_TIME', -1);
//reports all errors
error_reporting(E_ALL);
//the time the script starts
$timeStart = time();
session_start();
//include the api
include("../api/Nairaland.php");
//checks if a username is given, if yes store it in the session
$index = 0;
if(isset($_GET['username'])) {
$_SESSION['username'] = $_GET['username'];
}
//checks if a password is given, if yes store it in the session
if(isset($_GET['password'])) {
$_SESSION['password'] = $_GET['password'];
}
//if a session exist store it else store the default value 'grandtheft'
$username = isset($_SESSION['username']) ? $_SESSION['username'] : 'grandtheft';
//if a session exist store it else store the default 'test'
$password = isset($_SESSION['password']) ? $_SESSION['password'] : 'test';
//Instantiate the Nairaland class
$nl = new Nairaland($username);
//This is a simple demo so only login is called
//but it is better to also use setCookie
$result = $nl->login($password);
//checks for error
if($result === FALSE) {
die($nl->getErrorMsg());
}

//We are going to be using only one file as the demo
//so we know what to do next by a GET variable 'action'

//view a thread posts
//gets the thread content
$url = "http://www.nairaland.com/search/".KEYWORD;
$data = $nl->get($url);

$myFile = "test.txt";
$fh = fopen($myFile, 'a') or die("can't open file");

do{
    
$data = $nl->get($url.'/0/'.BOARD.'/0/'.$index);   

if($data == false)
    break;
$Read = $nl->readSearch($data);
//iterate through each Element
while(($row = $Read->fetch())) {
$postDate = $row['date'];
    preg_match_all('!\d+!', $row['likes'], $match);
    preg_match_all('!\d+!', $row['shares'], $matches);

    /* Trying to fix the date field
if((strpos($postDate, ','))!==false)
//Replace On and , with nothing
{
    $postdate = str_replace(array(" On",","),array("",""), $postdate);
    echo $postdate;
}
else if (strpos($postDate, " On "))
{   //Replace On with nothing  and add year
 $postdate .= str_replace(" On","", $postdate)." ".date(Y);
echo $postdate;
 }
else 
{// Add day, month and year
     $postdate .= $postdate." ".date(m)." ".date(d)." ".date(Y);
echo $postdate;
     }*/
 //$d=strtotime($postdate );

//echo '<a>'.$row['poster'].'</a>|'.$row['date'].'|'.$row['id'].'|'.$row['topic'].'|'.$row['board'].'|'.' <div> '.$row['post'].'</div>'.'|'.$match[0][0].'|'.$matches[0][0].'<br>';
//$stringData= str_replace("\"", "", $row['poster'].','.$row['date'].','.$row['id'].','.$row['topic'].','.$row['board'].','.str_replace("\"","",$row['post']).','.$match[0][0].','.$matches[0][0]);

$stringData=  "\"".$row['poster']."\"".','."\"".$row['date']."\"".','."\"".$row['id']."\"".','."\"".str_replace("\"", " ",$row['topic'])."\"".','."\"".$row['board']."\"".','."\"".str_replace("\"", " ",$row['post'])."\"".','."\"".$match[0][0]."\"".','."\"".$matches[0][0]."\"";
$stringData= str_replace("\n", "", $stringData);
$stringData = $stringData."\n";
fwrite($fh, $stringData);

}

}
while (($index = $nl->hasNextPage($data,$index)));
fclose($fh);

//the time the script stop executing
$timeEnd = time();
$time = ($timeEnd - $timeStart) / 1000;
//echoes the time taken to run the code
echo $time;
?>