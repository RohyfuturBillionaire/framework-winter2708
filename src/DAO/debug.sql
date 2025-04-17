INSERT INTO commandes (client)  
SELECT 'Client ' || generate_series(1, 100000); 

INSERT INTO details_commandes (commande_id, produit, quantite, prix) 
SELECT floor(random() * 100000) + 1,  
'Produit ' || floor(random() * 100),  
floor(random() * 10) + 1,  
round((random()*1000)::numeric, 2) 
FROM generate_series(1, 500000);

insert into type_etat_reservation VALUES 