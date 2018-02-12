(function() {
  'use strict';

  angular
    .module('emm')
    .controller('YesNoDialogController', YesNoDialogController);

  /** @ngInject */
  function YesNoDialogController($scope, $sce) {

    var vm = this;
    vm.confirm = $scope.confirm;
    vm.closeThisDialog = $scope.closeThisDialog;
    vm.title = $scope.ngDialogData.title;
    vm.message = $sce.trustAsHtml($scope.ngDialogData.message);
    vm.yesButtonText = $scope.ngDialogData.yesButtonText;
    vm.noButtonText = $scope.ngDialogData.noButtonText;

    vm.close = function() {
      vm.closeThisDialog()
    };

    vm.closeConfirm = function() {
      vm.confirm("yup")
    };

  }

})();
