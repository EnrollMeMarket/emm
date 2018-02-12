(function() {
  'use strict';

  angular
    .module('emm')
    .config(routerConfig);

  /** @ngInject */
  function routerConfig($routeProvider, $locationProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'app/main/main.html',
        controller: 'MainController',
        controllerAs: 'main',
        restricted: false
      })
      .when('/contact', {
        templateUrl: 'app/contact/contact.html',
        controller: 'ContactController',
        controllerAs: 'contact',
        restricted: true
      })
      .when('/calendarView', {
        templateUrl: 'app/calendarView/calendarView.html',
        controller: 'CalendarViewController',
        controllerAs: 'calendarView',
        restricted: true
      })
      .when('/createMarket', {
        templateUrl: 'app/createMarket/createMarket.html',
        controller: 'CreateMarketController',
        controllerAs: 'createMarket',
        restricted: true
      })
      .when('/exchangesSummary', {
        templateUrl: 'app/exchangesSummary/exchangesSummary.html',
        controller: 'ExchangesSummaryController',
        controllerAs: 'exchangesSummary',
        restricted: true
      })
      .when('/markets', {
        templateUrl: 'app/marketList/marketList.html',
        controller: 'MarketListController',
        controllerAs: 'marketList',
        restricted: true
      })
      .when('/markets/:marketName', {
        templateUrl: 'app/marketList/marketDetails/marketDetails.html',
        controller: 'MarketDetailsController',
        controllerAs: 'marketDetails',
        restricted: true
      })
      .when('/helpQA', {
        templateUrl: 'app/helpQA/helpQA.html',
        controller: 'HelpQAController',
        controllerAs: 'helpQA',
        restricted: true
      })
      .when('/helpIntro', {
        templateUrl: 'app/helpIntro/helpIntro.html',
        controller: 'HelpIntroController',
        controllerAs: 'helpIntro',
        restricted: true
      })
      .otherwise({redirectTo: '/'});

    $locationProvider.html5Mode(true);
  }

})();
