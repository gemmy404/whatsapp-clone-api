CREATE TABLE users
(
    id                 VARCHAR(255),
    created_date       TIMESTAMP(6) NOT NULL,
    last_modified_date TIMESTAMP(6),
    email              VARCHAR(255),
    first_name         VARCHAR(255),
    last_name          VARCHAR(255),
    last_seen          TIMESTAMP(6),
    CONSTRAINT users_pkey
        PRIMARY KEY (id),
    CONSTRAINT uk_email
        UNIQUE (email)
);