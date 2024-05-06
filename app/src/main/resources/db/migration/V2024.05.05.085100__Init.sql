DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id         BINARY(16) PRIMARY KEY,
    username   VARCHAR(255)  NOT NULL,
    email      VARCHAR(255)  NOT NULL,
    password   VARCHAR(255)  NOT NULL,
    image      VARCHAR(4096) NOT NULL DEFAULT '',
    bio        VARCHAR(4096) NOT NULL DEFAULT '',
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY idx_users_username_email (username, email)
);

DROP TABLE IF EXISTS user_follows;
CREATE TABLE user_follows
(
    id          BINARY(16) PRIMARY KEY,
    follower_id BINARY(16) NOT NULL,
    followee_id BINARY(16) NOT NULL,
    created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS user_favorites;
CREATE TABLE user_favorites
(
    id         BINARY(16) PRIMARY KEY,
    user_id    BINARY(16) NOT NULL,
    article_id BINARY(16) NOT NULL,
    created_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS articles;
CREATE TABLE articles
(
    id          BINARY(16) PRIMARY KEY,
    author_id   BINARY(16)    NOT NULL,
    slug        VARCHAR(255)  NOT NULL,
    title       VARCHAR(255)  NOT NULL,
    description VARCHAR(255)  NOT NULL,
    body        VARCHAR(4096) NOT NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY idx_articles_slug (slug)
);

DROP TABLE IF EXISTS comments;
CREATE TABLE comments
(
    id         BINARY(16) PRIMARY KEY,
    author_id  BINARY(16)    NOT NULL,
    article_id BINARY(16)    NOT NULL,
    body       VARCHAR(4096) NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS tags;
CREATE TABLE tags
(
    id          BINARY(16) PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY idx_tag_slug (description)
);

DROP TABLE IF EXISTS article_tags;
CREATE TABLE article_tags
(
    id         BINARY(16) PRIMARY KEY,
    article_id BINARY(16) NOT NULL,
    tag_id     BINARY(16) NOT NULL,
    created_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY idx_article_tags_article_tag (article_id, tag_id)
);

-- ----------------------------------------------------------------------
-- For test
DROP TABLE IF EXISTS vince_tmp;
CREATE TABLE vince_tmp
(
    ulid       BINARY(16)   PRIMARY KEY,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_valid   TINYINT(1)   NOT NULL DEFAULT FALSE
);
