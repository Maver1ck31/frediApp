<?php

class MySQLDao {

    var $con = null;
    var $dbhost = null;
    var $dbname = null;
    var $dbuser = null;
    var $userPwd = null;

    function __construct() {
        $this->dbhost = Connection::$dbhost;
        $this->dbname = Connection::$dbname;
        $this->dbuser = Connection::$dbuser;
        $this->userPwd = Connection::$userPwd;
    }

    public function openConnection() {
        $dsn = "mysql:host=$this->dbhost;dbname=$this->dbname";
        $this->con = new PDO($dsn, $this->dbuser, $this->userPwd, array(PDO::MYSQL_ATTR_INIT_COMMAND => 'SET NAMES utf8'));
        $this->con->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    }

    public function getConnection() {
        return $this->con;
    }

	public function userAlreadyExist($mail)
	{
		$sql = "SELECT * FROM demandeur WHERE AdresseMail = '$mail'";
		 try {
            $res = $this->con->query($sql);
            $row = $res->fetch(PDO::FETCH_ASSOC);
        } catch (PDOException $exc) {
            echo $exc->getTraceAsString();
        }

        if (empty($row))
            return false;
		else
			true;
	}
	
    public function getUserDetailsWithPasssword($mail, $passwd = NULL) {
        $returnValue = array();

        if ($passwd == NULL) {
            $sql = "SELECT * FROM demandeur "
                    . "WHERE AdresseMail = '$mail' ";
        } else {
            $sql = "SELECT * FROM demandeur "
                    . "WHERE AdresseMail = '$mail' "
                    . "AND motDePasse = '$passwd'";
        }

        try {
            $res = $this->con->query($sql);
            $row = $res->fetch(PDO::FETCH_ASSOC);
        } catch (PDOException $exc) {
            echo $exc->getTraceAsString();
        }

        if (!empty($row)) {
            $returnValue = $row;
        }

        return $returnValue;
    }

    public function insertDemandeur($nom, $prenom, $rue, $cp, $ville, $mail, $mdp) {
        $returnValue = array();

        $sql = "INSERT INTO demandeur(Nom, Prenom, Rue, CP, Ville, AdresseMail, motDePasse) VALUES"
                . "('$nom', '$prenom', '$rue', $cp, '$ville', '$mail', '$mdp')";

        try {
            $returnValue = $this->con->exec($sql);
        } catch (PDOException $exc) {
            echo $exc->getMessage();
        }

        return $returnValue;
    }

    public function insertNdf($date, $trajet, $km, $coutPeage, $coutRepas, $coutHebergement, $idDemandeur, $idMotif, $annee) {

        $returnValue = array();
        $sql = "INSERT INTO lignefrais(date, trajet, km, coutPeage, coutRepas, "
                . "coutHebergement, idDemandeur, idMotif, Annee) VALUES "
                . "('$date', '$trajet', $km, $coutPeage, $coutRepas, $coutHebergement, "
                . "$idDemandeur, $idMotif, $annee";

        try {
            $returnValue = $this->con->exec($sql);
        } catch (PDOException $exc) {
            echo $exc->getMessage();
        }
        
        return $returnValue;
    }

    public function getAllLigue() {
        $returnValue = array();

        $sql = "SELECT * FROM ligue";

        try {
            $res = $this->con->query($sql);
            $rows = $res->fetchAll(PDO::FETCH_ASSOC);
        } catch (PDOException $exc) {
            echo $exc->getMessage();
        }

        if (!empty($rows)) {
            $returnValue = $rows;
        }

        return $returnValue;
    }

}
?>

