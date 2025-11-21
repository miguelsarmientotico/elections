#!/bin/bash

TENANT="dev-ytiqdtrd7n283l1w.us.auth0.com"
MGM_CLIENT_ID="FS4gfZ3RFg8RWZBHY9FoQStCVA3tO0Od"
MGM_CLIENT_SECRET="udwd4XjLY-w8djgym_sU7hTFCccyjGbq4c_LpA_Pi5zQWqIMbGxo5YxbQ0u2Mhkg"

# Hacer la petición y mostrar la respuesta completa
echo "Haciendo petición a Auth0..."
RESPONSE=$(curl -X POST "https://$TENANT/oauth/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=client_credentials" \
    -d "audience=https://localhost:8443/candidate-composite" \
    -d "scope=candidate:read candidate:write" \
    -d "client_id=$MGM_CLIENT_ID" \
    -d "client_secret=$MGM_CLIENT_SECRET" -s)

echo "Respuesta completa:"
echo "$RESPONSE" | jq .

ACCESS_TOKEN=$(echo "$RESPONSE" | jq -r '.access_token')
echo "Access Token: $ACCESS_TOKEN"
