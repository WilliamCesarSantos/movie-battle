CREATE TABLE `user`(
    id IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(255) NOT NULL,
);

CREATE TABLE ranking(
    id IDENTITY PRIMARY KEY,
    user_id INT NOT NULL,
    score FLOAT NOT NULL
);

CREATE TABLE movie(
    id IDENTITY PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	genre VARCHAR(80),
	rank FLOAT NOT NULL,
	votes INT NOT NULL
);

CREATE TABLE battle(
    id IDENTITY PRIMARY KEY,
    description VARCHAR(80),
    created_at DATETIME,
    player_id INT NOT NULL,
    status ENUM('INITIALIZED', 'STARTED', 'FINISHED') NOT NULL
);

CREATE TABLE round(
    id IDENTITY PRIMARY KEY,
    movie_one_id INT NOT NULL,
    movie_two_id INT NOT NULL,
    next_round_id INT,
    battle_id INT NOT NULL,
    status ENUM('OPEN', 'HIT', 'MISS') NOT NULL,
    choose_id INT,
);


ALTER TABLE ranking ADD FOREIGN KEY user_kf (user_id) REFERENCES `user`(id);
ALTER TABLE battle ADD FOREIGN KEY player_fk (player_id) REFERENCES `user`(id);
ALTER TABLE round ADD FOREIGN KEY battle_fk (battle_id) REFERENCES battle(id);
ALTER TABLE round ADD FOREIGN KEY movie_one_fk (movie_one_id) REFERENCES movie(id);
ALTER TABLE round ADD FOREIGN KEY movie_two_fk (movie_two_id) REFERENCES movie(id);
ALTER TABLE round ADD FOREIGN KEY next_round_fk (next_round_id) REFERENCES round(id);
ALTER TABLE round ADD FOREIGN KEY choose_fk (choose_id) REFERENCES movie(id);
