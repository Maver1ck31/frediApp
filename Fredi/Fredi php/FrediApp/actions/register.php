<?php
	require_once '../class/Connection.class.php';
	require_once '../class/MySQLDao.class.php';

	$nom = htmlentities($_POST['Nom']);
	$prenom = htmlentities($_POST['Prenom']);
	$rue = htmlentities($_POST['Rue']);
	$cp = htmlentities($_POST['CP']);
	$ville = htmlentities($_POST['Ville']);
	$mail = htmlentities($_POST['AdresseMail']);
	$mail2 = htmlentities($_POST['AdresseMail2']);
	$mdp = htmlentities($_POST['motDePasse']);
	$mdp2 = htmlentities($_POST['motDePasse2']);

	$returnValue = array();
	
	if (empty($nom) || empty($prenom) || empty($rue) || empty($cp) || empty($ville) || empty($mail) || empty($mdp)) || empty($mdp2)) || empty($mail2)) {
		$returnValue['status'] = "error";
		$returnValue['message'] = "All fields are required";
		echo json_encode($returnValue);
		return;
	}

	if(($mdp != $mdp2) || ($mail != $mail2))
	{
		$returnValue['status'] = "error";
		$returnValue['message'] = "Pass or mail error";
		echo json_encode($returnValue);
		return;
	}
	
	$dao = new MySQLDao();
	$dao->openConnection();
	if ($dao->userAlreadyExist($mail)) {
		$returnValue['status'] = "error";
		$returnValue['message'] = "User already exists";
		echo json_encode($returnValue);
		return;
	}

	$secureMdp = md5($mdp);

	$result = $dao->insertDemandeur($nom, $prenom, $rue, $cp, $ville, $mail, $secureMdp);

	if ($result) {
		$returnValue['status'] = "success";
		$returnValue['message'] = "Registration successful";
		echo json_encode($returnValue);
		return;
	}
	else
	{
		$returnValue['status'] = "error";
		$returnValue['message'] = "Unnknow error";
		echo json_encode($returnValue);
		return;
	}
?>

