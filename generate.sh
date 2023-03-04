#!/usr/bin/env sh

PROGRAM="java -jar target/acousticrules-jar-with-dependencies.jar"

$PROGRAM --stopOnDuplicates @rules_import.options -q CPP_QualityProfile.json CPP_Rules1.json CPP_Rules2.json

$PROGRAM --stopOnDuplicates @rules_import.options -q Java_QualityProfile.json Java_Rules1.json Java_Rules2.json

$PROGRAM --stopOnDuplicates @rules_import.options -q TypeScript_QualityProfile.json TypeScript_Rules1.json

$PROGRAM --stopOnDuplicates @rules_import.options -q XML_QualityProfile.json XML_Rules1.json

$PROGRAM --stopOnDuplicates @rules_import.options -q HTML_QualityProfile.json HTML_Rules1.json
$PROGRAM --stopOnDuplicates @rules_import.options -q CSS_QualityProfile.json CSS_Rules1.json