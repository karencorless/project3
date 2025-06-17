--Testing seeds
DELETE FROM games;
DELETE FROM players;
DELETE FROM player_hands;
DELETE FROM cards;
DELETE FROM decks;
DELETE FROM users;

DROP SEQUENCE IF EXISTS games;
DROP SEQUENCE IF EXISTS players;
DROP SEQUENCE IF EXISTS player_hands;
DROP SEQUENCE IF EXISTS cards;
DROP SEQUENCE IF EXISTS decks;
DROP SEQUENCE IF EXISTS users;

INSERT into users (username, email, birthday)
VALUES
('Mr. Bot', 'robouser1@yahoo.com', '2000-01-01'),
('Mx. Bot', 'robouser2@yahoo.com', '2000-01-01'),
('Ms. Bot', 'robouser3@yahoo.com', '2000-01-01');

INSERT INTO decks (name, unique_stat_name, thumbnail)
VALUES ('test deck', 'testing', 'test/test');

INSERT INTO cards (deck_id, name, strength, intelligence, defense, unique_stat, luck, flavour_text, image)
VALUES
(1, 'test card', 1, 1, 1, 1, 1, 'this is test card 1', 'test/test'),
(1, 'test card2', 1, 1, 1, 1, 1, 'this is test card 2', 'test/test'),
(1, 'test card3', 1, 1, 1, 1, 1, 'this is test card 3', 'test/test'),
(1, 'test card4', 1, 1, 1, 1, 1, 'this is test card 4', 'test/test');

INSERT INTO player_hands (player_id, card_id)
VALUES (1,1), (2,2), (1, 3), (2,4);

INSERT INTO players (user_id, current_card)
VALUES (1, 1), (2, 2);

INSERT INTO games (player_1_id, player_2_id, points_to_win)
VALUES (1, 2, 10);
