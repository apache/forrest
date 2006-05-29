<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--><!-- ===================================================================

     Apache Forrest Glossary module

TYPICAL INVOCATION:

  <!ENTITY % glossary PUBLIC
      "-//Apache Forrest//ENTITIES Glossary Vx.y//EN"
      "glossary-vxy.mod">
  %glossary;

  where

    x := major version
    y := minor version

NOTES:

==================================================================== -->

<!-- =============================================================== -->
<!-- Element declarations -->
<!-- =============================================================== -->

<!ELEMENT glossary (authors?, title?, introduction?, (part)+)>
<!ATTLIST glossary %common.att;>

<!ELEMENT introduction (%flow;)*>

<!ELEMENT part (title, (item | part)+) >
<!ATTLIST part %common.att;>

<!ELEMENT item ((term)+, acronym?, see*, definitions, notes?)>
<!ATTLIST item %common.att;>

<!ELEMENT term (%content.mix;)*>
<!ATTLIST term %common.att;>

<!ELEMENT definitions (definition)+>
<!ELEMENT definition (%flow;)*>
<!ATTLIST definition cite IDREF #IMPLIED>

<!ELEMENT notes (item-note)+>
<!ELEMENT item-note (%flow;)*>

<!ELEMENT see (id, text)>

<!ELEMENT id (#PCDATA)>

<!ELEMENT text (%content.mix;)*>
<!ATTLIST text %common.att;>

<!-- =============================================================== -->
<!-- End of DTD -->
<!-- =============================================================== -->
