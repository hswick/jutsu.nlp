(ns jutsu.nlp.tokenization
  (:import [org.deeplearning4j.text.tokenization.tokenizerfactory
            DefaultTokenizerFactory
            NGramTokenizerFactory]
           [org.deeplearning4j.text.tokenization.tokenizer.preprocessor
            CommonPreprocessor
            EndingPreProcessor
            LowCasePreProcessor
            StringCleaning]
           [org.deeplearning4j.text.tokenization.tokenizer
            DefaultTokenizer
            DefaultStreamTokenizer
            NGramTokenizer]
           [org.tartarus.snowball.ext PorterStemmer]))

(defn default-tokenizer-factory 
  ([] (DefaultTokenizerFactory.))
  ([preprocessor] 
   (let [t (DefaultTokenizerFactory.)]
     (.setTokenPreProcessor t preprocessor)
     t)))

(defn ngram-tokenizer-factory [tokenizer-factory min-n max-n]
  (NGramTokenizerFactory. tokenizer-factory min-n max-n))

(defn common-preprocessor []
  (CommonPreprocessor.))

(defn ending-preprocessor []
  (EndingPreProcessor.))

(defn lower-case-preprocessor []
  (LowCasePreProcessor.))

(defn strip-punct [base]
  (StringCleaning/stripPunct base))

(defn set-token-preprocessor! [tokenizer preprocessor]
  (.setTokenPreProcessor tokenizer preprocessor))

(defn common-stemmer-preprocessor []
  (proxy [CommonPreprocessor] []
    (preProcess [^String token]
      (let [prep (proxy-super preProcess token)
            stemmer (PorterStemmer.)]
           (.setCurrent stemmer prep)
           (.stem stemmer)
           (.getCurrent stemmer)))))

