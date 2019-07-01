# just-juxt

So far I've got the GitHub part done and running as a webservice on Heroku:

[Try it](https://just-juxt.herokuapp.com/)

(Patience... free Heroku dyno takes a minute to "wake up")

And I've got the Twitter API part done, made a successful test post.

What is left is to refine the parsing/posting strategy as to provide the optimally pleasant and educational (i.e. non-spammy) experience. Details to work out before launch are:

* Posting frequency (once an hour? 6 hours? once per day?)
* Tweet content, i.e. how much of the example to show. Currently set to the "outer juxt", i.e. 2 sets of parens, but would like to show more than that if possible.
