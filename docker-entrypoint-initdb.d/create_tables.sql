CREATE DATABASE battle;

CREATE SEQUENCE pk_table;

CREATE TABLE movie(
    id VARCHAR(80) PRIMARY KEY,
    name VARCHAR(255),
    genre VARCHAR(80),
    rating NUMERIC(18,6),
    votes NUMERIC(18,0),
    poster VARCHAR(400)
);

CREATE TABLE player(
    id INT PRIMARY KEY,
    name VARCHAR(255),
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);

CREATE TABLE ranking(
    id INT PRIMARY KEY,
    player_id INT,
    score NUMERIC(18,6)
);

ALTER TABLE ranking ADD CONSTRAINT fk_player_id FOREIGN KEY (player_id) REFERENCES player(id);
