CREATE TABLE CarteBancaire (
    numCarte DECIMAL,
    dateExpiration DATE,
    cvv DECIMAL (3),
    titulaire VARCHAR (30),
    CONSTRAINT pk_CarteBancaire PRIMARY KEY (numCarte)
);

CREATE TABLE TypePaiement(
    idType DECIMAL (5),
    libelleType VARCHAR (80),
    CONSTRAINT pk_TypePaiement PRIMARY KEY (idType)
);

CREATE TABLE Paypal(
    email VARCHAR (80),
    motDePasse VARCHAR (50),
    CONSTRAINT pk_Paypal PRIMARY KEY (email)
);

CREATE TABLE Paiement(
    idPaiement DECIMAL (15),
    montantTotal DECIMAL (10),
    status VARCHAR (30),
    CONSTRAINT pk_Paiement PRIMARY KEY (idPaiement),
    CONSTRAINT ck_Paiement_montantTotal CHECK (montantTotal > 0), 
    CONSTRAINT ck_Paiement_status CHECK (status IN ('accepté', 'annulé', 'échoué'))
);

CREATE TABLE Commande(
    numCommande DECIMAL (15),
    dateCommande DATE,
    montantFrais DECIMAL (15), 
    montant DECIMAL (15),
    codeEtiquette DECIMAL (15),
    numClient DECIMAL (15),
    CONSTRAINT pk_Commande PRIMARY KEY (numCommande),
    CONSTRAINT fk_Commande_codeEtiquette FOREIGN KEY (codeEtiquette) REFERENCES Etiquette (codeEtiquette),
    CONSTRAINT fk_Commande_numClient FOREIGN KEY (numClient) REFERENCES Client (numClient),
    CONSTRAINT ck_Commande_montantFrais CHECK (montantFrais > 0),
    CONSTRAINT ck_Commande_montant CHECK (montant > 0)
);

CREATE TABLE Client(
    numClient DECIMAL (15),
    nomClient VARCHAR (50),
    prenomClient VARCHAR (50),
    adrRueClient VARCHAR (50),
    adrCodePostalClient VARCHAR (5),
    adrVilleClient VARCHAR (50),
    adrPaysClient VARCHAR (50),
    telephoneClient VARCHAR (10),
    mailClient VARCHAR2 (80),
    codeEtiquette DECIMAL (15),
    codeListe VARCHAR (5),
    mdpClient VARCHAR (50) NOT NULL,
    CA_cumule DECIMAL (15),
    CONSTRAINT pk_Client PRIMARY KEY (numClient),
    CONSTRAINT fk_Client_codeEtiquette FOREIGN KEY (codeEtiquette) REFERENCES Etiquette (codeEtiquette),
    CONSTRAINT fk_Client_codeListe FOREIGN KEY (codeListe) REFERENCES ListePrix (codeListe)
);

CREATE TABLE Etiquette(
    codeEtiquette DECIMAL (15),
    libelleEtiquette VARCHAR2(80),
    codeTypeTVA CHAR(3),
    CONSTRAINT pk_Etiquette PRIMARY KEY (codeEtiquette)
);

CREATE TABLE Produit(
    numProduit DECIMAL (15),
    coutAchat DECIMAL (12),
    prixVente  DECIMAL (12),
    codeBarre CHAR (10),
    referenceInterne VARCHAR2 (10),
    nomProduit VARCHAR2 (50),
    seuilReapprovisionnement DECIMAL (10),
    stockMax DECIMAL (10),
    stock DECIMAL (10),
    fraisSupplementaires DECIMAL (10),
    numCategorie DECIMAL (35),
    codeType CHAR (3) NOT NULL,
    CONSTRAINT pk_Produit PRIMARY KEY (numProduit),
    CONSTRAINT fk_Produit_codeType FOREIGN KEY (codeType) REFERENCES TypeProduit (codeType),
    CONSTRAINT fk_Produit_numCategorie FOREIGN KEY (numCategorie) REFERENCES Categorie (numCategorie)
);

CREATE TABLE Categorie(
    numCategorie DECIMAL (5),
    libelleCategorie VARCHAR (50),
    CONSTRAINT pk_Categorie PRIMARY KEY (numCategorie)
);

CREATE TABLE TypeProduit (
    codeType CHAR (3),
    libelleType VARCHAR2 (40),
    CONSTRAINT pk_TypeProduit PRIMARY KEY (codeType)
);

CREATE TABLE Description(
    idDescription DECIMAL (15),
    prixAchat VARCHAR (22),
    delaiLivraison DECIMAL (5),
    CONSTRAINT pk_Description PRIMARY KEY (idDescription),
    CONSTRAINT ck_Description_prixAchat CHECK (prixAchat > 0)
);

CREATE TABLE ListePrix(
    codeListe VARCHAR (5),
    libelleListe VARCHAR (80),
    CONSTRAINT pk_ListePrix PRIMARY KEY (codeListe)
);

CREATE TABLE AdresseLivraison(
    idLivraison DECIMAL (15),
    adrPostaleLivraison VARCHAR (5),
    adrVilleLivraison VARCHAR (50),
    adrPaysLivraison VARCHAR (50),
    adrLivraison VARCHAR (50),
    CONSTRAINT pk_AdresseLivraison PRIMARY KEY (idLivraison)
);

