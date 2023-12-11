<?php
try {
$conn = new
    PDO('mysql:host=localhost;dbname=saemysql12;charset=UTF8' ,"saemysql12", "Lb4c45R9dif5SX", [ PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION ]);
}
catch (PDOException $e) {
    echo "Erreur: ".$e->getMessage()."<br>" ;
    die() ;
}

?>

