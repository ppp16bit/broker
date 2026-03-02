(ns broker.transform)

(defn enrich [msg]
  (-> {:id        (.id msg)
       :routingKey (.routingKey msg)
       :payload    (.payload msg)
       :timestamp  (.timestamp msg)}
      (assoc :enriched-at (System/currentTimeMillis))))