CREATE TABLE Livraison(
    numClient DECIMAL (15),
    idLivraison DECIMAL (15),
    libelleLivraison VARCHAR (50),
    CONSTRAINT fk_Livraison_numClient FOREIGN KEY (numClient) REFERENCES Client (numClient),
    CONSTRAINT fk_Livraison_idLivraison FOREIGN KEY (idLivraison) REFERENCES AdresseLivraison (idLivraison),
    CONSTRAINT pk_Livraison PRIMARY KEY (numClient, idLivraison)
);

CREATE TABLE LigneCde(
    numProduit DECIMAL (15),
    numCommande DECIMAL (15),
    quantiteCommandee DECIMAL (22),
    quantiteLivree DECIMAL (22),
    CONSTRAINT fk_LigneCde_numProduit FOREIGN KEY (numProduit) REFERENCES Produit (numProduit),
    CONSTRAINT fk_LigneCde_numCommande FOREIGN KEY (numCommande) REFERENCES Commande (numCommande),
    CONSTRAINT pk_LigneCde PRIMARY KEY (numProduit, numCommande),
    CONSTRAINT ck_LigneCde_quantiteCommandee CHECK (quantiteCommandee > 0)
);

-- On crée une séquence afin de générer la clé primaire des clients
DROP SEQUENCE seq_id_client;
CREATE SEQUENCE seq_id_client
  MINVALUE 1  MAXVALUE 999999999999  
  START WITH 1 INCREMENT BY 1;

-- On crée une séquence afin de générer la clé primaire des commandes
DROP SEQUENCE seq_id_commande;
CREATE SEQUENCE seq_id_commande
  MINVALUE 1  MAXVALUE 999999999999  
  START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE PACKAGE GestionCommandes AS 
    PROCEDURE CreerCommande(p_numClient Client.numClient%TYPE, p_montant Commande.montant%TYPE);
    FUNCTION CalculerMontantTotal(p_numCommande Commande.numCommande%TYPE);
    PROCEDURE SupprimerCommande(p_numClient Commande.numClient%TYPE, p_numCommande Commande.numCommande%TYPE);
END GestionCommandes;

CREATE OR REPLACE PACKAGE BODY GestionCommandes AS
    PROCEDURE CreerCommande(p_numClient Client.numClient%TYPE, p_montant Commande.montant%TYPE) AS
    BEGIN 
        INSERT INTO Commande (numCommande, numClient, montant);
        VALUES (seq_id_commande.NEXTVAL, p_numClient, p_montant);
    END;

    FUNCTION CalculerMontantTotal(p_numCommande Commande.numCommande%TYPE) RETURN DECIMAL AS 
    v_montant DECIMAL;
    BEGIN
        SELECT montant INTO v_montant 
        FROM Commande
        WHERE numCommande = p_numCommande;
        RETURN v_montant;
    END;
END GestionCommandes;

CREATE OR REPLACE PROCEDURE CalculerMontantTotalCmd (p_numCommande Commande.numCommande%TYPE) AS 
    v_montantTotal DECIMAL;
BEGIN 
    SELECT SUM (quantiteCommandee * prixVente)
    INTO v_montant
    FROM LigneCde
    JOIN Produit ON LigneCde*.numProduit = Produit.numProduit
    WHERE numCommande = p_numCommande

    UPDATE Commande 
    SET montant = v_montantTotal
    WHERE numCommande = p_numCommande
END;

CREATE OR REPLACE TRIGGER Maj_Depense_Client
BEFORE INSERT ON Commande 
FOR EACH ROW 
BEGIN 
    UPDATE Client
    SET CA_cumule = IFNULL(CA_cumule, 0) + :NEW.montant
    WHERE numClient = :NEW.numClient;
END;



CREATE OR REPLACE TRIGGER MajCoutTotalCmd
BEFORE INSERT ON LigneCde
FOR EACH ROW
BEGIN 
    UPDATE Commande 
    SET montant = montant + (:NEW.quantiteCommandee *(SELECT coutAchat
                                                      FROM Produit 
                                                      WHERE numProduit = :NEW.numProduit))
    WHERE numCommande = :NEW.numCommande;
END;

CREATE OR REPLACE TRIGGER GererFraisLivraison
BEFORE INSERT ON Commande 
FOR EACH ROW
BEGIN
    --IF :NEW.montant > 1000 THEN 
       -- UPDATE Commande
       -- SET montantFrais = 0.05 * :NEW.montant
      --  WHERE numCommande = :NEW.numCommande
   -- ELSE
      --  UPDATE Commande
    --    SET montantFrais = 0.1 * :NEW.montant
    --    WHERE numCommande = :NEW.numCommande
    --END IF;
    UPDATE Commande 
    SET montantFrais = :NEW.montant * Produit.fraisSupplementaires
    WHERE numCommande = :NEW.numCommande
END;

CREATE OR REPLACE TRIGGER MajStock
AFTER INSERT ON Livraison
FOR EACH ROW
BEGIN
    UPDATE Produit
    SET stock = stock - (SELECT quantiteLivree
                         FROM LigneCde
                         WHERE numProduit = :NEW.numProduit
                         AND numCommande = :NEW.numCommande)
    WHERE numProduit = :NEW.numProduit
END;
