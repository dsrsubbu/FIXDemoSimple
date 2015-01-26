(defproject fix-demo-simple "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [quickfixj/quickfixj-core "1.5.3"]
                 [quickfixj/quickfixj-fixt11 "1.5.3"]
                 [quickfixj/quickfixj-msg-fix50 "1.5.3"]
                 [org.slf4j/slf4j-api "1.6.3"]
                 [org.slf4j/slf4j-jdk14 "1.6.3"]
                 [org.apache.mina/mina-core "1.1.7"]
                 ]
  ; quickfixj is not in the standard maven repo.
  :repositories [["marketcetera" "http://repo.marketcetera.org/maven/"]]
  
  :main ^:skip-aot fix-demo-simple.core
  :source-paths ["src" "src/clojure"]
  :java-source-paths ["src/java"]
  :test-paths ["test" "test/clojure"]
  
  :target-path "target/%s"
  
  )
