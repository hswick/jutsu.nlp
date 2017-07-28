# jutsu.nlp

Clojure library meant for word embedding using the deeplearning4j.

Currently still in experimental stage and api subject to change.

Pull requests are welcome!

# Usage

To use jutsu.nlp:
```clojure
(:require '[jutsu.nlp.core :as nlp]
		  '[jutsu.nlp.util :as util])

;;Configure your Word2Vec model
(def w2v (nlp/word-2-vec "path/to/text-file" 
{:min-word-frequency 5;;You can also input an option map
	:iterations 1		  ;;To set certain parameters
    :layer-size 100
    :seed 42
    :window-size 5}))

;;This trains the model on the data given
(nlp/fit! w2v)

;;Write the word2vec model to memory
(nlp/write-word-vectors w2v "word_vectors.csv")

;;Load a word2vec model from memory
(def w2v-2 (nlp/read-word-vectors "word_vectors.csv"))

;;If you want stopping and stemming initialize word2vec like this
(nlp/word-2-vec 
 (iter/default-iterator (util/absolute-path "neuromancer.txt"))
 (token/default-tokenizer-factory (token/common-stemmer-preprocessor))
 {:min-word-frequency 6
  :stopwords (nlp/stop-words)
  :window-size 10
  :layer-size 150})
  
(require '[jutsu.nlp.sentence-iterator :as iter]
		 '[jutsu.nlp.tokenization :as token])
  
;;If you want to input a directory initialize like this
(def w2v4 (nlp/word-2-vec
            (iter/dir-iterator "path/to/dir")
            (token/default-tokenizer-factory)
            {:min-word-frequency 6
             :window-size 10
             :layer-size 150
             :stopwords (nlp/stop-words)}))
```

# Dev

Run `boot night` to startup nightlight and begin editing your project in a browser.

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.