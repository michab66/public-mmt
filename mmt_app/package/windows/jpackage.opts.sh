source ../../../package/app-info.src

RESULT="--verbose
--name \"$APP_INFO_NAME\"
--app-version \"$APP_INFO_VERSION\"
--vendor \"$APP_INFO_VENDOR\"
--runtime-image ../../target/maven-jlink
--module $APP_INFO_MODULE
--type \"msi\"
--win-upgrade-uuid CA01EFB3-036D-45D5-A468-AB6CED7E34F8
--win-menu
--win-shortcut
--icon mmt-icon.ico"

#Id='B98AF0E2-F941-4C78-8D5A-63BF9CD4AF67'

echo "$RESULT"

