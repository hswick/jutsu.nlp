(ns jutsu.nlp.core
  (:require [jutsu.nlp.sentence-iterator :refer :all]
            [jutsu.nlp.tokenization :refer :all]
            [jutsu.nlp.util :refer :all]
            [jutsu.matrix.core :as m])
  (:import [org.datavec.api.util ClassPathResource]
           [org.deeplearning4j.models.word2vec Word2Vec$Builder]
           [org.deeplearning4j.models.embeddings.loader WordVectorSerializer]
           [org.deeplearning4j.text.stopwords StopWords]))

(defn word-2-vec-builder []
  (Word2Vec$Builder.))

(defn stop-words []
  (StopWords/getStopWords))

(defn word-2-vec
  ([filename] (word-2-vec filename {}))
  ([filename options] (word-2-vec (default-iterator (absolute-path filename))
                        (let [t (default-tokenizer-factory)]
                          (set-token-preprocessor! t (common-preprocessor))
                          t)
                        options))
  ([iterator tokenizer-factory options]
   (let [options (merge {:min-word-frequency 5
                         :iterations 1
                         :layer-size 100
                         :seed 42
                         :window-size 5
                         :stopwords nil}
                   options)]
     (-> (Word2Vec$Builder.)
         (.minWordFrequency (options :min-word-frequency))
         (.iterations (options :iterations))
         (.layerSize (options :layer-size))
         (.seed (options :seed))
         (.windowSize (options :window-size))
         (.iterate iterator)
         ((fn [builder]
           (if (options :stopwords)
             (.stopWords builder (options :stopwords))
             builder)))
         (.tokenizerFactory tokenizer-factory)
         (.build)))))

(defn fit! [model]
  (.fit model))

(defn word-2-vec!
  "Word 2 vec trained upon initialization"
  ([filename] 
   (let [w2v (word-2-vec filename)]
        (fit! w2v)
        w2v))
  ([filename options]
   (let [w2v (word-2-vec filename options)]
        (fit! w2v)
        w2v))
  ([iterator tokenizer-factory options]
   (let [w2v (word-2-vec iterator tokenizer-factory options)]
     (fit! w2v)
     w2v)))

(defn write-word-vectors
  ([model output]
   (WordVectorSerializer/writeWordVectors model output))
  ([lookup-table cache path]
   (WordVectorSerializer/writeWordVectors lookup-table cache path)))

(defn join
  "Utility function for concatenating words together"
  ([words] (join words " "))
  ([words delimiter]
   (reduce #(str %1 delimiter %2) words)))

(defn get-word-vector 
  "Returns a ND4J Array as the word vector"
  [w2v word]
  (.getWordVectorMatrix w2v word))

(defn read-word-vectors [vectors-file]
  (WordVectorSerializer/loadTxtVectors vectors-file))

(defn words [model]
  (map (fn [token] (.getWord token)) (.tokens (.vocab model))))

(defn has-word? [model word]
  (.hasWord model word))

(defn similarity [model word word2]
  (.similarity model word word2))

(defn words-nearest [model words top] 
  (.wordsNearest model words top))

(defn word-vectors [model]
  (.getSyn0 (.lookupTable model)))

;;Doc2Vec
(defn doc-2-vec [w2v document]
  (let [sum (m/zeros 1 (m/jutsu-cols (word-vectors w2v)))]
    (doseq [word (clojure.string/split document #" ")]
      (let [word-vector (get-word-vector w2v word)]
        (when (not (nil? word-vector))
          (m/add! sum word-vector))))
    sum))
