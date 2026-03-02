(ns broker.log
  (:import (org.slf4j LoggerFactory)))

(defn logger [name]
  (LoggerFactory/getLogger name))