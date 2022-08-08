DROP TABLE IF EXISTS books;

CREATE TABLE books(
    id INT PRIMARY KEY,
    price VARCHAR(250) DEFAULT NULL,
    price_old VARCHAR(250) DEFAULT NULL,
    title VARCHAR(250) NOT NULL,
    author_id INT NOT NULL
);

DROP TABLE IF EXISTS authors;

create table authors (
     id INT PRIMARY KEY ,
     first_name VARCHAR(50),
     last_name VARCHAR(50)
);