(ns dl4clj-nlp.test
  (:require [jutsu.nlp.core :as nlp]
            [jutsu.nlp.sentence-iterator :as iter]
            [jutsu.nlp.tokenization :as token]
            [jutsu.nlp.util :as util]
            [clojure.test :refer :all]
            [clojure.tools.logging :as log]))

(def filepath (util/absolute-path "neuromancer.txt"))

(testing 'file-io
  (is (= true (.exists (clojure.java.io/file filepath)))))

(def w2v (nlp/word-2-vec "neuromancer.txt"))

(nlp/fit! w2v)

(defn w2v-test [w2v]
  (is (= org.deeplearning4j.models.word2vec.Word2Vec (class w2v)))
  (is (= 100 (count (nlp/words-nearest w2v "sky" 100))))
  (is (= true (nlp/has-word? w2v "sky")))
  (is (= true (not (nil? (nlp/get-word-vector w2v "sky")))))
  (is (= java.lang.Double (class (nlp/similarity w2v "sky" "city")))))

(deftest w2v-test0
  (w2v-test w2v))

(nlp/write-word-vectors w2v "data/neuromancer.csv")

(def w2v2 (nlp/read-word-vectors (clojure.java.io/file "data/neuromancer.csv")))

(deftest serialization-test
  (is (= true (.exists (clojure.java.io/file "data/neuromancer.csv"))))
  (w2v-test w2v2))

(def stopwords (nlp/stop-words))

(deftest stopwords-test
  (is (< 0 (.size stopwords)))
  (is (= true (not (nil? stopwords)))))

(def w2v3 (nlp/word-2-vec 
            (iter/default-iterator (util/absolute-path "neuromancer.txt"))
            (token/default-tokenizer-factory (token/common-stemmer-preprocessor))
            {:min-word-frequency 6
             :stopwords (nlp/stop-words)
             :window-size 10
             :layer-size 150}))
              
(nlp/fit! w2v3)

(deftest w2v3-test
  (w2v-test w2v3))

(def w2v4 (nlp/word-2-vec
            (iter/file-sentence-iterator (clojure.java.io/file (util/absolute-path "test_dir")))
            (token/default-tokenizer-factory)
            {:min-word-frequency 6
             :window-size 10
             :layer-size 150
             :stopwords (nlp/stop-words)}))

(nlp/fit! w2v4)

(deftest w2v4-test
  (w2v-test w2v4))

(def w2v5 (nlp/word-2-vec!
            (iter/file-sentence-iterator (clojure.java.io/file (util/absolute-path "test_dir")))
            (token/default-tokenizer-factory)
            {:min-word-frequency 6
             :window-size 10
             :layer-size 150
             :stopwords (nlp/stop-words)}))

(deftest w2v5-test
  (w2v-test w2v5))

(def w2v6 (nlp/word-2-vec!
            (iter/dir-iterator "test_dir")
            (token/default-tokenizer-factory)
            {:min-word-frequency 6
             :window-size 10
             :layer-size 150
             :stopwords (nlp/stop-words)}))

(deftest w2v6-test
  (w2v-test w2v6))

