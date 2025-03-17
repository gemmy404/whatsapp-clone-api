CREATE TABLE chats
(
    id                 VARCHAR(255),
    created_date       TIMESTAMP(6) NOT NULL,
    last_modified_date TIMESTAMP(6),
    recipient_id       VARCHAR(255),
    sender_id          VARCHAR(255),
    CONSTRAINT chats_pkey
        PRIMARY KEY (id),
    CONSTRAINT fk_recipient
        FOREIGN KEY (recipient_id) REFERENCES users,
    CONSTRAINT fk_sender
        FOREIGN KEY (sender_id) REFERENCES users
);