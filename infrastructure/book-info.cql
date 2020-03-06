CREATE KEYSPACE IF NOT EXISTS book_info WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}
                                         AND durable_writes = true;

CREATE TABLE IF NOT EXISTS book_info.book
(
    id                   INT PRIMARY KEY,
    isbn                 TEXT,
    isbn_13              TEXT,
    asin                 TEXT,
    kindle_asin          TEXT,
    description          TEXT,
    format               TEXT,
    link                 TEXT,
    url                  TEXT,
    title                TEXT,
    edition              TEXT,
    title_without_series TEXT,
    image_url            TEXT,
    publisher            TEXT,
    page_count           int,
    publication_year     int,
    publication_date     TEXT,
    ebook                boolean,
    average_rating       DOUBLE,
    rating_count         BIGINT,
    review_count         BIGINT,
    authors              frozen<map<TEXT, TEXT>>
);

create table if not exists book_info.author_with_books
(
    author_id        int,
    book_id          int,
    author_name      text,
    publication_year int,
    role_name        text,
    primary key (author_id, book_id, publication_year)
) with clustering order by (book_id ASC, publication_year asc);


create table if not exists book_info.book_by_authors
(
    author_id        int,
    book_id          int,
    publication_year int,
    author_name      text,
    role_name        text,
    primary key (book_id, author_id, publication_year)
) with clustering order by (author_id ASC, publication_year asc);

create table if not exists book_info.book_by_isbn
(
    isbn             text primary key,
    book_id          int,
    publication_year int,
    title            text,
    url              text,
    link             text,
    image_url        text,
    description      text,
    authors          frozen<map<TEXT, TEXT>>,
);

create table if not exists book_info.book_by_isbn_13
(
    isbn             text primary key,
    book_id          int,
    publication_year int,
    title            text,
    url              text,
    link             text,
    image_url        text,
    description      text,
    authors          frozen<map<TEXT, TEXT>>,
);

CREATE TABLE IF NOT EXISTS book_info.book_author
(
    author_id      INT,
    author_name    TEXT,
    average_rating DOUBLE,
    ratings_count  BIGINT,
    reviews_count  BIGINT,
    PRIMARY KEY (author_id)
);

CREATE TABLE IF NOT EXISTS book_info.reviews
(
    book_id       INT,
    review_id     TEXT,
    user_id       TEXT,
    created_at    DATE,
    rating        DOUBLE,
    review_text   TEXT,
    vote_count    BIGINT,
    comment_count BIGINT,
    PRIMARY KEY (book_id, created_at, review_id, user_id )
) WITH CLUSTERING ORDER BY (created_at DESC, review_id ASC, user_id ASC);

