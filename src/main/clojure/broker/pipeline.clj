(ns broker.pipeline
  (:require [broker.routing :as routing]
            [broker.filters :as filters]
            [broker.transform :as transform]
            [broker.handlers :as handlers])
  (:import (org.slf4j LoggerFactory)))

(def logger (LoggerFactory/getLogger "broker.pipeline"))

(defn process [msg]
  (try
    (.info logger (str "[ >>> RECEIVED: " msg + " <<< ]\n"))

    (if-not (filters/valid? msg)
      (do
        (.warn logger "Message invalid")
        (throw (ex-info "Invalid message" {:msg msg})))

      (let [msg* (transform/enrich msg)
            dest (routing/route msg*)
            handler (get handlers/handlers dest)]

        (.info logger (str "Destination: " dest))
        (.info logger (str "Available handlers: " (keys handlers/handlers)))

        (if handler
          (do
            (handler msg*)
            (.info logger "[ >>> HANDLED SUCESSFULLY <<< ]"))
          (throw (ex-info "Handler not found" {:dest dest})))))

    (catch Exception e
      (.error logger "Pipeline error" e)
      (throw e))))