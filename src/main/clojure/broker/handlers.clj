(ns broker.handlers)

(def handlers
  {:orders (fn [msg] (println "Order handled:" msg))
   :default (fn [msg] (println "Default handler:" msg))})