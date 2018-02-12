(function () {
  'use strict';

  angular
    .module('emm')
    .controller('ExchangesSummaryController', ExchangesSummaryController);

  /** @ngInject */

  function ExchangesSummaryController($location, NotificationService, ApiService, UserService, ngDialog) {

    UserService.refreshState();

    var vm = this;
    var userId = UserService.getUserIndex();

    vm.formatTimeString = function (time) {
      return time.split(':').slice(0, 2).join(":");
    };

    vm.removeSwap = function (swapId) {
      ngDialog.openConfirm({
        template: 'app/notifications/yesNoDialog.html',
        className: "ngdialog-theme-default",
        controller: "YesNoDialogController",
        controllerAs: "yesNoDialog",
        closeByDocument: false,
        closeByEscape: false,
        data: {
          title: "Remove preference",
          message: "Are you sure you want to remove this preference?",
          yesButtonText: "Yes!",
          noButtonText: "Maybe not"
        }
      }).then(function () {
        ApiService.delete(
          '/swap/' + swapId,
          function () {
            vm.loadSwaps();
          },
          function () {
            NotificationService.error();
          },
          {}
        );
      }, function () {
      });
    };

    vm.loadSwaps = function () {

      vm.isRequestProcessing = true;
      ApiService.get(
        '/student/' + userId + '/swaps',
        function (response) {
          vm.terms = response.data;
          vm.isRequestProcessing = false;
        },
        function (response) {
          if (response.status == 404) {

            NotificationService.error('Oops!',
              'You\'re not assigned to any existing market. <br>' +
              'If you think this is an error, contact your year\'s representative.');

            $location.path('/');
          } else {
            NotificationService.error();
          }
          vm.isRequestProcessing = false;
        }
      );
    };

    vm.loadSwaps();

  }
})();
