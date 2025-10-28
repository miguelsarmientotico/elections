#!/usr/bin/env bash

: ${HOST=localhost}
: ${PORT=8080}
: ${CAND_ID_CMTS_NEWS=1}
: ${CAND_ID_NOT_FOUND=113}
: ${CAND_ID_NOT_CMTS=113}
: ${CAND_ID_NOT_NEWS=113}

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]
  then
    if [ "$httpCode" = "200" ]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
    echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
    echo  "- Failing command: $curlCmd"
    echo  "- Response Body: $RESPONSE"
    exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]
  then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}

set -e

echo "HOST=${HOST}"
echo "PORT=${PORT}"

assertCurl 200 "curl http://$HOST:$PORT/candidate-composite/$CAND_ID_CMTS_NEWS -s"
assertEqual $CAND_ID_CMTS_NEWS $(echo $RESPONSE | jq .candidateId)
assertEqual 1 $(echo $RESPONSE | jq ".comments | length")
assertEqual 1 $(echo $RESPONSE | jq ".newsArticles | length")

assertCurl 404 "curl http://$HOST:$PORT/candidate-composite/$CAND_ID_NOT_FOUND -S"
assertEqual "No candidate found for candidateId: $CAND_ID_NOT_FOUND" "$(echo $RESPONSE | jq -r .message)"

assertCurl 422 "curl http://$HOST:$PORT/candidate-composite/-1 -s"
assertEqual "\"Invalid candidateId: -1\"" "$(echo $RESPONSE | jq .message)"

assertCurl 400 "curl http://$HOST:$PORT/candidate-composite/invalidProductId -s"
assertEqual "\"Type mismatch.\"" "$(echo $RESPONSE | jq .message)"

echo "End, all tests OK:" `date`
