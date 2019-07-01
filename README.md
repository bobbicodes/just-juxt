# just-juxt

So far I've got the GitHub part done and running as a webservice on Heroku:

[Try it](https://just-juxt.herokuapp.com/)

(Patience... free Heroku dyno takes a minute to "wake up")

It simply returns the outer `juxt` expression and url of the most recent github entry, without any validation.
Functions are in place for fetching full source code and parsing 5 different levels of nested S-expressions.

Twitter API part is done, made a successful test post.

What is left is to refine the parsing/posting strategy as to provide the optimally pleasant and educational (i.e. non-spammy) experience. Details to work out before launch are:

* Posting frequency (once an hour? 6 hours? once per day?)
* Tweet content, i.e. selecting and trimming results

While I give some time to consider the above, I've gone ahead and set up a postgresql database with [next.jdbc](https://github.com/seancorfield/next-jdbc/) with which to store our juxts, and a hiccup page that lists all the urls.
