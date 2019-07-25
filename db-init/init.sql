CREATE TABLE IF NOT EXISTS users (id int NOT NULL AUTO_INCREMENT, name TEXT, birthDate DATE, city TEXT, PRIMARY KEY(id));
SET GLOBAL max_connections=50000;
SET GLOBAL thread_cache_size=250;
