(function() {
  'use strict';

  angular
    .module('emm')
    .controller('NotificationController', NotificationController);

  /** @ngInject */
  function NotificationController($scope, $sce) {

    var vm = this;
    vm.closeThisDialog = $scope.closeThisDialog;
    vm.title = $scope.ngDialogData.title;
    vm.message = $sce.trustAsHtml($scope.ngDialogData.message);
    vm.onClose = $scope.ngDialogData.onClose;
    vm.iconName = $scope.ngDialogData.iconName;
    vm.iconClass = $scope.ngDialogData.iconClass;
    vm.buttonClass = $scope.ngDialogData.buttonClass;
    vm.buttonText = $scope.ngDialogData.buttonText;

    $scope.$on('ngDialog.closing', function () {
      vm.onClose();
    });

    vm.close = function() {
      vm.closeThisDialog();
    };

  }

})();
