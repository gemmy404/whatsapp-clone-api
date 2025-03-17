CREATE SEQUENCE msg_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE messages
(
    id                 BIGINT       DEFAULT NEXTVAL('msg_seq'),
    created_date       TIMESTAMP(6) NOT NULL,
    last_modified_date TIMESTAMP(6),
    content            TEXT,
    media_file_path    VARCHAR(255),
    receiver_id        VARCHAR(255) NOT NULL,
    sender_id          VARCHAR(255) NOT NULL,
    state              VARCHAR(255),
    type               VARCHAR(255),
    chat_id            VARCHAR(255),
    CONSTRAINT messages_pkey
        PRIMARY KEY (id),
    CONSTRAINT fk_chat
        FOREIGN KEY (chat_id) references chats,
    CONSTRAINT messages_state_check
        CHECK ((state)::text = ANY ((ARRAY ['SENT'::character varying, 'SEEN'::character varying])::text[])),
    CONSTRAINT messages_type_check
        CHECK ((type)::text = ANY
               ((ARRAY ['TEXT'::character varying, 'AUDIO'::character varying, 'IMAGE'::character varying, 'VIDEO'::character varying])::text[]))
);