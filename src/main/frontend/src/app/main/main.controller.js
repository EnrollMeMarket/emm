(function () {
  'use strict';

  angular
    .module('emm')
    .controller('MainController', MainController);

  /** @ngInject */
  function MainController(UserService, NotificationService, $location, $http, adminRole,  starostaRole, studentRole, ProfileService, environment) {
    var vm = this;
    var code = ($location.search().code);

    vm.adminRole = adminRole;
    vm.starostaRole = starostaRole;
    vm.studentRole = studentRole;
    vm.debugIndex = '';
    vm.debugToken = '';     // only for integration environment for now

    vm.debugRole = ProfileService.isDevProfile() ? vm.studentRole : null;

    if (code != undefined) {

      $http({
        method: 'POST',
        url: environment.apiUrl + '/token/login/',
        data: {
          code: code
        }
      }).then(function successCallback(response) {
        vm.user = {};
        vm.user.index = response.data.transcript_number;
        vm.user.userRole = response.data.role;
        var accessToken = response.data.accessToken;
        var refreshToken = response.data.refreshToken;
        UserService.login(vm.user, accessToken, refreshToken, false);

        $location.search('code', null);
      }, function errorCallback() {
        NotificationService.error('Unable to log in');
      });
    } else if (!UserService.isLoggedIn()) UserService.refreshState();

    vm.isLoggedIn = function () {
      return UserService.isLoggedIn();
    };

    vm.isDevProfile = function () {
      return ProfileService.isDevProfile();
    };

    vm.isDebugEnabled = function() {
      return ProfileService.isDebugEnabled();
    }

    vm.isTokenRequired = function () {
      return ProfileService.isDebugEnabled() && ProfileService.isTokenRequired();
    }

    //only for debug - we won't need this function after deploying to emm.iiet.pl
    vm.loginDebug = function () {

      if (vm.isDebugEnabled()) {

        if (adminRole === vm.debugRole) {
          vm.debugIndex = environment.adminDebugIndex;
        } else if (starostaRole === vm.debugRole) {
          vm.debugIndex = environment.starostaDebugIndex;
        } else {
          vm.debugIndex = (/^\d+$/.test(vm.debugIndex)) ? vm.debugIndex : environment.studentDebugIndex;
        }

        if (ProfileService.isTokenRequired() && ProfileService.getToken() !== vm.debugToken) {
          NotificationService.error("Unable to log in", "Invalid token");
          return;
        }

        $http({
          method: 'POST',
          url: environment.apiUrl + '/token/debugLogin/',
          data: {
            transcriptNumber: vm.debugIndex
          }
        }).then(function successCallback(response) {
          vm.user = {};
          vm.user.index = response.data.transcript_number;
          vm.user.userRole = response.data.role;
          var accessToken = response.data.accessToken;
          var refreshToken = response.data.refreshToken;
          UserService.login(vm.user, accessToken, refreshToken, true);
        }, function errorCallback(response) {
          NotificationService.error("Unable to log in", angular.toJson(response));
        });
      }
    };

    vm.oauthLogin = function () {
      UserService.oauthLogin();
    };
  }
})();
