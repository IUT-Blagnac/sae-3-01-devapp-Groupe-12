DROP TABLE IF EXISTS LigneCde;
DROP TABLE IF EXISTS Livraison;
DROP TABLE IF EXISTS AdresseLivraison;
DROP TABLE IF EXISTS TypePaiement;
DROP TABLE IF EXISTS Paiement;
DROP TABLE IF EXISTS Commande;
DROP TABLE IF EXISTS ListeSouhait;
DROP TABLE IF EXISTS Client;
DROP TABLE IF EXISTS ListePrix;
DROP TABLE IF EXISTS Description;
DROP TABLE IF EXISTS Produit;
DROP TABLE IF EXISTS Categorie;
DROP TABLE IF EXISTS TypeProduit;
DROP TABLE IF EXISTS Paypal;
DROP TABLE IF EXISTS CarteBancaire;
DROP TABLE IF EXISTS RegroupementProduit
DROP TABLE IF EXISTS ProduitRegroupe
DROP TABLE IF EXISTS ProduitApparente
DROP TABLE IF EXISTS ProduitCompose
DROP TABLE IF EXISTS Avis
DROP TABLE IF EXISTS ReponseAvis


CREATE TABLE CarteBancaire (
    numCarte DECIMAL,
    dateExpiration DATE,
    cvv DECIMAL (3),
    titulaire VARCHAR (30),
    CONSTRAINT pk_CarteBancaire PRIMARY KEY (numCarte)
);

CREATE TABLE Paypal(
    email VARCHAR (80),
    motDePasse VARCHAR (50),
    CONSTRAINT pk_Paypal PRIMARY KEY (email)
);

CREATE TABLE Client(
    numClient INT (15) AUTO_INCREMENT,
    nomClient VARCHAR (50),
    prenomClient VARCHAR (50),
    adrRueClient VARCHAR (50),
    adrCodePostalClient VARCHAR (6),
    adrVilleClient VARCHAR (50),
    adrPaysClient VARCHAR (50),
    telephoneClient VARCHAR (20),
    mailClient VARCHAR (80) UNIQUE,
    mdpClient VARCHAR (255) NOT NULL,
    CA_cumule DECIMAL (15),
    pseudoClient VARCHAR (50),
    tokenClient VARCHAR (50),
    CONSTRAINT pk_Client PRIMARY KEY (numClient)
);

CREATE TABLE ListeSouhait(
    numClient INT (15),
    libelleListeSouhait VARCHAR (50),
    listeProduit VARCHAR (40),
    CONSTRAINT fk_ListeSouhait_numClient FOREIGN KEY (numClient) REFERENCES Client (numClient),
    CONSTRAINT pk_ListeSouhait_numClient PRIMARY KEY (numClient)
);

CREATE TABLE ListePrix(
    codeListe VARCHAR (5),
    libelleListe VARCHAR (80),
    CONSTRAINT pk_ListePrix PRIMARY KEY (codeListe)
);

CREATE TABLE Description(
    idDescription DECIMAL (15),
    prixAchat DECIMAL (15) CHECK (prixAchat > 0),
    delaiLivraison DECIMAL (5),
    CONSTRAINT pk_Description PRIMARY KEY (idDescription)
);

CREATE TABLE Commande(
    numCommande DECIMAL (15),
    dateCommande DATE,
    montantFrais DECIMAL (15) CHECK (montantFrais > 0), 
    montant DECIMAL (15) CHECK (montant > 0),
    numClient INT (15),
    idDescription DECIMAL (15),
    CONSTRAINT pk_Commande PRIMARY KEY (numCommande),
    CONSTRAINT fk_Commande_numClient FOREIGN KEY (numClient) REFERENCES Client (numClient),
    CONSTRAINT fk_Commande_idDescription FOREIGN KEY (idDescription) REFERENCES Description (idDescription)
);

CREATE TABLE Paiement(
    idPaiement DECIMAL (15),
    montantTotal DECIMAL (10) CHECK (montantTotal > 0),
    status VARCHAR (30) CHECK (status IN ('accepté', 'annulé', 'échoué')),
    numCommande DECIMAL (15),
    CONSTRAINT pk_Paiement PRIMARY KEY (idPaiement),
    CONSTRAINT fk_Paiement_numCommande FOREIGN KEY (numCommande) REFERENCES Commande (numCommande)
);

CREATE TABLE TypePaiement(
    idType DECIMAL (5),
    libelleType VARCHAR (80),
    idPaiement DECIMAL (15),
    CONSTRAINT pk_TypePaiement PRIMARY KEY (idType),
    CONSTRAINT fk_TypePaiement_idPaiement FOREIGN KEY (idPaiement) REFERENCES Paiement (idPaiement)
);

CREATE TABLE TypeProduit (
    codeType CHAR (3),
    libelleType VARCHAR (40),
    CONSTRAINT pk_TypeProduit PRIMARY KEY (codeType)
);

CREATE TABLE Categorie(
    numCategorie DECIMAL (5),
    libelleCategorie VARCHAR (50),
    CONSTRAINT pk_Categorie PRIMARY KEY (numCategorie)
);

