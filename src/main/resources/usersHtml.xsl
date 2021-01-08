<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes" encoding="UTF-8"/>
    <xsl:param name="ProjectName"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>Project with groups</title>
            </head>
            <body>
                <h1>Project <xsl:value-of select="$ProjectName"/> </h1>
                <xsl:variable name="name" select="Payload/Projects/Project/name"/>
                <table border="1">
                    <tr>
                        <th>Groups</th>
                    </tr>
                        <xsl:for-each select="Payload/Projects/Project/Groups/Group">
                            <tr>
                                <td>
                                    <xsl:value-of select="@id"/>
                                </td>
                            </tr>
                        </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>