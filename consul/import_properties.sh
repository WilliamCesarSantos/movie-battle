# /bin/bash
cat application.properties | jq -R -s 'split("\n") | map(select(length > 0)) | map(select(startswith("#") | not)) | map(split("=")) | map({(.[0]): .[1]}) | add' > app_config.json
<app_config.json jq '. | to_entries | . | map({key:.key, value:(.value| @base64)})' > app_config_base64.json
consul kv import @app_config_base64.json

# cat app_config_base64.json | consul kv import -prefix=movie-battle