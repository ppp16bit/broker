(ns broker.filters)

(defn valid? [msg]
  (some? (.payload msg)))