CREATE TABLE Produit(
    numProduit DECIMAL (15),
    coutAchat DECIMAL (12),
    prixVente  DECIMAL (12),
    codeBarre CHAR (10),
    referenceInterne VARCHAR (10),
    nomProduit VARCHAR (50),
    seuilReapprovisionnement DECIMAL (10),
    stockMax DECIMAL (10),
    stock DECIMAL (10),
    fraisSupplementaires DECIMAL (10),
    numCategorie DECIMAL (5),
    codeType CHAR (3) NOT NULL,
    numRegroupement DECIMAL(5),
    numProduitCompose DECIMAL (15) DEFAULT NULL,
    CONSTRAINT pk_Produit PRIMARY KEY (numProduit),
    CONSTRAINT fk_Produit_codeType FOREIGN KEY (codeType) REFERENCES TypeProduit (codeType),
    CONSTRAINT fk_Produit_numCategorie FOREIGN KEY (numCategorie) REFERENCES Categorie (numCategorie),
    CONSTRAINT fk_Produit_RegroupementProduit FOREIGN KEY (numRegroupement) REFERENCES RegroupementProduit (numRegroupement),
    CONSTRAINT fk_Produit_ProduitCompose FOREIGN KEY (numProduitCompose) REFERENCES ProduitCompose (numProduitCompose)
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
    numClient INT (15),
    idLivraison DECIMAL (15),
    libelleLivraison VARCHAR (50),
    CONSTRAINT fk_Livraison_numClient FOREIGN KEY (numClient) REFERENCES Client (numClient),
    CONSTRAINT fk_Livraison_idLivraison FOREIGN KEY (idLivraison) REFERENCES AdresseLivraison (idLivraison),
    CONSTRAINT pk_Livraison PRIMARY KEY (numClient, idLivraison)
);

CREATE TABLE LigneCde(
    numProduit DECIMAL (15),
    numCommande DECIMAL (15),
    quantiteCommandee DECIMAL (22) CHECK (quantiteCommandee > 0),
    quantiteLivree DECIMAL (22),
    CONSTRAINT fk_LigneCde_numProduit FOREIGN KEY (numProduit) REFERENCES Produit (numProduit),
    CONSTRAINT fk_LigneCde_numCommande FOREIGN KEY (numCommande) REFERENCES Commande (numCommande),
    CONSTRAINT pk_LigneCde PRIMARY KEY (numProduit, numCommande)
);

CREATE TABLE RegroupementProduit (
    numRegroupement DECIMAL (5),
    libelleRegroupement VARCHAR (50),
    CONSTRAINT pk_RegroupementProduit PRIMARY KEY (numRegroupement)
);

CREATE TABLE ProduitRegroupe (
    numProduit DECIMAL (15),
    numRegroupement DECIMAL (5),
    CONSTRAINT fk_ProduitRegroupe_Produit FOREIGN KEY (numProduit) REFERENCES Produit (numProduit),
    CONSTRAINT fk_ProduitRegroupe_Regroupement FOREIGN KEY (numRegroupement) REFERENCES RegroupementProduit (numRegroupement),
    CONSTRAINT pk_ProduitRegroupe PRIMARY KEY (numProduit, numRegroupement)
);

CREATE TABLE ProduitApparente (
    numProduitParent DECIMAL (15),
    numProduitEnfant DECIMAL (15),
    CONSTRAINT fk_ProduitApparente_Parent FOREIGN KEY (numProduitParent) REFERENCES Produit (numProduit),
    CONSTRAINT fk_ProduitApparente_Enfant FOREIGN KEY (numProduitEnfant) REFERENCES Produit (numProduit),
    CONSTRAINT pk_ProduitApparente PRIMARY KEY (numProduitParent, numProduitEnfant)
);

CREATE TABLE ProduitCompose (
    numProduitCompose DECIMAL (15),
    libelleProduitCompose VARCHAR (50),
    CONSTRAINT pk_ProduitCompose PRIMARY KEY (numProduitCompose)
);

CREATE TABLE Avis (
    numAvis INT AUTO_INCREMENT,
    numClient INT,
    numProduit INT,
    note INT,
    commentaire VARCHAR(255),
    dateAvis DATE,
    CONSTRAINT pk_Avis PRIMARY KEY (numAvis),
    CONSTRAINT fk_Avis_numClient FOREIGN KEY (numClient) REFERENCES Client(numClient)
);

CREATE TABLE ReponseAvis (
    numReponse INT AUTO_INCREMENT,
    numAvis INT,
    numClient INT,
    contenuReponse VARCHAR(255),
    dateReponse DATE,
    CONSTRAINT pk_ReponseAvis PRIMARY KEY (numReponse),
    CONSTRAINT fk_ReponseAvis_numAvis FOREIGN KEY (numAvis) REFERENCES Avis (numAvis),
    CONSTRAINT fk_ReponseAvis_numClient FOREIGN KEY (numClient) REFERENCES Client(numClient)
);