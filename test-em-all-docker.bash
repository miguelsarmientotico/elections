#!/usr/bin/env bash

: ${HOST=localhost}
: ${PORT=8080}
: ${CAND_ID_CMTS_NEWS=1}
: ${CAND_ID_NOT_FOUND=13}
: ${CAND_ID_NOT_CMTS=113}
: ${CAND_ID_NOT_NEWS=213}

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

function testUrl() {
  url=$@
  if $url -ks -f -o /dev/null
  then
    return 0
  else
    return 1
  fi;
}

function waitForService() {
  url=$@
  echo -n "Wait for: $url... "
  n=0
  until testUrl $url
  do
    n=$((n + 1))
    if [[ $n == 100 ]]
    then
      echo " Give up"
      exit 1
    else
      sleep 3
      echo -n ", retry #$n "
    fi
  done
  echo "DONE, continues..."
}

function testCompositeCreated() {

    # Expect that the Product Composite for productId $PROD_ID_REVS_RECS has been created with three recommendations and three reviews
    if ! assertCurl 200 "curl http://$HOST:$PORT/candidate-composite/$CAND_ID_CMTS_NEWS -s"
    then
        echo -n "FAIL"
        return 1
    fi

    set +e
    assertEqual "$CAND_ID_CMTS_NEWS" $(echo $RESPONSE | jq .candidateId)
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".comments | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".newsArticles | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    set -e
}

function waitForMessageProcessing() {
    echo "Wait for messages to be processed... "

    # Give background processing some time to complete...
    sleep 1

    n=0
    until testCompositeCreated
    do
        n=$((n + 1))
        if [[ $n == 40 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
    echo "All messages are now processed!"
}

function recreateComposite() {
  local candidateId=$1
  local composite=$2

  assertCurl 202 "curl -X DELETE http://$HOST:$PORT/candidate-composite/${candidateId} -s"
  assertEqual 202 $(curl -X POST -s http://$HOST:$PORT/candidate-composite -H "Content-Type: application/json" --data "$composite" -w "%{http_code}")
}

function setupTestdata() {

  body="{\"candidateId\":$CAND_ID_NOT_CMTS"
  body+=\
',"name":"candidate name A","edad":30, "newsArticles":[
  {"newsArticleId":1,"title":"Title 1","content":"Content 1","author":"Author 1","publishDate":"2024-06-01T10:00:00Z","category":"Politics"},
  {"newsArticleId":2,"title":"Title 2","content":"Content 2","author":"Author 2","publishDate":"2024-06-01T10:00:00Z","category":"Economy"},
  {"newsArticleId":3,"title":"Title 3","content":"Content 3","author":"Author 3","publishDate":"2024-06-01T10:00:00Z","category":"Technology"}
]}'
  recreateComposite "$CAND_ID_NOT_CMTS" "$body"

  body="{\"candidateId\":$CAND_ID_NOT_NEWS"
  body+=\
',"name":"candidate name B","edad":29, "comments":[
  {"commentId":1,"content":"Excellent candidate","author":"Voter A","createdAt":"1717200000000"},
  {"commentId":2,"content":"Fully supported","author":"Voter B","createdAt":"1717200000000"},
  {"commentId":3,"content":"Great proposals","author":"Voter C","createdAt":"1717200000000"}
]}'
  recreateComposite "$CAND_ID_NOT_NEWS" "$body"


  body="{\"candidateId\":$CAND_ID_CMTS_NEWS"
  body+=\
