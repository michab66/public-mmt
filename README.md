# Macular Mapping Test 2.0

* New Java 11 support.
* New Maven build.
* Integrated repositories sec and common.

# Building

## Tools

### Windows
* [Active: 64bit jdk 11](https://corretto.aws/downloads/latest/amazon-corretto-11-x64-windows-jdk.msi)
* [Info: Latest Coretto 64bit jdk 11](https://corretto.aws/downloads/latest/amazon-corretto-11-x64-linux-jdk.tar.gz)
* For further installation notes, especially regarding handling of spaces in the installation path see [build documentation from smack](https://github.com/smacklib/dev_smack/wiki/Build).

### Mac
* [Active: 64bit jdk 11](https://corretto.aws/downloads/latest/amazon-corretto-11-x64-macos-jdk.pkg)

### Installer generation
See [mmt-tools](https://jdk.java.net/14/) for the required jdk-14 that has the needed jpackage tool.

## Maven configuration

Add to Maven .m2/settings.xml to properly download smack:
```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <localRepository />
  <interactiveMode />
  <offline />
  <pluginGroups />
  <servers>
    <server>
      <id>github</id>
      <username>michab66</username>
      <password>f89ce99fb607dc23be695ebceafe8eb33db9a3f4</password>
    </server>
  </servers>

  <mirrors />
  <proxies />
  <profiles />
  <activeProfiles />
</settings>
```
