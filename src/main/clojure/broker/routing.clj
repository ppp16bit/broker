(ns broker.routing)

(def routes
  {"order.created" :orders})

(defn route [msg]
  (get routes (:routing-key msg) :default))