ALTER TABLE users
ALTER COLUMN profile_pic
DROP DEFAULT;

ALTER TABLE users
ALTER COLUMN profile_pic
SET DEFAULT '/uploads/profilePics/default.jpg';