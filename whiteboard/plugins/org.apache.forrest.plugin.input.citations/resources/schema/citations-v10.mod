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
-->
<!-- ===================================================================

     Apache References module (Version 1.0)

TYPICAL INVOCATION:

  <!ENTITY % faq PUBLIC
      "-//Apache Forrest//ENTITIES Citations Vxy//EN"
      "citations-vxy.mod">
  %faq;

  where

    x := major version
    y := minor version

NOTES:

==================================================================== -->

<!-- =============================================================== -->
<!-- Element declarations -->
<!-- =============================================================== -->

<!ELEMENT references (authors?, (part)+)>
<!ATTLIST references %common.att;>

    <!ELEMENT part (title, (item | part)+) >
    <!ATTLIST part %common.att;>

    <!ELEMENT item (%content.mix;)*>
    <!ATTLIST item %common.att;>

<!-- =============================================================== -->
<!-- End of DTD -->
<!-- =============================================================== -->
