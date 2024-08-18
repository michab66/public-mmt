<?xml version="1.0" encoding="UTF-8"?>

<!-- 
  - Transforms html to a JavaFX fxml.
  - Used in the MmtTextFlow component.
  -
  - $Id$
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">
  <xsl:output method="xml" />

  <!-- Entry point. Fun begins here. -->
  <xsl:template match="html/body">
    <xsl:text>&#x0A;</xsl:text>
    <xsl:processing-instruction name="import">de.michab.app.mmt.components.MmtTextFlow</xsl:processing-instruction>
    <xsl:text>&#x0A;</xsl:text>
    <xsl:processing-instruction name="import">de.michab.app.mmt.components.MmtText</xsl:processing-instruction>
    <xsl:text>&#x0A;</xsl:text>

    <xsl:element name="MmtTextFlow">
      <xsl:attribute name="textAlignment">
          <xsl:value-of select="./@align" />
        </xsl:attribute>

      <xsl:apply-templates />

    </xsl:element>
  </xsl:template>

  <!-- Process special font selection. 
     - Mainly used for color setting, being a font attribute. -->
  <xsl:template match="font">
    <xsl:element name="MmtText">
      <xsl:attribute name="mmtColor">
        <xsl:value-of select="./@color" />
      </xsl:attribute>
      <xsl:attribute name="text">
        <xsl:value-of select="." />
      </xsl:attribute>
    </xsl:element>
    <xsl:text>&#x0A;</xsl:text>
  </xsl:template>

  <!-- Process line breaks. -->
  <xsl:template match="br">
    <xsl:element name="MmtText">
      <xsl:attribute name="text">&#13;</xsl:attribute>
    </xsl:element>
    <xsl:text>&#x0A;</xsl:text>
  </xsl:template>

  <!-- Match text only inside the body. -->
  <xsl:template match="html/body/text()">
    <xsl:element name="MmtText">
      <xsl:attribute name="text">
          <xsl:value-of select="." />
        </xsl:attribute>
    </xsl:element>
    <xsl:text>&#x0A;</xsl:text>
  </xsl:template>
  
  <!-- Swallow remaining text, do not pipe thru. -->
  <xsl:template match="text()"/>
  
</xsl:stylesheet>
