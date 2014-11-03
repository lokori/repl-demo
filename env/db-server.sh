#!/bin/bash
set -eu

if [ $# -ne 1 ]
then
    echo "$0 <sovelluspalvelimen-ip>"
    exit 1
fi

system='repl'

app_host=$1

software/postgresql.sh

# Sovelluspalvelin
iptables -I INPUT 1 -p tcp -s $app_host --dport 5432 -j ACCEPT

service iptables save

until service postgresql-9.2 status > /dev/null; do
  echo "Waiting for postgresql...\n"
  sleep 1
done
su postgres -c 'psql --file=db/dev.sql'
