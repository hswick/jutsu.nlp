(ns jutsu.nlp.util
  (:import [org.datavec.api.util ClassPathResource]))

(defn absolute-path [filename]
    (-> (ClassPathResource. filename)
      (.getFile)
      (.getAbsolutePath)))
