(ns com.terry.meta-handlers.front-controller
  (:require [clojure.tools.logging :as log]))

(def mappings (atom nil))
(def http-methods [:get :post :head :put :delete :trace :options])

(defn- has-http-method
  "does the meta-data have a http method as a key?, if so return it, otherwise return nil"
  [meta-data]
  (let [v (first meta-data)
        http-method (if v (key v) nil)]
    (when (and http-method
               (some #(= http-method %) http-methods))
        http-method)))

(defn- controller?
  "Is the specific function eligible to be a controller?"
  [item]
  (let [value (val item)
        var (var-get value)]
    (when (fn? var)
      (let [meta-data (meta value)
            http-method (has-http-method meta-data)]
        (when http-method
          ;(get meta-data http-method)
          (assoc {:function var} :meta-data {http-method (get meta-data http-method)})
          ;nil
          )))))

(defn- create-mapping
  "Create a controller mapping from a suitable controller"
  [item]
  (when-let [controller-data (controller? item)]
    controller-data))

(defn- scan-namespace [namespace]
  (let [ns-items (ns-publics namespace)]
    (map create-mapping ns-items)))

(defn- scan-namespaces
  "Finds all controller mappings in a namespace"
  [namespaces]
  (doall (remove nil? (reduce concat (map scan-namespace namespaces)))))

(defn- handler-match
  "Find a controller mapping that matches the specified http method and url"
  [url http-method mapping]
  (when-let [mapping-url (http-method (:meta-data mapping))]
    (when (= mapping-url url)
      (:function mapping))
    (when (and (instance? java.util.regex.Pattern mapping-url) (re-matches mapping-url url))
      (:function mapping))))

(defn- find-handler
  "find a handler that matches the request using the specified mappings"
  [request mappings]
  (let [uri (:uri request)
        http-method (:request-method request)
        matched-handler (some #(handler-match uri http-method %) mappings)]
    matched-handler))

(defn- sort-key-fn
  "Uses the mapping as the sort key"
  [mapping]
  (let [url-path (val (first (:meta-data mapping)))]
    url-path))

(defn- sort-comparator [a b]
  (let [a-re (instance? java.util.regex.Pattern a)
        b-re (instance? java.util.regex.Pattern b)]
    (if (and (not a-re) b-re)
      true
      false)))

(defn- sort-mappings
  "sort mappings so that regexs are tested after literals"
  [mappings]
  (sort-by sort-key-fn (comparator sort-comparator) mappings))

(defn- get-mappings
  "returns all handler mappings, caching can be specified to speed up subsequent requests"
  [namespaces cache?]
  (if cache?
    (let [existing-mappings @mappings]
      (if (= nil existing-mappings)
        (let [new-mappings (sort-mappings (scan-namespaces namespaces))
              modified (compare-and-set! mappings nil new-mappings)]
          @mappings)
        existing-mappings))
    (sort-mappings (scan-namespaces namespaces))))

(defn- log-no-handler [request mappings]
  (let [uri (:uri request)
        http-method (:request-method request)]
    (log/warn (str "Unable to match handler for " http-method " " uri " mappings: " mappings))))

(defn handle
  "Handle an incomming request by finding an appropriate mapping"
  [request namespaces & {:keys [cache?] :or {cache? true}}]
  (let [mappings (get-mappings namespaces cache?)
        matched-handler (find-handler request mappings)]
    (if matched-handler
      (matched-handler request)
      (log-no-handler request mappings))))

(defn flush-cache!
  "flushes the controller mappings cache"
  []
  (reset! mappings nil))

(defn perf-test [n cache?]
  (let [req {:uri "/perf-test-dhdfh.html" :request-method :get}
        nses ['com.terry.meta-controllers.integration.web 'com.terry.meta-controllers.integration.web2]]
    (time (doall (for [i (range n)] (handle req nses :cache? cache?))))
    0
  ))

;(def request {:uri "/test.html" :request-method :get})
;(handle request 'com.terry.metadata-controllers.integration.web)

;(handle {:url "/test.js" :request-method :get}
;        ['com.terry.metadata-controllers.integration.web
;         'com.terry.metadata-controllers.integration.web2]
;        :cache? false)