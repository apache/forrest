
<!ELEMENT faqs (authors?, (faq|part)+)>
<!ATTLIST faqs %common.att;
               %title.att;>

    <!ELEMENT part (title, (faq | part)+) >
    <!ATTLIST part %common.att;>

    <!ELEMENT faq (question, answer)>
    <!ATTLIST faq %common.att;>

        <!ELEMENT question (%content.mix;)*>
        <!ATTLIST question %common.att;>

        <!ELEMENT answer (%blocks;)*>
        <!ATTLIST answer author IDREF #IMPLIED>
