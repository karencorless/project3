--V9
DROP TABLE IF EXISTS lobby;

CREATE TABLE pvp_games (
    id BIGSERIAL PRIMARY KEY,
    player_1_id BIGINT,
    player_2_id BIGINT DEFAULT NULL,
    points_to_win SMALLINT DEFAULT 1,
    status VARCHAR(20) DEFAULT 'WAITING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (player_1_id) REFERENCES players(id) ON DELETE CASCADE,
    FOREIGN KEY (player_2_id) REFERENCES players(id) ON DELETE CASCADE,
    CHECK (player_1_id <> player_2_id)
);

--  join table for pvp decks and pvp games
CREATE TABLE pvp_decks (
	id BIGSERIAL,
  	pvp_game_id BIGINT NOT NULL,
  	deck_id BIGINT,

  	CONSTRAINT fk_pvp_games FOREIGN KEY (pvp_game_id) REFERENCES pvp_games(id) ON DELETE CASCADE,
  	CONSTRAINT fk_decks FOREIGN KEY (deck_id) REFERENCES cards(id) ON DELETE CASCADE,
  	PRIMARY KEY (pvp_game_id, deck_id)
  	);

ALTER TABLE players
    ADD COLUMN pvp_game_id BIGINT,
    ADD CONSTRAINT fk_pvp_game
        FOREIGN KEY (pvp_game_id) REFERENCES pvp_games(id) ON DELETE CASCADE,
    ADD CONSTRAINT chk_pvp_or_game_is_null
        CHECK ((pvp_game_id IS NULL) OR (game_id IS NULL));
