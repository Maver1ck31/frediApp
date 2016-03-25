<?php

require_once '../class/Connection.class.php';
require_once '../class/MySQLDao.class.php';

$returnValue  = array();

$dao = new MySQLDao();
$dao->openConnection();
$listLigues = $dao->getAllLigue();

if (empty($listLigues)) {
    $retunValue['status'] = "error";
    $retunValue['message'] = "No data to fetch";
    echo json_encode($retunValue);
    return;
} else {
    $retunValue['status'] = "success";
    $retunValue['message'] = $listLigues;
    echo json_encode($retunValue);
    return;
}