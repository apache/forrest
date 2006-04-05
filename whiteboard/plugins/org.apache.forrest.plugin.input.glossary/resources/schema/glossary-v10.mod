<!--
  Copyright 2006 The Apache Software Foundation or its licensors,
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
--><!-- ===================================================================

     Apache Forrest Glossary module (Version 1.0)

TYPICAL INVOCATION:

  <!ENTITY % faq PUBLIC
      "-//APACHE//ENTITIES Glossary Vxy//EN"
      "glossary-vxy.mod">
  %faq;

  where

    x := major version
    y := minor version

NOTES:

==================================================================== -->

<!-- =============================================================== -->
<!-- Element declarations -->
<!-- =============================================================== -->

<!ELEMENT glossary (authors?, (part)+)>
<!ATTLIST glossary %common.att;>

    <!ELEMENT part (title, (item | part)+) >
    <!ATTLIST part %common.att;>

    <!ELEMENT item ((term)+, acronym?, see?, definition)>
    <!ATTLIST item %common.att;>

        <!ELEMENT term (%content.mix;)*>
          <!ATTLIST term %common.att;>
        
 
        <!ELEMENT definition (%flow;)*>
          <!ATTLIST definition cite IDREF #IMPLIED>
    
    <!ELEMENT see (id, text)>
    
    <!ELEMENT id (#PCDATA)>
    
    <!ELEMENT text (%content.mix;)*>
        <!ATTLIST text %common.att;>

<!-- =============================================================== -->
<!-- End of DTD -->
<!-- =============================================================== -->
