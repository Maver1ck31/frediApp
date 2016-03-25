<?php
	require_once '../class/Connection.class.php';
	require_once '../class/MySQLDao.class.php';

	$nom = $_POST['Nom'];
	$prenom = $_POST['Prenom'];
	$rue = $_POST['Rue'];
	$cp = $_POST['CP'];
	$ville = $_POST['Ville'];
	$mail = $_POST['AdresseMail'];
	$mail2 = $_POST['AdresseMail2'];
	$mdp = $_POST['motDePasse'];
	$mdp2 = $_POST['motDePasse2'];
	$returnValue = array();
	
	$nom = "n"; 
	$prenom = "p";
	$rue = "r";
	$cp = 31000;
	$ville = "t";
	$mail = "mkk";
	$mail2 = "mkk";
	$mdp = "p";
	$mdp2 = "p";
	
	if(empty($nom) || empty($prenom) || empty($rue) || empty($cp) || empty($ville) || empty($mail) || empty($mail2) || empty($mdp) || empty($mdp2))
	{
		$returnValue['status'] = "error";
		$returnValue['message'] = "All fields are required";
		echo json_encode($returnValue);
	}
	else
	{
		if(($mdp != $mdp2) || ($mail != $mail2))
		{
			$returnValue['status'] = "error";
			$returnValue['message'] = "Pass or mail error";
			echo json_encode($returnValue);
			return;
		}
		else 
		{
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
		}
	}
?>

