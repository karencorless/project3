-- Seed file for basic testing
-- This file seeds three users, one deck, four cards, two players, and one game.
-- If player card seeds are needed, uncomment lines 42-43 and 9.
-- Run this after all migrations are complete

-- Clear existing data
DELETE FROM games CASCADE;
DELETE FROM players CASCADE;
DELETE FROM player_cards CASCADE;
DELETE FROM cards CASCADE;
DELETE FROM decks CASCADE;
DELETE FROM users CASCADE;

-- Insert users
INSERT into users (username, email, birthday)
VALUES
('Mr. Bot', 'robouser1@yahoo.com', '2000-01-01'),
('Mx. Bot', 'robouser2@yahoo.com', '2000-01-01'),
('Ms. Bot', 'robouser3@yahoo.com', '2000-01-01');

-- Add deck
INSERT INTO decks (name, unique_stat_name, thumbnail)
VALUES ('test deck', 'testing', 'test/test');

-- Add cards
INSERT INTO cards (deck_id, name, strength, wisdom, defence, unique_stat, luck, flavour_text, image)
VALUES
(1, 'test card1', 1, 1, 1, 1, 1, 'this is test card 1', 'test/test'),
(1, 'test card2', 1, 1, 1, 1, 1, 'this is test card 2', 'test/test'),
(1, 'test card3', 1, 1, 1, 1, 1, 'this is test card 3', 'test/test'),
(1, 'test card4', 1, 1, 1, 1, 1, 'this is test card 4', 'test/test');

-- Add players
INSERT INTO players (user_id, current_card)
VALUES (1, 1), (2, 2);

-- Add game
INSERT INTO games (player_1_id, player_2_id, points_to_win)
VALUES (1, 2, 10);

---- Add player cards (Uncomment if needed)
--INSERT INTO player_cards (player_id, card_id)
--VALUES (1,1), (2,2), (1, 3), (2,4);
