CREATE TABLE books (
    id UUID PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    pdf_path VARCHAR(1000) NOT NULL,
    cover_path VARCHAR(1000) NOT NULL,
    status VARCHAR(32) NOT NULL
);
