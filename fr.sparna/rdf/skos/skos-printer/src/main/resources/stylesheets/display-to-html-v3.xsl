<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:disp="http://www.sparna.fr/thesaurus-display">
	
	<xsl:output method="html"
            encoding="UTF-8"
            indent="yes"/>
	
	<xsl:template match="/">
		<xsl:apply-templates select="disp:display" />
	</xsl:template>
	
	<xsl:template match="disp:display">
		<html>
			<head>
				<title><xsl:value-of select="disp:header/disp:title" /></title>
				<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css" rel="stylesheet"></link>
				<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
				<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/js/bootstrap.min.js"></script>
				<style>
					.unstyled > li { font-size: 80% }
					
					.ext-link {
						background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAVklEQVR4Xn3PgQkAMQhDUXfqTu7kTtkpd5RA8AInfArtQ2iRXFWT2QedAfttj2FsPIOE1eCOlEuoWWjgzYaB/IkeGOrxXhqB+uA9Bfcm0lAZuh+YIeAD+cAqSz4kCMUAAAAASUVORK5CYII=');
						background-repeat: no-repeat;
						background-position : 100% 50%;
						padding-right:13px;
						cursor:pointer;
					}			
				</style>
			</head>
			<body>
				<div class="container">
					<xsl:apply-templates />
				</div>
				<script>
			      $(document).ready(function () {
					// add external link behavior to every external link
					$('span[title]').mouseover(function() {
						$(this).addClass('ext-link');
					});
					$('span[title]').mouseout(function() {
						$(this).removeClass('ext-link');
					});
					$('span[title]').click(function() {
						window.open($(this).attr('title'));
						// change this to the following line to have links open in same window/tab
						// document.location.href = $(this).attr('title');
					});
					
					// add unstyled class to all 'no' class
					$('.no').addClass('unstyled');
			      });					
				</script>
			</body>
		</html>
	</xsl:template>
	
	<!-- Display header -->
	<xsl:template match="disp:header">
		<div class="header">
		<h1><xsl:value-of select="disp:title" /></h1>
			<div>
				<xsl:apply-templates select="disp:creator" />
				<xsl:apply-templates select="disp:date" />
				<xsl:apply-templates select="disp:version" />
				<xsl:apply-templates select="disp:description" />
			</div>
		</div>
	</xsl:template>
	<xsl:template match="disp:creator | disp:date | disp:version | disp:description">
		<xsl:value-of select="." /><br /><br />
	</xsl:template>


	<!-- Display body -->
	<xsl:template match="disp:body">

		<!-- if more than one section, and at least have a title, generate navbar -->
		<xsl:if test="count(disp:section[@title]) > 1">
			<div class="navbar navbar-fixed-bottom">
		    	<div class="navbar-inner">
		        	<ul class="nav">
		        		<xsl:for-each select="disp:section">
							<li><a href="#{@title}"><xsl:value-of select="@title" /></a></li>
						</xsl:for-each>
		      		</ul>
		    	</div>
		    </div>
		</xsl:if>
		<!-- display each section -->
		<div class="display">
			<xsl:apply-templates select="disp:section" />
		</div>

	</xsl:template>

	
	<!-- Process a section -->
	<xsl:template match="disp:section">
		<div class="section" id="{@title}">
			<xsl:if test="@title"><h2 class="title"><xsl:value-of select="@title" /></h2></xsl:if>
			<!-- process either list, table or tree -->
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Display a list -->
	<xsl:template match="disp:list">
		<ul>
			<xsl:for-each select="disp:listItem">
				<li>
					<xsl:apply-templates select="disp:conceptBlock" />
				</li>
			</xsl:for-each>
		</ul>
	</xsl:template>
	
	<!-- Display a tree -->
	<xsl:template match="disp:tree">
		<div class="display">
			<ul class="tree">
				<xsl:apply-templates select="disp:node" />
			</ul>
		</div>
	</xsl:template>
	
	<!-- process a tree node -->
	<xsl:template match="disp:node">
		<li id="{@entryId}">
			<!-- print its conceptBlock data -->
			<xsl:apply-templates select="disp:nodeData/disp:conceptBlock" />
			<!-- recurse -->
			<xsl:if test="disp:node">
				<ul>
					<xsl:apply-templates select="disp:node" />
				</ul>
			</xsl:if>
		</li> 
	</xsl:template>

	<!-- Display a table -->
	<xsl:template match="disp:table">
		<table class="table table-striped table-condensed">
			<!-- TODO : change this for tables with more than 2 cells -->
			<colgroup>
               <col span="1" style="width: 50%;" />
               <col span="1" style="width: 50%;" />
            </colgroup>
			<xsl:apply-templates select="disp:tableHeader" />
			<tbody>
				<xsl:for-each select="disp:row">
					<tr>
						<xsl:for-each select="disp:cell">
							<td>
								<xsl:apply-templates />
							</td>
						</xsl:for-each>
					</tr>
				</xsl:for-each>
			</tbody>
		</table>
	</xsl:template>
	
	<xsl:template match="disp:tableHeader">
		<thead>
			<tr>
				<xsl:for-each select="disp:cell">
					<th style="text-align:center;">
						<xsl:apply-templates />
					</th>
				</xsl:for-each>
			</tr>
		</thead>
	</xsl:template>

	<!-- display a concept block -->
	<xsl:template match="disp:conceptBlock">
		<div id="{@id}">
			<span title="{@uri}"><xsl:apply-templates select="disp:label" /></span>
			<xsl:if test="disp:att | disp:ref">
				<ul class="no">
					<xsl:apply-templates select="disp:att | disp:ref" />
				</ul>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="disp:str | disp:label | disp:typedString">
		<xsl:choose>
			<xsl:when test="@type = 'pref'"><b><xsl:value-of select="text()" /></b></xsl:when>
			<xsl:when test="@type = 'alt'"><i><xsl:value-of select="text()" /></i></xsl:when>
			<xsl:otherwise><xsl:value-of select="text()" /></xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
	
	<xsl:template match="disp:att">
		<li><xsl:value-of select="@type" /> : <xsl:apply-templates /></li>
	</xsl:template>
	
	<xsl:template match="disp:ref">
		<li><a href="#{@refId}"><xsl:value-of select="@type" /> : <xsl:apply-templates select="disp:label" /></a></li>
	</xsl:template>
	
</xsl:stylesheet>