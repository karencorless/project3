ALTER TABLE users
ADD COLUMN favourite_deck_id BIGINT,
ADD FOREIGN KEY (favourite_deck_id) REFERENCES decks(id) ON DELETE SET NULL;
