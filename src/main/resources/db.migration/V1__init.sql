DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS players;
DROP TABLE IF EXISTS player_hands;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS decks;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id bigserial PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(50) NOT NULL UNIQUE,
  birthday DATE,
  games_won BIGINT DEFAULT 0,
  total_games_played BIGINT DEFAULT 0,
  profile_pic  VARCHAR DEFAULT '/images/profile/default.jpg'
  );

CREATE TABLE decks (
    id bigserial PRIMARY KEY,
    name VARCHAR(255),
    unique_stat_name VARCHAR(255),
    thumbnail text
    );

CREATE TABLE cards (
    id bigserial PRIMARY KEY,
    deck_id BIGINT,
    name VARCHAR(255),
    strength SMALLINT,
    intelligence SMALLINT,
    defense SMALLINT,
    unique_stat SMALLINT,
    luck SMALLINT,
    flavour_text VARCHAR(255),
    image text,

    FOREIGN KEY (deck_id) REFERENCES decks(id) ON DELETE CASCADE
    );

CREATE TABLE player_hands (
	id bigserial PRIMARY KEY,
	player_id BIGINT NOT NULL,
	card_id BIGINT,
	discarded BOOLEAN DEFAULT false
	);

CREATE TABLE players (
  id bigserial PRIMARY KEY,
  user_id BIGINT NOT NULL,
  current_card BIGINT,
  current_stat VARCHAR,
  current_score SMALLINT DEFAULT 0,

  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
  );

CREATE TABLE games (
    id BIGSERIAL PRIMARY KEY,
    player_1_id BIGINT NOT NULL,
    player_2_id BIGINT NOT NULL,
    points_to_win SMALLINT DEFAULT 5,

    FOREIGN KEY (player_1_id) REFERENCES players(id) ON DELETE CASCADE,
    FOREIGN KEY (player_2_id) REFERENCES players(id) ON DELETE CASCADE,
    CHECK (player_1_id <> player_2_id)
);
