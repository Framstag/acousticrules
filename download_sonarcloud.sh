#!/usr/bin/env sh

if [ $# -ne 2 ]; then
  echo "download_soundcloud.sh <user_token> <organisation>"
  exit 1
fi

TOKEN="$1"
ORGANISATION="$2"

curl -u "$TOKEN:" -o Java_Rules1.json "https://sonarcloud.io/api/rules/search?organization=$ORGANISATION&languages=java&ps=500&p=1"
curl -u "$TOKEN:" -o Java_Rules2.json "https://sonarcloud.io/api/rules/search?organization=$ORGANISATION&languages=java&ps=500&p=2"

curl -u "$TOKEN:" -o CPP_Rules1.json "https://sonarcloud.io/api/rules/search?organization=$ORGANISATION&languages=cpp&ps=500&p=1"
curl -u "$TOKEN:" -o CPP_Rules2.json "https://sonarcloud.io/api/rules/search?organization=$ORGANISATION&languages=cpp&ps=500&p=2"

curl -u "$TOKEN:" -o XML_Rules1.json "https://sonarcloud.io/api/rules/search?organization=$ORGANISATION&languages=xml&ps=500&p=1"

curl -u "$TOKEN:" -o HTML_Rules1.json "https://sonarcloud.io/api/rules/search?organization=$ORGANISATION&languages=web&ps=500&p=1"

curl -u "$TOKEN:" -o CSS_Rules1.json "https://sonarcloud.io/api/rules/search?organization=$ORGANISATION&languages=css&ps=500&p=1"

curl -u "$TOKEN:" -o TypeScript_Rules1.json "https://sonarcloud.io/api/rules/search?organization=$ORGANISATION&languages=ts&ps=500&p=1"