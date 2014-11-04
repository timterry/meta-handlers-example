(ns com.terry.meta-handlers.handlers2)

(defn ^{:get "/test4.html"} test-page4 [request]
  {:body "test page 4" :status 200})

(defn ^{:post #"/.+\.js"} test-page5 [request]
  {:body "test page 5" :status 200})
