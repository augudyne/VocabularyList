<div id="table-of-contents">
<h2>Table of Contents</h2>
<div id="text-table-of-contents">
<ul>
<li><a href="#sec-1">1. Vocabulary List</a>
<ul>
<li><a href="#sec-1-1">1.1. Feature</a>
<ul>
<li><a href="#sec-1-1-1">1.1.1. User Experience: Type a word to add to my Vocabulary List. Get, save, and show me the definition of the word.</a></li>
<li><a href="#sec-1-1-2">1.1.2. User Experience: If I mispell a word when adding it, try and get me word suggestions that I can easily confirm and add.</a></li>
<li><a href="#sec-1-1-3">1.1.3. User Experience: Sometimes I don't have internet, I want it to automatically save the word for me to add later if no internet is detected.</a></li>
<li><a href="#sec-1-1-4">1.1.4. User Experience: I want to be able to backup my offline database, which can be transferred and loaded to VocabularyList on a seperate device.</a></li>
<li><a href="#sec-1-1-5">1.1.5. User Experience: I want to share a word and its definition with a friend through E-mail/SMS/Facebook etc.</a></li>
<li><a href="#sec-1-1-6">1.1.6. User Experience: I want to learn new words. Send me a daily notification with a random word in my list.</a></li>
</ul>
</li>
<li><a href="#sec-1-2">1.2. Future Updates</a></li>
</ul>
</li>
</ul>
</div>
</div>

# Vocabulary List<a id="sec-1" name="sec-1"></a>

A Vocabulary List that allows user to type a word, and automatically fetch definition from Webster API. Allows offline list and viewing.

## Feature<a id="sec-1-1" name="sec-1-1"></a>

### User Experience: Type a word to add to my Vocabulary List. Get, save, and show me the definition of the word.<a id="sec-1-1-1" name="sec-1-1-1"></a>

Simply add the word you want. Queries the Merriam-Webster API for a match. If it is found, it will add the
word to the database (vocabulary list) and produce the word's info fragment, adding the main screen to the backStack

### User Experience: If I mispell a word when adding it, try and get me word suggestions that I can easily confirm and add.<a id="sec-1-1-2" name="sec-1-1-2"></a>

If the word is not found, it will present a new fragment of 'word suggestions' offered by Marriam-Webster API. Selecting a word will prompt a confirmation dialog box to add the word, returning you to the mainScreen and displays the word's info page fragment.

### User Experience: Sometimes I don't have internet, I want it to automatically save the word for me to add later if no internet is detected.<a id="sec-1-1-3" name="sec-1-1-3"></a>

If the URL is not found (bad HTTP connection a.k.a. students can't afford data plans), it will add the word to an "offline list". This can be accessed through the default menu dialog in the top right corner. When a word is selected from the offline list, it will go through the same control flow as a normal word addition (by leveraging the same singleton IO provider written for ). If it succesfully adds the last word in the "offline list", it will force popBackstack() back to the mainScreen.

### User Experience: I want to be able to backup my offline database, which can be transferred and loaded to VocabularyList on a seperate device.<a id="sec-1-1-4" name="sec-1-1-4"></a>

As a backup (because I sometimes debug by running it on my phone, and I also use the app regularly so wiping the database is a real concern), it is possible to export the entire JSON database (as it is stored) via a SendText intent - works with any native E-mail/Messaging app.

### User Experience: I want to share a word and its definition with a friend through E-mail/SMS/Facebook etc.<a id="sec-1-1-5" name="sec-1-1-5"></a>

It is also possible (and more reasonably) to share a single word. By long-pressing on a word item, a drop-down menu is displayed that offers delete and share options. The share option performs a SendTextIntent with a cleaned version of the Word's JSON (includes it's seperate variants and different parts of speech)

### User Experience: I want to learn new words. Send me a daily notification with a random word in my list.<a id="sec-1-1-6" name="sec-1-1-6"></a>

Leverages the Android Alarm and BroadcastListener interfaces to randomly select a word from the library to show as a notification to the user at 5:30am (as a NON-WAKE notification). The runOnStartup (after restart/reboot) broadcast is not functioning properly (hypotheses: lack of permissions or improper Alarm start flag).

## Future Updates<a id="sec-1-2" name="sec-1-2"></a>

1.  A fix for the alarm event on reboot
2.  A more refined word of the day selection algorithm

Currently, it is pseudo-random, which isn't the greatest way to learn new words. Rather than a 'Word of the Day' inspiration, it should be more of a "learn a word that you want to use". For this purpose, the several following features are considered:
-   Set 'priority' of a word when it is added, by a simple slider from 0 - 100% that would be stored with the Word Object Abstraction
-   Naively, we could try to reduce reoccurences by also tracking the occurences of selection for a given word (a long run total). However, it might be useful to have duplicates since the direction is "learn a word I want to use".
-   **Machine Learning** I have little experience in actually implementing my own versions of these ideas. However, I have several ideas (if anyone is actually reading this, I feel bad for you because I don't see why you would want to read my ramblings. I hope you're paid right now), but more experienced suggestions are most welcome! I can add Notification response, allowing me to 'like/dislike' (rating system would be even better) a given word. This can give me a good basis for mapping my own personal preferences. The idea is to derive a loss function for the performance of the selection. The inputs can be seen as - part of speech, number of variants, the type of variant (are they different parts of speech? Are they phrases?), sentiment analysis, topic analysis (usage contexts) etc.

<div id="footnotes">
<h2 class="footnotes">Footnotes: </h2>
<div id="text-footnotes">

<div class="footdef"><sup><a id="fn.1" name="fn.1" class="footnum" href="#fnr.1">1</a></sup> <p>DEFINITION NOT FOUND.</p></div>


</div>
</div>