<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>

    <xsl:template name="startDocument">
        <xsl:text disable-output-escaping="no">
</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;licenseSummary&gt;</xsl:text>
        <xsl:text disable-output-escaping="no">
  </xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;dependencies&gt;</xsl:text>
    </xsl:template>

    <xsl:template name="endDocument">
            <xsl:text disable-output-escaping="no">
  </xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/dependencies&gt;</xsl:text>
        <xsl:text disable-output-escaping="no">
</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/licenseSummary&gt;</xsl:text>
    </xsl:template>

    <xsl:template name="processFile">
        <xsl:param name="fileName"/>
        <xsl:for-each select="document($fileName)/licenseSummary/dependencies/*">
            <xsl:text disable-output-escaping="no">
    </xsl:text>
            <xsl:copy>
                <xsl:apply-templates select="@* | node()"/><xsl:text disable-output-escaping="no">  </xsl:text><source><xsl:value-of select="$fileName"/></source><xsl:text disable-output-escaping="no">
    </xsl:text>
            </xsl:copy>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
