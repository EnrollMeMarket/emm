(function () {
  'use strict';

  angular
    .module('emm')
    .factory("NotificationService", function (ngDialog, $location) {

      var vm = this;


      vm.notification = function (title, message, onClose, buttonText, iconName, iconClass, buttonClass) {
        ngDialog.open({
          template: 'app/notifications/notification.html',
          className: "ngdialog-theme-default",
          controller: "NotificationController",
          controllerAs: "notification",
          data: {
            title: title,
            message: message,
            buttonText: buttonText,
            onClose: onClose,
            iconName: iconName,
            iconClass: iconClass,
            buttonClass: buttonClass
          }
        })
      };

      vm.error = function (title, message, onClose) {
        if(angular.isUndefined(onClose)) onClose = function(){ $location.path('/') };
        if(angular.isUndefined(title)) title = 'Oops!';
        if(angular.isUndefined(message)) message = 'Something went wrong.<br><br>' +
          'Try again later. If the error persists, please inform the Enroll-Me staff.';


        vm.notification(title, message, onClose, "Close", "glyphicon-exclamation-sign", "danger", "btn-danger");
      };

      vm.success = function (title, message, onClose) {
        if(angular.isUndefined(onClose)){
          onClose = function(){};
        }

        vm.notification(title, message, onClose, "OK", "glyphicon-ok-sign", "success", "btn-success");
      };


      vm.yesNoDialog = function (title, message, yesButtonText, noButtonText, successCallback, errorCallback) {
        ngDialog.openConfirm({
          template: 'app/notifications/yesNoDialog.html',
          className: "ngdialog-theme-default",
          controller: "YesNoDialogController",
          controllerAs: "yesNoDialog",
          closeByDocument: false,
          closeByEscape: false,
          data: {
            title: title,
            message: message,
            yesButtonText: yesButtonText,
            noButtonText: noButtonText
          }
        }).then(successCallback, errorCallback);
      };



      return this;

    });
})();
