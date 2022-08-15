DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS authors;

CREATE TABLE books(
    id BIGSERIAL PRIMARY KEY,
    price VARCHAR(250) DEFAULT NULL,
    price_old VARCHAR(250) DEFAULT NULL,
    title VARCHAR(250) NOT NULL,
    author_id SERIAL NOT NULL
);

create table authors (
     id BIGSERIAL PRIMARY KEY ,
     first_name VARCHAR(50),
     last_name VARCHAR(50)
);