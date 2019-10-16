
CREATE TABLE Movie
(
	mid CHAR(5) PRIMARY KEY,
	title VARCHAR(255),
	rating REAL,
	num_reviews REAL,
	year INTEGER
);
CREATE TABLE Movie_Countries
(
	mid CHAR(5) NOT NULL,
	country_origin VARCHAR(35),
	FOREIGN KEY (mid) REFERENCES Movie (mid) 
	ON DELETE CASCADE
);
CREATE TABLE Movie_Genre
(
	mid CHAR(5) NOT NULL,
	genre VARCHAR(20),
	FOREIGN KEY (mid) REFERENCES Movie (mid) 
	ON DELETE CASCADE
);
CREATE TABLE Filming_Location
(
	mid CHAR(5) NOT NULL,
	country VARCHAR(255),
	FOREIGN KEY (mid) REFERENCES Movie (mid) 
	ON DELETE CASCADE
);
CREATE TABLE Movie_Tags
(
	tagid CHAR(5) PRIMARY KEY,
	tagtext VARCHAR(255)
	-- FOREIGN KEY (tagid) REFERENCES Tag_weight (tagid) 
	-- ON DELETE CASCADE
);
CREATE TABLE Tag_Weight
(
	mid CHAR(5) NOT NULL,
	tagid CHAR(5) NOT NULL,
	tagWeight INTEGER,
	PRIMARY KEY (mid,tagid),
	FOREIGN KEY (mid) REFERENCES Movie(mid) ON DELETE CASCADE,
	FOREIGN KEY (tagid) REFERENCES Movie_Tags(tagid) ON DELETE CASCADE
);

-- todo create index
CREATE INDEX idx_genre ON Movie_Genre(genre);
CREATE INDEX idx_countries ON Movie_Countries(country_origin);
CREATE INDEX idx_location ON Filming_Location(country);
CREATE INDEX idx_tag ON Tag_Weight(tagid, tagWeight);
