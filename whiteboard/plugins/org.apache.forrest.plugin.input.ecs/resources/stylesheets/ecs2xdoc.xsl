<?xml version="1.0" encoding="iso-8859-1"?>
<!--
  Copyright 2002-2005 The Apache Software Foundation or its licensors,
  as applicable.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ecs="http://webservices.amazon.com/ECSECommerceService/2005-07-26"
    xmlns:str="http://exslt.org/strings"
    xmlns:xi="http://www.w3.org/2001/XInclude"
    extension-element-prefixes="str">

  <xsl:param name="file"/>
  <xsl:param name="type"/>

  <xsl:template match="/">
    <document>
      <header>
        <xsl:choose>
          <xsl:when test="ecs:ItemSearchResponse">
            <title>Search Results</title>
          </xsl:when>
          <xsl:when test="ecs:ItemLookupResponse">
            <title>
              <xsl:value-of select="ecs:Item/ecs:ItemAttributes/ecs:Title"/>
            </title>
          </xsl:when>
        </xsl:choose>
      </header>
      <body>
        <xsl:apply-templates/>
      </body>
    </document>
  </xsl:template>

  <xsl:template match="ecs:ItemSearchResponse">
    <xsl:apply-templates select="//ecs:Item"/>
  </xsl:template>

  <xsl:template match="ecs:ItemLookupResponse">
    <xsl:apply-templates select="//ecs:Item"/>
  </xsl:template>

  <xsl:template match="OperationRequest"></xsl:template>

  <xsl:template match="Items">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="Request"></xsl:template>

  <xsl:template match="item"/>

  <xsl:template match="ecs:Item">
    <section>
      <title>
        <xsl:value-of select="ecs:ItemAttributes/ecs:Title"/>
      </title>
      <xsl:call-template name="images"/>
      <xsl:call-template name="additionalImages"/>
      <xsl:call-template name="itemAttributes"/>
      <xsl:call-template name="price"/>
      <xsl:call-template name="editorialReviews"/>
      <xsl:call-template name="moreDetails"/>
      <xsl:call-template name="customerReviews"/>
    </section>
  </xsl:template>

  <xsl:template name="price">
    <xsl:if test="ecs:Offers">
      <div id="price">
        <p>Price: <xsl:value-of select="ecs:Offers/ecs:Offer/ecs:OfferListing/ecs:Price/ecs:FormattedPrice"/>
        (<xsl:value-of select="ecs:Offers/ecs:Offer/ecs:OfferListing/ecs:Availability"/>)</p>
      </div>
    </xsl:if>
  </xsl:template>

  <xsl:template name="itemAttributes">
    <div id="ecs_itemAttributes">
      <xsl:call-template name="manufacturer"/>
      <xsl:call-template name="batteries"/>
    </div>
  </xsl:template>

  <xsl:template name="manufacturer">
    <xsl:if test="ecs:ItemAttributes/ecs:Manufacturer">
      <div id="ecs_manufacturer">
        <p>Manufacturer: <xsl:value-of select="ecs:ItemAttributes/ecs:Manufacturer"/>
        </p>
      </div>
    </xsl:if>
  </xsl:template>

  <xsl:template name="batteries">
    <div id="ecs_batteries">

      <xsl:if test="ecs:ItemAttributes/ecs:Batteries">
        <p>
           Batteries Required: <xsl:value-of select="ecs:ItemAttributes/ecs:Batteries"/>
          <xsl:if test="ecs:ItemAttributes/ecs:Batteries/@Units">
             x <xsl:value-of select="ecs:ItemAttributes/ecs:Batteries/@Units"/>
          </xsl:if>
          <xsl:choose>
            <xsl:when test="ecs:ItemAttributes/ecs:BatteriesIncluded='1'">
            (included).
              <xsl:value-of select="ecs:ItemAttributes/ecs:Batteries/@ecs:Units"/>
            </xsl:when>
            <xsl:otherwise>
            (not included).
          </xsl:otherwise>
          </xsl:choose>
        </p>
      </xsl:if>

    </div>
  </xsl:template>

  <xsl:template name="customerReviews">
    <div id="ecs_customerReviews">
      <section>
        <title>Customer Reviews</title>
        <div class="ecs_customerReviewsSummary">
          <p>Average rating is <xsl:value-of select="ecs:CustomerReviews/ecs:AverageRating"/> 
          out of 5.
          (Rated by <xsl:value-of select="ecs:CustomerReviews/ecs:TotalReviews"/> people)</p>
        </div>
        <xsl:apply-templates select="ecs:CustomerReviews/ecs:Review"/>
      </section>
    </div>
  </xsl:template>

  <xsl:template match="ecs:Review">
    <div>
      <xsl:attribute name="class">ecs_customerReview_<xsl:value-of select="position()"/>
      </xsl:attribute>
      <section>
        <title>
          <xsl:value-of select="ecs:Summary"/>
        </title>
        <xsl:apply-templates select="ecs:Content"/>
        <p>Rating: <strong><xsl:value-of select="ecs:Rating"/>
          </strong> out of 5</p>
      </section>
    </div>
  </xsl:template>

  <xsl:template match="ecs:Content">
    <xsl:for-each select="str:split(., '&lt;p&gt;')">
      <p>
        <xsl:value-of select="."/>
      </p>
    </xsl:for-each>
  </xsl:template>


  <xsl:template name="editorialReviews">
    <div id="ecs_editorialReviews">
      <section>
        <title>Editorial Reviews</title>
        <xsl:apply-templates select="//item/ecs:EditorialReviews/ecs:EditorialReview"/>
        <xsl:apply-templates select="ecs:EditorialReviews/ecs:EditorialReview"/>
      </section>
    </div>
  </xsl:template>

  <xsl:template match="ecs:EditorialReview">
    <div>
      <xsl:attribute name="class">ecs_editorialReview_<xsl:value-of select="position()"/>
      </xsl:attribute>
      <section>
        <title>
          <xsl:value-of select="ecs:Source"/>
        </title>
        <xsl:apply-templates select="ecs:Content"/>
      </section>
    </div>
  </xsl:template>

  <xsl:template name="images">
    <div id="ecs_smallImage">
      <img>
        <xsl:attribute name="src">
          <xsl:value-of select="ecs:SmallImage/ecs:URL"/>
        </xsl:attribute>
        <xsl:attribute name="height">
          <xsl:value-of select="ecs:SmallImage/ecs:Height"/>
        </xsl:attribute>
        <xsl:attribute name="width">
          <xsl:value-of select="ecs:SmallImage/ecs:Width"/>
        </xsl:attribute>
      </img>
    </div>
    <div id="ecs_mediumImage">
      <img>
        <xsl:attribute name="src">
          <xsl:value-of select="ecs:MediumImage/ecs:URL"/>
        </xsl:attribute>
        <xsl:attribute name="height">
          <xsl:value-of select="ecs:MediumImage/ecs:Height"/>
        </xsl:attribute>
        <xsl:attribute name="width">
          <xsl:value-of select="ecs:MediumImage/ecs:Width"/>
        </xsl:attribute>
      </img>
    </div>
    <div id="ecs_largeImage">
      <img>
        <xsl:attribute name="src">
          <xsl:value-of select="ecs:LargeImage/ecs:URL"/>
        </xsl:attribute>
        <xsl:attribute name="height">
          <xsl:value-of select="ecs:LargeImage/ecs:Height"/>
        </xsl:attribute>
        <xsl:attribute name="width">
          <xsl:value-of select="ecs:LargeImage/ecs:Width"/>
        </xsl:attribute>
      </img>
    </div>
  </xsl:template>

  <xsl:template name="additionalImages">
    <xsl:for-each select="//ecs:ImageSets/ecs:ImageSet/ecs:MediumImage">
      <xsl:if test="@Category != 'primary'">
        <div>
          <xsl:attribute name="id">ecs_mediumImage_<xsl:value-of select="position()"/>
          </xsl:attribute>
          <img>
            <xsl:attribute name="src">
              <xsl:value-of select="ecs:URL"/>
            </xsl:attribute>
            <xsl:attribute name="height">
              <xsl:value-of select="ecs:Height"/>
            </xsl:attribute>
            <xsl:attribute name="width">
              <xsl:value-of select="ecs:Width"/>
            </xsl:attribute>
          </img>
        </div>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="moreDetails">
    <div id="ecs_moreDetails">
      <fork>
        <xsl:attribute name="href">
          <xsl:value-of select="ecs:DetailPageURL"/>
        </xsl:attribute>
        More Details...
      </fork>
    </div>
  </xsl:template>
</xsl:stylesheet>
