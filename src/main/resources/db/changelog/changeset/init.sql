CREATE TABLE users(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    login VARCHAR(50) NOT NULL,
    password VARCHAR(64) NOT NULL
);

CREATE TABLE message(
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    content VARCHAR(500) NOT NULL,

    FOREIGN KEY (sender_id) REFERENCES users(id)
);

CREATE INDEX index_users_login ON users(login);