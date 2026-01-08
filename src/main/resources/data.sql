INSERT INTO persons (name) VALUES ('Teszt Elek'); -- ID: 1
INSERT INTO persons (name) VALUES ('Gipsz Jakab'); -- ID: 2
INSERT INTO persons (name) VALUES ('Nagy Anna');   -- ID: 3

INSERT INTO addresses (person_id, zip_code, city, street, house_number, type)
VALUES (1, '1111', 'Budapest', 'Fő utca', '10/A', 'PERMANENT'); -- Address ID: 1

INSERT INTO addresses (person_id, zip_code, city, street, house_number, type)
VALUES (1, '8600', 'Siófok', 'Nyári út', '5', 'TEMPORARY');     -- Address ID: 2

INSERT INTO addresses (person_id, zip_code, city, street, house_number, type)
VALUES (2, '4000', 'Debrecen', 'Kossuth tér', '1', 'PERMANENT'); -- Address ID: 3

INSERT INTO contacts (address_id, contact_value, type)
VALUES (1, 'elek.teszt@email.com', 'EMAIL');

INSERT INTO contacts (address_id, contact_value, type)
VALUES (1, '+36-30-123-4567', 'PHONE');

INSERT INTO contacts (address_id, contact_value, type)
VALUES (2, '+36-20-987-6543', 'PHONE');

INSERT INTO contacts (address_id, contact_value, type)
VALUES (3, 'jakab@cegesmail.hu', 'EMAIL');