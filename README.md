# Movie-Rockerz

Vector space model:

        It is an algebraic model for representing text documents (and any objects, in general) as vectors of identifiers, such as, for example, index terms. It is used in information filtering, information retrieval, indexing and relevancy rankings.

What we’ll learn today

How to calculate a “similarity” between two documents

Therefore, given a document, how to find the most similar document to it in the corpus

The term-document matrix (TDM):

        In the TDM, rows represent documents, columns represent terms (in the collection vocabulary). Cell values are term frequency counts or more generally “score” attached to a term for a document. The TDM is a handy conceptual representation (though, as we’ll see later, not always a suitable implementation). But it also suggests a useful way of modelling and computing with documents i.e.by modelling documents as points (vectors) in multi-dimensional term space.


Documents in term space:

We can transfer the same model to modelling documents as terms. Each term becomes a dimension. The document’s position in the dimension of term t is determined by sd,t (score of term t in document d – for now, just the term frequency). Each document becomes a point in |T|-d term space.


Document similarity as (inverse):

        We can then answer, “how similar are two documents?” by answering “how close are they in term space?” using by Euclidean distance.

Note: The computation extends trivially to multiple dimensions (though again it’s difficult to visualize)


Document length bias: 

        Measuring literal distance between documents in term space has a problem. Documents with lots of terms will be further from origin. Documents with few terms closer to it. So we’ll find all short documents relatively similar even if they’re unrelated


Angular distance:

        To avoid the length issue, we instead treat the documents as “vectors” i.e. as lines from the origin to their locations in term space and then measure their similarity by the angle between the vectors.


Cosine distance:

         In fact, what we measure is the cosine of the angle. Larger cosine = closer together (a similarity measure) I All term values positive, so cosine always between [0, 1]. Also, there’s a simple trick for calculating cosine easily


Calculating cosine distance If we have two vectors A and B then the cosine between them is


cos (A, B) = A · B |A| × |B|


where A • B is the “dot product” (explained next) and |A| and |B| are the length of the vectors.

In fact, if we normalize the vectors so that they have unit length (i.e. they’re 1 unit long), as a = A/|A| and b = B/|B|, then: cos (A, B) = a · b

 

Speeding up cosine computation:

        All document vectors are stored normalized to unit length i.e. normalization factors precomputed and stored. Cosine distance can be quickly calculated as a summation of multiplications. Only dimensions in which both elements are non-zero need to be calculated. This is known as the vector space model.

Down-weighting common terms:

        Terms that occur in a lot of documents tend to be less discriminative than terms that appear in fewer. For instance, the document “The clown” is more distinctively about clown than it is about stories. But (perversely) frequent terms often have more impact upon similarity comparisons.


Inverse document frequency:

        A commonly-used measure of a term’s discriminative power is its inverse document frequency or IDF. If N is the number of documents in the corpus, and dft is the number of documents that term t appears in. Then the IDF of t is defined as


idft = log N dft

TF*IDF:

        The weight of a term’s appearance in a document is frequently calculated by combining the terms frequency or TF in the document with its inverse document frequency (IDF).

​

wt,d = tfd,t ∗ idft

​

Querying in the Vector Space Model:

​

         The query can be interpreted as a vector. E.g., for q=vector, space, model. We have terms vector, space and model all with tf=1. Therefore set wt,q = 1 for t ∈ {vector, space, model}. Now, given a document, we can compute cosine similarity


cos (q, d) = q . d ( | q | X | d | )


where Q is the query vector and D is the document vector


Developing Basic search engine using vector space model:

Data set: https://www.kaggle.com/tmdb/tmdb-movie-metadata

Columns in the data set used for search feature are

              1. Name of the movie.

              2. Overview of the movie.

              3. Tags of the movie.


The project provides a search box for input from the user. Once the search button is clicked search algorithm is run on the input data and related results will be shown to the user. The search algorithm is implemented in two phases.

 
Phase 1 (Preload): This is done only once i.e. at the time of application boot up.


Name, overview and tags of all the movies from the data-set are fetched .

Then name, overview and tags of each movie are concatenated as one string and the entire string is split into each individual word and stored in a separate array for each movie and then the array is mapped to corresponding movie id.

Then we remove stop words from each array by iterating over all the arrays.

Then we use porter’s steaming algorithm to get the steam form of each word by iterating over all the words in all the arrays.

Then we form a list of all the unique terms and determine tf and idf values of these unique words.

Then we give vector representation of each movie by calculating tf – idf value of each term corresponding to that movie.

Phase 2 (Search): This is done for each search request.

We now calculate the tf of each term and in the search query.

Then we remove stop words from each array by iterating over all the arrays.

Then we use porter’s steaming algorithm to get the steam form of each word by iterating over all the words in all the arrays

Then we get the idf values from the phase 1 for each term in the search query.

Then we give vector representation of the search query by calculating tf-idf score.

Then we compute the cosine similarity of the search query with respect to each document i.e. movie (name and overview).

We store all the non-zero cosine similarity scores and rank them in descending order and show them to the user.


Developing basic classifier using Naive-Bayes algorithm:​

​​

Data set: https://www.kaggle.com/tmdb/tmdb-movie-metadata

Columns in the data set used for classifier feature are

              1. Name of the movie.

              2. Overview of the movie.

              3. Tags of the movie.

              4. Genre of the movie.

The project provides a text box for input from the user. Once the classify button is clicked classifier algorithm is executed on the input data and related results will be shown to the user. The classifier algorithm is implemented in two phases.

Phase 1 (Preload): This is done only once i.e. at the time of application boot up.

Name, overview and tags of all the movies from the data-set are fetched .

Then name, overview and tags of each movie are concatenated as one string and the entire string is split into each individual word and stored in a separate array for each movie and then the array is mapped to corresponding movie id.

Then we remove stop words from each array by iterating over all the arrays.

Then we use porter’s steaming algorithm to get the steam form of each word by iterating over all the words in all the arrays.

When the retrieve genres of each movie and store them in each array. Then map the genre arrays to corresponding movieId.

We also get all the genres from the data-set and store them in a set.

We not create a map containing genre as the key and list of terms appeared in the genre from the data-set.

Then we calculate the probability of genre by using this formula i.e.​

P = No. of terms belonging to that genre / Total no of terms in the data -set

Now we calculate the term frequency of the each term in the data-set and then calculate probability of term give genre i.e P( T | G )             using conditional probability formula (Note we apply smoothing here i.e. increment term frequency counter by 1 for each term).

Now we store P( T | G) values in a map.

Phase 2 (Classify): This is done for each classify request.

We now calculate the term frequency of each term and in the classify query.

Then we remove stop words from each array by iterating over all the arrays.

Then we use porter’s steaming algorithm to get the steam form of each word by iterating over all the words in all the arrays

Then we get the P(T | G) values from the phase 1 for each term in the classify query.

We now calculate P(G | T) value for each term using Bayer's Rule i.e.

         P(G | T) = (P(T | G)P( G) ) / P( T ) , here we ignore P( T )
    
We know add score for each term in the classify query. Repeat, step 5, 6 for all genres.

We store now store top 3 scores and display the corresponding genres to the user.

Technologies used: Java 1.8, Spring boot, Thyme leaf, bootstrap, Apache openNlp, AWS EC2, AWS S3.

Website Url: Movie Rockerz

Code: https://github.com/tanveer3567/Movie-Rockerz/
