-- V3__add_authid_to_users.sql

ALTER TABLE users
ADD COLUMN auth0_id VARCHAR(255) UNIQUE;
