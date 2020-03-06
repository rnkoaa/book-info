CREATE TABLE book
(
    id   INTEGER primary key,
    data json
);
CREATE TABLE book_author
(
    book_id   INTEGER,
    author_id INTEGER,
    data      json,
    PRIMARY KEY (book_id, author_id)
);

CREATE TABLE book_review
(
    book_id   INTEGER,
    review_id INTEGER,
    data      json not null,
    PRIMARY KEY (book_id, review_id)
);
