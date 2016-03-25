<?php

require_once '../class/Connection.class.php';
require_once '../class/MySQLDao.class.php';

$idDemandeur = htmlentities($_POST['idDemandeur']);

$returnValue = array();

$dao = new MySQLDao();
$dao->openConnection();

$listNdf = $dao->getListNdfByIdDemandeur($idDemandeur);

if (empty($userDetails)) {
    $retunValue['status'] = "error";
    $retunValue['message'] = "No ndf to display";
    $retunValue['infos'] = $userDetails;
    echo json_encode($retunValue);
} else {
    $retunValue['status'] = "success";
    $retunValue['message'] = "Data downloaded";
    $retunValue['infos'] = $userDetails;
    echo json_encode($retunValue);
}
