INSERT INTO USERS (ID, EMAIL, NAME, PASSWORD, BLOCKED) VALUES
(NEXT VALUE FOR USER_SEQ, 'john.doe@email.com', 'John Doe', '$2a$12$Zdu304zVW2xz.QxODEYmF.iQd2CD.OPGqJrbt2I1s3M4mKcuIAqZG', 0),
(NEXT VALUE FOR USER_SEQ, 'jane.smith@email.com', 'Jane Smith', 'abc456', 0),
(NEXT VALUE FOR USER_SEQ, 'bob.jones@email.com', 'Bob Jones', 'qwerty789', 0),
(NEXT VALUE FOR USER_SEQ, 'alice.white@email.com', 'Alice White', 'secret567', 0),
(NEXT VALUE FOR USER_SEQ, 'mike.wilson@email.com', 'Mike Wilson', 'mypassword', 0),
(NEXT VALUE FOR USER_SEQ, 'sara.brown@email.com', 'Sara Brown', 'letmein123', 0),
(NEXT VALUE FOR USER_SEQ, 'tom.jenkins@email.com', 'Tom Jenkins', 'pass4321', 0),
(NEXT VALUE FOR USER_SEQ, 'lisa.taylor@email.com', 'Lisa Taylor', 'securepwd', 0),
(NEXT VALUE FOR USER_SEQ, 'david.wright@email.com', 'David Wright', 'access123', 0),
(NEXT VALUE FOR USER_SEQ,'emily.harris@email.com', 'Emily Harris', '1234abcd', 0),

(NEXT VALUE FOR USER_SEQ,'eugene@gmail.com', 'Eugene Tsasiuk', '$2a$12$feLJU3wYmiAG5CdG0tWGE.DHvT7i74fj.VykuMV05Qhw4aHHIZk52', 0),
(NEXT VALUE FOR USER_SEQ,'client2@example.com', 'Landon Phillips', 'securepass', 0),
(NEXT VALUE FOR USER_SEQ,'client3@example.com', 'Harmony Mason', 'abc123', 0),
(NEXT VALUE FOR USER_SEQ,'client4@example.com', 'Archer Harper', 'pass456', 0),
(NEXT VALUE FOR USER_SEQ,'client5@example.com', 'Kira Jacobs', 'letmein789', 0),
(NEXT VALUE FOR USER_SEQ,'client6@example.com', 'Maximus Kelly', 'adminpass', 0),
(NEXT VALUE FOR USER_SEQ,'client7@example.com', 'Sierra Mitchell', 'mypassword', 0),
(NEXT VALUE FOR USER_SEQ,'client8@example.com', 'Quinton Saunders', 'test123', 0),
(NEXT VALUE FOR USER_SEQ,'client9@example.com', 'Amina Clarke', 'qwerty123', 0),
(NEXT VALUE FOR USER_SEQ,'client10@example.com','Bryson Chavez', 'pass789', 0);

INSERT INTO EMPLOYEES (ID, BIRTH_DATE, PHONE) VALUES
(1, '1990-05-15', '555-123-4567'),
(2, '1985-09-20', '555-987-6543'),
(3, '1978-03-08', '555-321-6789'),
(4, '1982-11-25', '555-876-5432'),
(5, '1995-07-12', '555-234-5678'),
(6, '1989-01-30', '555-876-5433'),
(7, '1975-06-18', '555-345-6789'),
(8, '1987-12-04', '555-789-0123'),
(9, '1992-08-22', '555-456-7890'),
(10,'1980-04-10', '555-098-7654');

INSERT INTO CLIENTS (ID, BALANCE) VALUES
(11, 1000.00),
(12, 1500.50),
(13, 800.75),
(14, 1200.25),
(15, 900.80),
(16, 1100.60),
(17, 1300.45),
(18, 950.30),
(19, 1050.90),
(20, 880.20);

