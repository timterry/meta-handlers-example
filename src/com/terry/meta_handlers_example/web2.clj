(ns com.terry.meta-handlers-example.web2)

(defn ^{:get "/test3.html"} test-page3 [request]
  {:body "hello3" :status 200})

(defn ^{:post #"/.+\.js"} test-page4 [request]
  {:body "hello regex3" :status 200})
