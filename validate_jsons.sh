#!/usr/bin/env sh

OPTIONS=""
QUALITY_PROFILE_SCHEMA="schema/QualityProfile_Schema.json"
PROCESSING_GROUP_SCHEMA="schema/ProcessingGroup_Schema.json"

for qualityProfileFile in *_QualityProfile.json; do
  echo "Validating $qualityProfileFile"
  check-jsonschema $OPTIONS --schemafile $QUALITY_PROFILE_SCHEMA $qualityProfileFile
done

for processingGroupFile in rules/*.json; do
  echo "Validating $processingGroupFile"
  check-jsonschema $OPTIONS --schemafile $PROCESSING_GROUP_SCHEMA $processingGroupFile
done
