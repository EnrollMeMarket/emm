(function() {
  'use strict';

  angular
    .module('emm')
    .filter('paginationFilter', function() {
      return function(items, page, itemsPerPage) {
        return items.filter(function(item, index){
          return (Math.floor(index/itemsPerPage) + 1) === page;
        });

      };
    })
    .controller('MarketListController', MarketListController);

  /** @ngInject */
  function MarketListController($location, NotificationService, ApiService, $timeout, UserService) {

    var vm = this;

    vm.lang = {
      "subpageHeader": "Markets",
      "nextButtonText": "Next",
      "previousButtonText": "Previous",
      "createMarketButtonText": "New Market",
      "connectionFailureTitle": "Error",
      "connectionFailureText": "Failed to reach the server side. Please try again later.",
      "noMarketsMessageStarosta": "No markets exist at the time. Be the first person to create one!",
      "noMarketsMessage": "No markets exist at the time.",
      "headerName": "Name",
      "headerCreator": "Created By",
      "headerStart": "Start",
      "headerFinish": "Finish",
      "headerState": "State",
      "headerDetails": "Details",
      "closeMarketText": "Close Market",
      "detailsButtonText": "DETAILS",
      "manualBegFinText": "MANUAL",
      "OPEN": "OPEN",
      "CLOSED": "CLOSED"

    };

    vm.isRequestProcessing = false;

    var createMarketPath = "/createMarket";
    var homePath = "/";


    vm.markets = [];

    var getExistingMarkets = function(){
      vm.isRequestProcessing = true;
      ApiService.get(
        '/market',
        function(response) {
          vm.markets = response.data.reverse();
          //$log.info("Successfully fetched markets data.");
          //$log.info(angular.fromJson(response));
          vm.isRequestProcessing = false;
        },
        function() {
          //$log.error("Failed to fetch existing markets.");
          //$log.error(angular.fromJson(response));
          NotificationService.error(vm.lang.connectionFailureTitle, vm.lang.connectionFailureText,
            function(){
              $timeout(function() {
                $location.path(homePath);
              }, 0);
            });
          vm.isRequestProcessing = false;
        }
      );
    };

    $timeout(getExistingMarkets(), 1);

    vm.itemsPerPage = 5;
    vm.maxPaginationSize = 10;
    vm.currentPage = 1;

    vm.getDateTimeString = function(millis){
      if(millis === -1){
        return vm.lang.manualBegFinText;
      }

      var date = new Date(millis);
      var dateString = date.toLocaleDateString();
      var hoursString = date.getHours();
      var minutesString = date.getMinutes() <= 10 ? "0"+date.getMinutes() : date.getMinutes();
      return  dateString + " - " + hoursString + ":" + minutesString;
    };

    vm.goToCreateMarket = function(){
        $location.path(createMarketPath);
    };

    vm.shouldShowPagination = function(){
      return vm.markets.length > vm.itemsPerPage;
    };

    vm.areThereAnyMarkets = function(){
      return vm.markets.length > 0;
    };

    vm.isStarosta = function() {
      return UserService.isStarosta();
    }
  }
})();
