(ns com.terry.meta-handlers.handlers1)

(defn ^{:get "/test.html"} test-page [request]
  {:body "test page" :status 200})

(defn ^{:get "/perf-test.html"} test-page2 [request]
  {:body "test page 2" :status 200})

(defn ^{:get #".*\.html"} test-page3 [request]
  {:body "test page 3" :status 200})

