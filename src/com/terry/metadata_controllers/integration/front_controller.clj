(ns com.terry.metadata-controllers.integration.front-controller
  (:require [clojure.tools.logging :as log]))
(def mappings (atom nil))

(defn has-http-method? [meta]
  (contains? meta :get))

(defn controller?
  "Is the specifiec function eligible to be a controller?"
  [item]
  (let [value (val item)
        var (var-get value)]
    (when (fn? var)
      (let [meta-data (meta value)]
        (when (has-http-method? meta-data)
          (assoc {:function var} :meta-data {:get (:get meta-data)}))))))

(defn create-mapping
  "Create a controller mapping from a suitable controller"
  [item]
  (when-let [controller-data (controller? item)]
    controller-data))

(defn scan-namespace [namespace]
  (let [ns-items (ns-publics namespace)]
    (map create-mapping ns-items)))

(defn scan-namespaces
  "Finds all controller mappings in a namespace"
  [namespaces]
  (doall (reduce concat (map scan-namespace namespaces))))

(defn handler-match
  "Find a controller mapping that matches the specified http method and url"
  [url http-method mapping]
  (when-let [mapping-url (http-method (:meta-data mapping))]
    (when (= mapping-url url)
      (:function mapping))))

(defn find-handler [request mappings]
  (let [uri (:uri request)
        http-method (:request-method request)
        matched-handler (some #(handler-match uri http-method %) mappings)]
    matched-handler))

(defn get-mappings
  "returns all handler mappings, caching can be specified to speed up subsequent requests"
  [namespaces cache?]
  (if cache?
    (let [existing-mappings @mappings]
      (if (= nil existing-mappings)
        (let [new-mappings (scan-namespaces namespaces)
              modified (compare-and-set! mappings nil new-mappings)]
          @mappings)
        existing-mappings))
    (scan-namespaces namespaces)))

(defn handle
  "Handle an incomming request by finding an appropriate mapping"
  [request namespaces & {:keys [cache?] :or {cache? true}}]
  (let [mappings (get-mappings namespaces cache?)
        matched-handler (find-handler request mappings)]
    (matched-handler request)))

(defn perf-test [n cache?]
  (let [req {:uri "/perf-test.html" :request-method :get}
        nses ['com.terry.metadata-controllers.integration.web]]
    (time (doall (for [i (range n)] (handle req nses :cache? cache?))))
    0
  ))

;(def request {:uri "/test.html" :request-method :get})
;(handle request 'com.terry.metadata-controllers.integration.web)