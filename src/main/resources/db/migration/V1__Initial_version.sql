CREATE TABLE runner (
    id TEXT NOT NULL PRIMARY KEY,
    uri TEXT NOT NULL,
    timeout REAL NOT NULL,
    interval REAL NOT NULL
);


CREATE TABLE result (
    id TEXT NOT NULL PRIMARY KEY,
    statusCode INTEGER NOT NULL,
    responseTime REAL NOT NULL,
    runnerId TEXT REFERENCES runner(id)
);