#!/bin/bash
set -eu

./db-server.sh 192.168.50.151

# poistetaan esteet hostin ja guestin valilta
iptables -F
service iptables save
