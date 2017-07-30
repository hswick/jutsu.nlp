(ns jutsu.nlp.sentence-iterator
  (:require [jutsu.nlp.util :refer [absolute-path]])
  (:import [org.deeplearning4j.text.sentenceiterator
            BasicLineIterator
            CollectionSentenceIterator
            FileSentenceIterator
            LineSentenceIterator]))

(defn default-iterator [input]
  (BasicLineIterator. input))

(defn default-iterator [in-file-name]
  (LineSentenceIterator. (clojure.java.io/file in-file-name)))

(defn basic-line-iterator [input]
  (BasicLineIterator. input))

(defn collection-sentence-iterator
  ([coll] (CollectionSentenceIterator. coll))
  ([coll pre-processor] (CollectionSentenceIterator. coll pre-processor)))

(defn file-sentence-iterator
  ([dir] (FileSentenceIterator. dir))
  ([pre-processor file] (FileSentenceIterator. pre-processor file)))

(defn dir-iterator
  ([path-to-dir] (file-sentence-iterator (clojure.java.io/file (absolute-path "test_dir")))))
