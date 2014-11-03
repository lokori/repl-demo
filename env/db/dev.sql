create user repl_adm with password 'repl-adm';
CREATE DATABASE sourcedb;
CREATE DATABASE targetdb;
GRANT ALL PRIVILEGES ON DATABASE sourcedb to repl_adm;
GRANT ALL PRIVILEGES ON DATABASE targetdb to repl_adm;

