# SimpleNLG-EnFr
SimpleNLG-EnFr 1.1 is a bilingual English/French adaption of [SimpleNLG v4.2](http://code.google.com/p/simplenlg/).

SimpleNLG is a Java library for text surface realization in English. It was originally developed by Ehud Reiter, Albert Gatt and Dave Westwater, of Aberdeen University. Realization is the last step in the text generation process, and so SimpleNLG can be useful to programmers writing an application relating to text generation.

SimpleNLG-EnFr can realize text in both English and French in the same document. The French part covers practically all the grammar in [Le français fondamental (1er degré)](http://fr.wikipedia.org/wiki/Fran%C3%A7ais_fondamental) and has a 3871 entry lexicon covering the [échelle orthographique Dubois Buyse](http://o.bacquet.free.fr/db2.htm).

The English grammatical coverage of SimpleNLG-EnFr is the same as that of SimpleNLG v4.2. Its French grammatical coverage is equivalent to its English one. Compared to version 1.0, version 1.1 of SimpleNLG-EnFr adds the following grammatical points to the French part:

  *  interrogative and relative clauses

  *  conditional and subjonctive moods

  *  ordinal adjectives recognition

A detailed manual in French is included.

The code had to be reorganized so as to separate what was specific to English from what was more generic, before adding the French grammar. The API is almost identical, though. 

For more info, see [Pierre-Luc Vaudry's page](http://www-etud.iro.umontreal.ca/~vaudrypl/snlgbil/snlgEnFr_english.xhtml).
