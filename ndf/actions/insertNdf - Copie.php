<?php

require_once '../class/Connection.class.php';
require_once '../class/MySQLDao.class.php';

$date = htmlentities($_POST['date']);
$trajet = htmlentities($_POST['trajet']);
$km = htmlentities($_POST['km']);
$coutPeage = htmlentities($_POST['coutPeage']);
$coutRepas = htmlentities($_POST['coutRepas']);
$coutHebergement = htmlentities($_POST['coutHebergement']);
$idDemandeur = htmlentities($_POST['idDemandeur']);
$idMotif = htmlentities($_POST['idMotif']);
$annee = htmlentities($_POST['annee']);

$date = "12/12/2016";
$trajet = 12;
$km = 12;
$coutPeage = 5;
$coutRepas = 5;
$coutHebergement = 5;
$idDemandeur = 2;
$idMotif = 1;
$annee = "2016";

$returnValue = array();

if (empty($date) || empty($trajet) || empty($km) || empty($coutPeage) || 
    empty($coutRepas) || empty($coutHebergement) ||empty($idDemandeur) || 
        empty($idMotif) || empty($annee)) {
    $returnValue['status'] = "error";
    $returnValue['message'] = "All fields are required";
    echo json_encode($returnValue);
    return;
}

$dao = new MySQLDao();
$dao->openConnection();

$result = $dao->insertNdf($date, $trajet, $km, $coutPeage, $coutRepas, 
        $coutHebergement, $idDemandeur, $idMotif, $annee);

if ($result) {
        $returnValue['status'] = "success";
        $returnValue['message'] = "Ndf inserted";
        echo json_encode($returnValue);
        return;
    }
	else
	{
		$returnValue['status'] = "error";
        $returnValue['message'] = "Unknow error";
        echo json_encode($returnValue);
        return;
	}

