(ns mount-unmount-test.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(def app-state (atom {:text "Hello world!"}))

(defn test-component [data owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (println "I have mounted"))

    om/IRender
    (render [_]
      (dom/div nil (:yar data)))

    om/IWillUnmount
    (will-unmount [_]
      (println "I have unmounted"))))

(defn other-test [data owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (println "I have mounted2"))

    om/IRender
    (render [_]
      (dom/div nil (:text data)))

    om/IWillUnmount
    (will-unmount [_]
      (println "I have unmounted2"))))

(defn app-component [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
               (om/build other-test (:ok data))
               (if (:child-component data)
                 (om/build test-component (:child-component data))
                 (dom/strong nil "Not building child component"))))))

(defn change-state-atom [app-state]
  (.setInterval js/window
                #(if (get @app-state :child-component)
                   (reset! app-state {:nope "nope"})
                   (reset! app-state {:child-component {:yar "Hello, child component here."}
                                      :ok {:text  "yes"}}))
                1000))

(change-state-atom app-state)

(om/root
  app-component
  app-state
  {:target (. js/document (getElementById "app"))})
