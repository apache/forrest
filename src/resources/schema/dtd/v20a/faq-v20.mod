<!-- ===================================================================

     Apache Faq module (Version 2.0)

TYPICAL INVOCATION:

  <!ENTITY % faq PUBLIC
      "-//APACHE//ENTITIES FAQ Vxy//EN"
      "faq-vxy.mod">
  %faq;

  where

    x := major version
    y := minor version

NOTES:

AUTHORS:
  Steven Noels <stevenn@apache.org>

FIXME:

CHANGE HISTORY:
[Version 2.0a]
  20030506 Changed <part> to <faqsection>
  20030506 Changed @title on <faqs> to a nested <title> element

COPYRIGHT:
  Copyright (c) 2002 The Apache Software Foundation.

  Permission to copy in any form is granted provided this notice is
  included in all copies. Permission to redistribute is granted
  provided this file is distributed untouched in all its parts and
  included files.

==================================================================== -->

<!-- =============================================================== -->
<!-- Element declarations -->
<!-- =============================================================== -->

<!ELEMENT faqs (title?, authors?, (faq|faqsection)+)>
<!ATTLIST faqs %common.att;>

    <!ELEMENT faqsection (title, (faq | faqsection)+) >
    <!ATTLIST faqsection %common.att;>

    <!ELEMENT faq (question, answer)>
    <!ATTLIST faq %common.att;>

        <!ELEMENT question (%content.mix;|elaboration)*>
        <!ATTLIST question %common.att;>

        <!ELEMENT elaboration (%content.mix;)*>
        <!ATTLIST elaboration %common.att;>

        <!ELEMENT answer (%flow;)*>
        <!ATTLIST answer author IDREF #IMPLIED>

<!-- =============================================================== -->
<!-- End of DTD -->
<!-- =============================================================== -->
