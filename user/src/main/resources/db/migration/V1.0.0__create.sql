CREATE TABLE player(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(255)
);

CREATE TABLE ranking(
    id SERIAL PRIMARY KEY,
    player_id INT NOT NULL,
    score FLOAT NOT NULL
);

ALTER TABLE ranking ADD CONSTRAINT player_fk FOREIGN KEY (player_id) REFERENCES player(id);
