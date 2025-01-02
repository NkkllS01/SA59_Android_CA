-- Team03 Kuo Chi --
DROP DATABASE androidCA;
CREATE DATABASE androidCA;

USE androidCA;

CREATE TABLE User(
	UserId int NOT NULL,
    Username varchar(20) NOT NULL,
    Password varchar(20) NOT NULL,
    Primary Key (UserId)
);

INSERT INTO User(UserId, Username, Password)
VALUES 	(1, 'Annie', 'gdipsa'),
		(2, 'Ben', 'gdipsa'),
        (3, 'Chris', 'gdipsa'),
        (4, 'Delia', 'gdipsa'),
        (5, 'Elain', 'gdipsa'),
        (6, 'Frank', 'gdipsa'),
        (7, 'George', 'gdipsa'),
        (8, 'Helen', 'gdipsa');

CREATE TABLE Record(
	RecordId int NOT NULL,
    UserId int NOT NULL,
    CompletionTime TIME NOT NULL,
    Primary Key (RecordId)
);

INSERT INTO Record(RecordId, UserId, CompletionTime)
VALUES 	(1, 1, '00:08:15'),
		(2, 2, '00:10:32'),
        (3, 1, '00:11:01'),
        (4, 3, '00:09:56'),
        (5, 2, '00:10:17'),
        (6, 4, '00:07:39'),
        (7, 3, '00:06:35'),
        (8, 5, '00:07:39'),
        (9, 4, '00:08:52'),
        (10, 6, '00:10:41'),
        (11, 5, '00:12:03'),
        (12, 7, '00:11:38'),
        (13, 8, '00:09:22'),
        (14, 6, '00:10:36'),
        (15, 8, '00:12:16'),
        (16, 7, '00:10:28');