',"name":"candidate name C","edad":31, "comments":[
  {"commentId":1,"content":"Excellent candidate","author":"Voter A","createdAt":"1717200000000"},
  {"commentId":2,"content":"Fully supported","author":"Voter B","createdAt":"1717200000000"},
  {"commentId":3,"content":"Great proposals","author":"Voter C","createdAt":"1717200000000"}
  ], "newsArticles":[
  {"newsArticleId":1,"title":"Title 1","content":"Content 1","author":"Author 1","publishDate":"2024-06-01T10:00:00Z","category":"Politics"},
  {"newsArticleId":2,"title":"Title 2","content":"Content 2","author":"Author 2","publishDate":"2024-06-01T10:00:00Z","category":"Economy"},
  {"newsArticleId":3,"title":"Title 3","content":"Content 3","author":"Author 3","publishDate":"2024-06-01T10:00:00Z","category":"Technology"}
  ]}'
  recreateComposite "$CAND_ID_CMTS_NEWS" "$body"

}

set -e

echo "Start Tests:" `date`

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]
then
  echo "Restarting the test environment..."
  echo "$ docker compose down --remove-orphans"
  docker compose down --remove-orphans
  echo "$ docker compose up -d"
  docker compose up -d --build
fi

waitForService curl http://$HOST:$PORT/actuator/health

assertCurl 200 "curl -H "accept:application/json" $HOST:8761/eureka/apps -s"
assertEqual 4 $(echo $RESPONSE | jq ".applications.application | length")

echo "test setupTestdata"
setupTestdata

echo "test message"
waitForMessageProcessing

echo "init asserts"
assertCurl 200 "curl http://$HOST:$PORT/candidate-composite/$CAND_ID_CMTS_NEWS -s"
assertEqual $CAND_ID_CMTS_NEWS $(echo $RESPONSE | jq .candidateId)
assertEqual 3 $(echo $RESPONSE | jq ".comments | length")
assertEqual 3 $(echo $RESPONSE | jq ".newsArticles | length")

assertCurl 404 "curl http://$HOST:$PORT/candidate-composite/$CAND_ID_NOT_FOUND -S"
assertEqual "No candidate found for candidateId: $CAND_ID_NOT_FOUND" "$(echo $RESPONSE | jq -r .message)"

assertCurl 200 "curl http://$HOST:$PORT/candidate-composite/$CAND_ID_NOT_NEWS -s"
assertEqual $CAND_ID_NOT_NEWS $(echo $RESPONSE | jq .candidateId)
assertEqual 3 $(echo $RESPONSE | jq ".comments | length")
assertEqual 0 $(echo $RESPONSE | jq ".newsArticles | length")

assertCurl 200 "curl http://$HOST:$PORT/candidate-composite/$CAND_ID_NOT_CMTS -s"
assertEqual $CAND_ID_NOT_CMTS $(echo $RESPONSE | jq .candidateId)
assertEqual 0 $(echo $RESPONSE | jq ".comments | length")
assertEqual 3 $(echo $RESPONSE | jq ".newsArticles | length")

assertCurl 422 "curl http://$HOST:$PORT/candidate-composite/-1 -s"
assertEqual "\"Invalid candidateId: -1\"" "$(echo $RESPONSE | jq .message)"

assertCurl 400 "curl http://$HOST:$PORT/candidate-composite/invalidCandidateId -s"
assertEqual "\"Type mismatch.\"" "$(echo $RESPONSE | jq .message)"

echo "Swagger/OpenAPI tests"
assertCurl 302 "curl -s  http://$HOST:$PORT/openapi/swagger-ui.html"
assertCurl 200 "curl -sL http://$HOST:$PORT/openapi/swagger-ui.html"
assertCurl 200 "curl -s  http://$HOST:$PORT/openapi/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config"
assertCurl 200 "curl -s  http://$HOST:$PORT/openapi/v3/api-docs"
assertEqual "3.1.0" "$(echo $RESPONSE | jq -r .openapi)"
assertEqual "http://$HOST:$PORT" "$(echo $RESPONSE | jq -r '.servers[0].url')"
assertCurl 200 "curl -s  http://$HOST:$PORT/openapi/v3/api-docs.yaml"


if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    echo "$ docker compose down"
    docker compose down
fi

echo "End, all tests OK:" `date`
