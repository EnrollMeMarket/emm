(function () {
  'use strict';

  angular
    .module('emm')
    .controller('MarketDetailsController', MarketDetailsController);

  /** @ngInject */
  function MarketDetailsController($location, $http, NotificationService, $timeout, $routeParams, environment,
                                   ApiService, ngDialog, FileSaver, Blob, UserService, $route) {

    UserService.refreshState();

    var vm = this;
    var finishMarketPath = "/market/finish/";
    var openMarketPath = "/market/open/";
    var reportPath = "/report/";

    vm.lang = {
      "subpageHeader": "Market Details",
      "marketName": "Market Name:",
      "marketStart": "Start:",
      "marketFinish": "Finish:",
      "marketSubjects": "Subjects:",
      "marketState": "State:",
      "manualBegFinText": "MANUAL",
      "stateButtonOpen": "Open Market",
      "stateButtonClose": "Close Market",
      "stateButtonGenerate": "Generate report",
      "backButtonText": "Back",
      "stateChangeFailureTitle": "Operation Failed",
      "stateChangeFailureText": "The attempt to change the market's state was not successful.",
      "stateChangeSuccessTitle": "Operation Successful"
    };

    var marketListPath = "/markets";

    vm.marketName = $routeParams.marketName;
    vm.completedExchanges = [];
    vm.end = -1;
    vm.beg = -1;
    vm.subjects = [];
    vm.state = "";

    vm.isRequestProcessing = false;

    var getMarketDetails = function (marketName) {
      vm.isRequestProcessing = true;

      $http({
        method: 'GET',
        url: environment.apiUrl + "/market/" + marketName
      }).then(function successCallback(response) {

        // $log.info("Successfully fetched market details.");
        // $log.info(angular.fromJson(response));
        vm.completedExchanges = response.data.done_swaps;
        vm.beg = response.data.beg;
        vm.end = response.data.end;
        vm.subjects = response.data.subjects;
        vm.state = response.data.state;
        vm.isRequestProcessing = false;

      }, function errorCallback() {

        // $log.error("Failed to fetch market details.");
        // $log.error(angular.fromJson(response));
        NotificationService.error(vm.lang.connectionFailureTitle, vm.lang.connectionFailureText,
          function () {
            $timeout(function () {
              $location.path(marketListPath);
            }, 0);
          });
        vm.isRequestProcessing = false;
      });
    };

    $timeout(getMarketDetails(vm.marketName), 0);

    vm.isClosed = function () {
      return vm.state === "CLOSED";
    };

    vm.isOpen = function () {
      return vm.state === "OPEN";
    };

    vm.isFinished = function () {
      return vm.state === "FINISHED";
    };

    vm.getDateTimeString = function (millis) {
      if (millis === -1) {
        return vm.lang.manualBegFinText;
      }

      var date = new Date(millis);
      var dateString = date.toLocaleDateString();
      var hoursString = date.getHours();
      var minutesString = date.getMinutes() <= 10 ? "0" + date.getMinutes() : date.getMinutes();
      return dateString + " - " + hoursString + ":" + minutesString;
    };

    vm.returnToList = function () {
      $timeout($location.path(marketListPath), 0);
    };

    vm.openMarket = function () {
      vm.isRequestProcessing = true;

      ngDialog.openConfirm({
        template: 'app/notifications/yesNoDialog.html',
        className: "ngdialog-theme-default",
        controller: "YesNoDialogController",
        controllerAs: "yesNoDialog",
        closeByDocument: false,
        closeByEscape: false,
        data: {
          title: "Finish",
          message: "Are you sure you want to finish this market?",
          yesButtonText: "Yes!",
          noButtonText: "Maybe not"
        }
      }).then(function () {
          ApiService.post(
            openMarketPath + vm.marketName,
            function () {
              vm.isRequestProcessing = false;
              $route.reload();
            },
            function () {
              NotificationService.error('Oops!', 'Cannot open a market. Contact with admin.');
              vm.isRequestProcessing = false;
            });
        }
      );
    };

    vm.finishMarket = function () {
      vm.isRequestProcessing = true;

      ngDialog.openConfirm({
        template: 'app/notifications/yesNoDialog.html',
        className: "ngdialog-theme-default",
        controller: "YesNoDialogController",
        controllerAs: "yesNoDialog",
        closeByDocument: false,
        closeByEscape: false,
        data: {
          title: "Finish",
          message: "Are you sure you want to finish this market?",
          yesButtonText: "Yes!",
          noButtonText: "Maybe not"
        }
      }).then(function () {
          ApiService.post(
            finishMarketPath + vm.marketName,
            function () {
              vm.isRequestProcessing = false;
              $route.reload();
            },
            function () {
              NotificationService.error('Oops!', 'Cannot finish a market. Contact with admin.');
              vm.isRequestProcessing = false;
            });
        }
      );
    };

    vm.generateReport = function () {

      vm.isRequestProcessing = true;

      $http({
        method: 'GET',
        url: environment.apiUrl + reportPath + vm.marketName,
        responseType: 'arraybuffer'
      })
        .success(function(data){
          var blob = new Blob([data], {type: 'application/pdf'});
          FileSaver.saveAs(blob, vm.marketName + '.pdf');
          vm.isRequestProcessing = false;
        })
        .error(function() {
          NotificationService.error('Oops!', 'Cannot generate a report. Contact with admin.');
          vm.isRequestProcessing = false;
        });
    }
  }
})();
