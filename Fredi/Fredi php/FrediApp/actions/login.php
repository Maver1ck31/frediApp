<?php

require_once '../class/Connection.class.php';
require_once '../class/MySQLDao.class.php';

$mail = htmlentities($_POST['AdresseMail']);
$passwd = htmlentities($_POST['motDePasse']);

$retunValue = array();

if (empty($mail) || empty($passwd)) {
    $retunValue['status'] = "error";
    $retunValue['message'] = "All fields are required";
    echo json_encode($retunValue);
    return;
}

$securedPasswd = md5($passwd);

$dao = new MySQLDao();
$dao->openConnection();
$userDetails = $dao->getUserDetailsWithPasssword($mail, $securedPasswd);

if (empty($userDetails)) {
    $retunValue['status'] = "error";
    $retunValue['message'] = "Login failed, check your credentials";
    $retunValue['infos'] = $userDetails;
    echo json_encode($retunValue);
} else {
    $retunValue['status'] = "success";
    $retunValue['message'] = "Login successful";
    $retunValue['infos'] = $userDetails;
    echo json_encode($retunValue);
}

