
<!ELEMENT faqs (authors?, (faq|part)+)>
<!ATTLIST faqs %common.att;
               %title.att;>

    <!ELEMENT part ((faq | part)+) >
    <!ATTLIST part %title.att; %common.att;>

    <!ELEMENT faq (question, answer)>
    <!ATTLIST faq %common.att;>

        <!ELEMENT question (%content.mix;)*>
        <!ATTLIST question %common.att;>

        <!ELEMENT answer (%blocks;)*>
        <!ATTLIST answer author IDREF #IMPLIED>
