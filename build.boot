(set-env!
  :resource-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [nightlight "1.7.0" :scope "test"]
                  [adzerk/boot-test "1.2.0" :scope "test"]
                  [org.nd4j/nd4j-native-platform "0.8.0" :scope "test"]
                  [org.deeplearning4j/deeplearning4j-nlp "0.8.0"]
                  [org.datavec/datavec-api "0.8.0"]
                  [org.apache.lucene/lucene-snowball "3.0.3"]
                  [hswick/jutsu.matrix "0.0.14"]]
  :repositories (conj (get-env :repositories)
                      ["clojars" {:url "https://clojars.org/repo"
                                  :username (System/getenv "CLOJARS_USER")
                                  :password (System/getenv "CLOJARS_PASS")}]))

(task-options!
  jar {:main 'jutsu.nlp.core
       :manifest {"Description" "jutsu.nlp is meant to do natural language processing tasks such as word embedding."}}
  pom {:version "0.1.0"
       :project 'hswick/jutsu.nlp
       :description "jutsu.nlp is meant to do natural language processing tasks such as word embedding."
       :url "https://github.com/hswick/jutsu.nlp"}
  push {:repo "clojars"})

(deftask deploy []
  (comp
    (pom)
    (jar)
    (push)))

;;So nightlight can still open even if there is an error in the core file
(try
  (require 'jutsu.nlp.core)
  (catch Exception e (.getMessage e)))

(require
  '[nightlight.boot :refer [nightlight]]
  '[adzerk.boot-test :refer :all])

(deftask night []
  (comp
    (wait)
    (nightlight :port 4000)))

(deftask testing [] (merge-env! :source-paths #{"test" "data"}) identity)

(deftask test-code
  []
  (comp
    (testing)
    (test)))
