source ../../../package/app-info.src

RESULT="--verbose
--name \"$APP_INFO_NAME\"
--type \"dmg\"
--app-version \"$APP_INFO_VERSION\"
--vendor \"$APP_INFO_VENDOR\"
--runtime-image ../../target/maven-jlink
--icon mmt-icon.icns
--module \"$APP_INFO_MODULE\"
"

echo "$RESULT"
