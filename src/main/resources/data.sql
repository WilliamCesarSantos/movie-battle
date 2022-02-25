CREATE TABLE IF NOT EXISTS player(
    id IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ranking(
    id IDENTITY PRIMARY KEY,
    player_id INT NOT NULL,
    score FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS movie(
    id IDENTITY PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	genre VARCHAR(80),
	rating FLOAT NOT NULL,
	votes INT NOT NULL
);

CREATE TABLE IF NOT EXISTS battle(
    id IDENTITY PRIMARY KEY,
    description VARCHAR(80),
    created_at DATETIME,
    player_id INT NOT NULL,
    status ENUM('INITIALIZED', 'STARTED', 'FINISHED') NOT NULL
);

CREATE TABLE IF NOT EXISTS round(
    id IDENTITY PRIMARY KEY,
    movie_one_id INT NOT NULL,
    movie_two_id INT NOT NULL,
    battle_id INT NOT NULL,
    status ENUM('OPEN', 'HIT', 'MISS') NOT NULL,
    choice_id INT
);


ALTER TABLE ranking ADD CONSTRAINT IF NOT EXISTS player_fk FOREIGN KEY (player_id) REFERENCES player(id);
ALTER TABLE battle ADD CONSTRAINT IF NOT EXISTS player_fk FOREIGN KEY (player_id) REFERENCES player(id);
ALTER TABLE round ADD CONSTRAINT IF NOT EXISTS battle_fk FOREIGN KEY (battle_id) REFERENCES battle(id);
ALTER TABLE round ADD CONSTRAINT IF NOT EXISTS movie_one_fk FOREIGN KEY (movie_one_id) REFERENCES movie(id);
ALTER TABLE round ADD CONSTRAINT IF NOT EXISTS movie_two_fk FOREIGN KEY (movie_two_id) REFERENCES movie(id);
ALTER TABLE round ADD CONSTRAINT IF NOT EXISTS choice_fk FOREIGN KEY (choice_id) REFERENCES movie(id);
