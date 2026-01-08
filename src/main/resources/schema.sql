DROP TABLE IF EXISTS contacts;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS persons;

CREATE TABLE persons (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL
);

CREATE TABLE addresses (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    person_id BIGINT NOT NULL,
    zip_code NVARCHAR(20),
    city NVARCHAR(100),
    street NVARCHAR(255),
    house_number NVARCHAR(50),
    type NVARCHAR(20) NOT NULL,

    CONSTRAINT fk_address_person
        FOREIGN KEY (person_id)
        REFERENCES persons(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_person_address_type UNIQUE (person_id, type)
);

CREATE TABLE contacts (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    address_id BIGINT NOT NULL,
    contact_value NVARCHAR(255) NOT NULL,
    type NVARCHAR(20) NOT NULL,

    CONSTRAINT fk_contact_address
        FOREIGN KEY (address_id)
        REFERENCES addresses(id)
        ON DELETE CASCADE
);