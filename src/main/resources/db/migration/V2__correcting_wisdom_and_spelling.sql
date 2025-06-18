ALTER TABLE cards
RENAME COLUMN intelligence TO wisdom;

ALTER TABLE cards
RENAME COLUMN defense TO defence;

ALTER TABLE player_cards
ADD id bigserial;