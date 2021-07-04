(ns skattelisten-frontend.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]))

;; -------------------------
;; Views

(defn search-bar []
  [:div.search-bar [:input.search-bar__input]])

(defn link-container []
  [:div.links])

(defn site-name [name]
  [:p.site-name name])

(defn header-menu []
  [:header
   [site-name "Skattelisten"]
   [search-bar]
   [link-container]])

(defn home-page []
  [:div
   [:table
    [:tr
     [:th "CVR"]
     [:th "Name"]
     [:th "Type"]
     [:th "Year"]
     [:th "Taxable income"]
     [:th "Deficit"]
     [:th "Corporate Tax"]]
    [:tr
     [:td "Test"]
     [:td "Test"]
     [:td "Test"]
     [:td "Test"]
     [:td "Test"]
     [:td "Test"]
     [:td "Test"]]
    [:tr
     [:td "Test"]
     [:td "Test"]
     [:td "Test"]
     [:td "Test"]
     [:td "Test"]
     [:td "Test"]
     [:td "Test"]]]])

(defn app []
  [:body
   [:div.container
    [header-menu]
    [:div.body-container
     [home-page]]]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [app] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