INSERT INTO BOOKS
(NAME, GENRE, AGE_GROUP, PRICE, PUBLICATION_DATE, AUTHOR, NUMBER_OF_PAGES, CHARACTERISTICS, DESCRIPTION, LANGUAGE, ACTIVE)
VALUES
    ('The Lost Kingdom', 'Adventure', 'ADULT', 25.50, '2015-03-12', 'John Smith', 400, 'Epic quest', 'A thrilling journey through ancient lands', 'ENGLISH', 1),
    ('Sky of Light', 'Fantasy', 'TEEN', 18.75, '2012-07-20', 'Marie Dubois', 320, 'Magical realms', 'A fantasy world full of wonder', 'FRENCH', 1),
    ('Silent Shadows', 'Mystery', 'ADULT', 29.99, '2018-09-10', 'Carlos Fernandez', 450, 'Detective mystery', 'Intriguing twists in a shadowy city', 'SPANISH', 1),
    ('Heart Sound', 'Romance', 'ADULT', 21.50, '2016-05-18', 'Anna Müller', 310, 'Love story', 'A tale of love and destiny', 'GERMAN', 1),
    ('Whispers of the Stars', 'Science Fiction', 'CHILD', 19.80, '2020-11-05', 'Takashi Yamamoto', 280, 'Space adventure', 'A sci-fi journey across the galaxy', 'JAPANESE', 1),
    ('The Secret of the Old Castle', 'Thriller', 'ADULT', 27.00, '2019-02-14', 'Oleksandr Koval', 390, 'Suspense and intrigue', 'A chilling story in a haunted castle', 'UKRAINIAN', 1),
    ('The Enchanted Forest', 'Fantasy', 'TEEN', 17.50, '2013-06-22', 'Isabella Reed', 330, 'Magical creatures', 'A magical forest full of secrets', 'ENGLISH', 1),
    ('Eternal Love', 'Romance', 'ADULT', 22.50, '2014-12-12', 'Lucia Martinez', 340, 'Passionate love', 'A romance that spans lifetimes', 'SPANISH', 1),
    ('The Mystery of Time', 'Mystery', 'ADULT', 28.99, '2017-08-01', 'Julien Lefevre', 410, 'Time travel mystery', 'A gripping story that bends time', 'FRENCH', 1),
    ('Shadow of the Heart', 'Thriller', 'ADULT', 26.75, '2016-04-08', 'Katrin Schmidt', 380, 'Psychological thriller', 'A dark tale of secrets and lies', 'GERMAN', 1),
    ('Star Traveler', 'Adventure', 'CHILD', 20.00, '2021-01-15', 'Yuki Tanaka', 290, 'Space adventure', 'An exciting journey through the stars', 'JAPANESE', 1),
    ('Fiery Heart', 'Fantasy', 'TEEN', 18.99, '2015-09-25', 'Maria Petrenko', 310, 'Epic fantasy', 'A tale of magic and heroism', 'UKRAINIAN', 1),
    ('Hidden Horizons', 'Science Fiction', 'ADULT', 24.99, '2018-05-15', 'Emily White', 400, 'Interstellar voyage', 'Exploring new worlds beyond imagination', 'ENGLISH', 1),
    ('Song of the Stars', 'Romance', 'ADULT', 23.50, '2011-03-20', 'Sophie Laurent', 330, 'Star-crossed love', 'A touching romance under the stars', 'FRENCH', 1),
    ('Shadows of the Past', 'Mystery', 'ADULT', 29.50, '2019-10-11', 'Miguel Ramirez', 420, 'Detective work', 'Mysteries unravel in the heart of the city', 'SPANISH', 1),
    ('Heart of the Night', 'Thriller', 'ADULT', 27.25, '2017-02-14', 'Lukas Becker', 360, 'Suspense thriller', 'A night filled with dark secrets', 'GERMAN', 1),
    ('Whisper of the Wind', 'Adventure', 'CHILD', 19.50, '2020-06-18', 'Hana Suzuki', 270, 'Windy adventures', 'A story of courage and friendship', 'JAPANESE', 1),
    ('Key from the Past', 'Fantasy', 'TEEN', 17.75, '2013-08-30', 'Iryna Savchuk', 320, 'Magical artifacts', 'A journey to unlock ancient powers', 'UKRAINIAN', 1),
    ('Starlight Dreams', 'Romance', 'ADULT', 21.75, '2014-11-15', 'Michael Rose', 320, 'Heartfelt romance', 'A beautiful journey of love and longing', 'ENGLISH', 1),
    ('Echoes of Eternity', 'Fantasy', 'TEEN', 16.50, '2011-01-15', 'Daniel Black', 350, 'Enchanting realms', 'A magical fantasy of destiny', 'SPANISH', 1),
    ('Mystery in Shadows', 'Mystery', 'ADULT', 30.00, '2018-08-11', 'Sophia Green', 450, 'Suspense', 'A gripping tale of intrigue and secrets', 'GERMAN', 1),
    ('Melody of Souls', 'Fantasy', 'TEEN', 15.99, '2013-05-15', 'Isabella Reed', 330, 'Enchanting realms', 'A magical fantasy filled with wonder', 'FRENCH', 1),
    ('Silent Whispers', 'Mystery', 'ADULT', 27.50, '2021-05-15', 'Benjamin Hall', 420, 'Detective work', 'A mystery that keeps you on the edge', 'ENGLISH', 1),
    ('Love in the Storm', 'Romance', 'OTHER', 23.25, '2022-05-15', 'Emma Turner', 360, 'Passionate love', 'A romance that sweeps you off your feet', 'SPANISH', 1),
    ('Star Symphony', 'Adventure', 'ADULT', 24.99, '2018-05-15', 'Olena Koval', 400, 'Epic journey', 'An enthralling adventure through the stars', 'UKRAINIAN', 1),
    ('The Hidden Treasure', 'Adventure', 'ADULT', 25.50, '2015-05-15', 'Emilie Blanc', 400, 'Hidden treasures', 'A secret adventure awaits', 'FRENCH', 1),
    ('Shadows of the Past', 'Thriller', 'ADULT', 26.50, '2015-05-15', 'Olivia Smith', 380, 'Suspenseful twists', 'A thrilling tale of danger and intrigue', 'GERMAN', 1),
    ('Voice of the Wind', 'Fantasy', 'TEEN', 16.75, '2013-05-15', 'Isabella Yamamoto', 330, 'Mystical realms', 'A magical story filled with wonder', 'JAPANESE', 1),
    ('Secret Whispers', 'Mystery', 'ADULT', 27.50, '2021-05-15', 'Benjamin Hall', 420, 'Intricate detective work', 'A mystery that keeps you on the edge', 'UKRAINIAN', 1);


