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

CREATE TABLE movie(
    id VARCHAR(40) PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	genre VARCHAR(80),
	rating FLOAT NOT NULL,
	votes INT NOT NULL,
	poster VARCHAR(400)
);

CREATE TYPE battle_status AS ENUM('CREATED', 'STARTED', 'FINISHED');

CREATE TABLE battle(
    id SERIAL PRIMARY KEY,
    description VARCHAR(80),
    created_at TIMESTAMP,
    player_id INT NOT NULL,
    status battle_status NOT NULL
);

CREATE TYPE round_type AS ENUM ('OPEN', 'HIT', 'MISS');

CREATE TABLE round(
    id SERIAL PRIMARY KEY,
    movie_one_id VARCHAR(40) NOT NULL,
    movie_two_id VARCHAR(40) NOT NULL,
    battle_id INT NOT NULL,
    status round_type NOT NULL,
    choice_id VARCHAR(40)
);

ALTER TABLE ranking ADD CONSTRAINT player_fk FOREIGN KEY (player_id) REFERENCES player(id);
ALTER TABLE battle ADD CONSTRAINT player_fk FOREIGN KEY (player_id) REFERENCES player(id);
ALTER TABLE round ADD CONSTRAINT battle_fk FOREIGN KEY (battle_id) REFERENCES battle(id);
ALTER TABLE round ADD CONSTRAINT movie_one_fk FOREIGN KEY (movie_one_id) REFERENCES movie(id);
ALTER TABLE round ADD CONSTRAINT movie_two_fk FOREIGN KEY (movie_two_id) REFERENCES movie(id);
ALTER TABLE round ADD CONSTRAINT choice_fk FOREIGN KEY (choice_id) REFERENCES movie(id);
