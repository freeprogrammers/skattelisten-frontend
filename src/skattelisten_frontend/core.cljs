(ns skattelisten-frontend.core
  (:require
   skattelisten-frontend.controllers
   [reagent.dom :as d]
   [re-frame.core :as rf]))

(defn search-bar [value]
  [:div.search-bar
   [:input.search-bar__input
    {:type "text"
     :value value
     :placeholder ""
     :on-change #(rf/dispatch [:set-search-text (-> % .-target .-value)])}]])

(defn header-menu []
  (let [search-text (rf/subscribe [:search-text])]
    [:header
     [:p.site-name "Skattelisten"]
     [search-bar @search-text]
     [:div.links]]))

(defn display-companies [companies]
  [:table
   [:thead
    [:tr
     [:th "CVR"]
     [:th "Name"]
     [:th "Type"]
     [:th "Year"]
     [:th "Taxable income"]
     [:th "Deficit"]
     [:th "Corporate Tax"]]]
   [:tbody
    (for [company companies]
      ^{:key (company :cvr)}
      [:tr
       [:td (company :cvr)]
       [:td (company :company_name)]
       [:td (company :company_type)]
       [:td (company :year)]
       [:td (company :taxable_income) " kr."]
       [:td (company :deficit) " kr."]
       [:td (company :corporate_tax) " kr."]])]])

(defn app []
  [:div
   [header-menu]
   [:div.body-container
    [display-companies @(rf/subscribe [:companies])]]])

(defn mount-root []
  (d/render [app] (.getElementById js/document "app")))

(defn ^:export init! []
  (rf/dispatch-sync [:initialize-db])
  (rf/dispatch [:load-companies ""])
  (mount-root))
