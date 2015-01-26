(ns fix-demo-simple.core
  (:gen-class)
  (:import [com.fixdemosimple MessageSender MessageSender$CnnType]))

(defn run-provider
  "Run an application as a provider.\narg: MessageSender$CnnType/INITIATOR or MessageSender$CnnType/ACCEPTOR"
  [cnn-type]
  (if (= MessageSender$CnnType/INITIATOR cnn-type)
    (println "Running a provider as initiator...")
    (println "Running a provider as acceptor..."))
  (doto (new MessageSender cnn-type)
    (.run "./resources/settings-prov"))
  )

(defn run-requester
  "Run an application as a requester."
  []
  (println "Running a requester...")
  (doto (new MessageSender MessageSender$CnnType/INITIATOR)
    (.run "./resources/settings-req"))
  )

(defn -main
  "Run a simple FIX demo.\n\targ0: 'requester' | 'provider'\n\tif 'requester', arg1 can be 'initiator'"
  [& args]
  (if (or (= (count args) 0) (not (contains? #{"requester" "provider"} (first args))))
    (println "First argument should either be 'requester' or 'provider'.")
    (if (= (first args) "requester")
      (run-requester)
      (if (= (second args) "initiator")
        (run-provider MessageSender$CnnType/INITIATOR)
        (run-provider MessageSender$CnnType/ACCEPTOR)))
    )
  )

