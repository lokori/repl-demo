create user repl-adm with password 'repl-adm';
CREATE DATABASE sourcedb;
CREATE DATABASE targetdb;
GRANT ALL PRIVILEGES ON DATABASE sourcedb to repl-adm;
GRANT ALL PRIVILEGES ON DATABASE targetdb to repl-adm;

