-- Team03 Kuo Chi --
DROP DATABASE IF EXISTS androidCA;
CREATE DATABASE androidCA;

USE androidCA;

CREATE TABLE User(
                     UserId int NOT NULL,
                     Username varchar(20) NOT NULL,
                     Password varchar(20) NOT NULL,
                     UserType varchar(5) NOT NULL,
                     Primary Key (UserId)
);

INSERT INTO User(UserId, Username, Password, UserType)
VALUES 	(1, 'Annie', 'gdipsa', 'free'),
          (2, 'Ben', 'gdipsa', 'paid'),
          (3, 'Chris', 'gdipsa', 'paid'),
          (4, 'Delia', 'gdipsa', 'free'),
          (5, 'Elain', 'gdipsa', 'paid'),
          (6, 'Frank', 'gdipsa', 'free'),
          (7, 'George', 'gdipsa', 'free'),
          (8, 'Helen', 'gdipsa', 'paid');

CREATE TABLE Record(
                       RecordId int NOT NULL AUTO_INCREMENT,
                       UserId int NOT NULL,
                       CompletionTime TIME NOT NULL,
                       Primary Key (RecordId)
);

INSERT INTO Record(RecordId, UserId, CompletionTime)
VALUES 	(1, 1, '00:01:15'),
          (2, 2, '00:00:32'),
          (3, 1, '00:01:01'),
          (4, 3, '00:01:56'),
          (5, 2, '00:01:17'),
          (6, 4, '00:00:29'),
          (7, 3, '00:00:35'),
          (8, 5, '00:00:29'),
          (9, 4, '00:00:52'),
          (10, 6, '00:00:41'),
          (11, 5, '00:01:03'),
          (12, 7, '00:01:38'),
          (13, 8, '00:00:22'),
          (14, 6, '00:01:36'),
          (15, 8, '00:01:16'),
          (16, 7, '00:01:28');

CREATE TABLE AdImage(
                        Id int NOT NULL,
                        imageurl varchar(200),
                        Primary Key (Id)
);

INSERT INTO AdImage(Id, imageurl)
VALUES 	(1, 'https://images.pexels.com/photos/2016145/pexels-photo-2016145.jpeg?auto=compress&cs=tinysrgb&w=800'),
          (2, 'https://images.pexels.com/photos/988952/pexels-photo-988952.jpeg?auto=compress&cs=tinysrgb&w=800'),
          (3, 'https://images.pexels.com/photos/267371/pexels-photo-267371.jpeg?auto=compress&cs=tinysrgb&w=800'),
          (4, 'https://images.pexels.com/photos/1435750/pexels-photo-1435750.jpeg?auto=compress&cs=tinysrgb&w=800'),
          (5, 'https://images.pexels.com/photos/1961795/pexels-photo-1961795.jpeg?auto=compress&cs=tinysrgb&w=800'),
          (6, 'https://images.pexels.com/photos/2272752/pexels-photo-2272752.jpeg?auto=compress&cs=tinysrgb&w=800');