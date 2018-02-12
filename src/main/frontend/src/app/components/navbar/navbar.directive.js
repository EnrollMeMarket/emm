(function () {
  'use strict';

  angular
    .module('emm')
    .directive('acmeNavbar', acmeNavbar);

  /** @ngInject */
  function acmeNavbar() {
    var directive = {
      restrict: 'E',
      templateUrl: 'app/components/navbar/navbar.html',
      scope: {
        creationDate: '='
      },
      controller: NavbarController,
      controllerAs: 'vm',
      bindToController: true
    };

    return directive;

    /** @ngInject */
    function NavbarController(UserService, $location, $window) {

      UserService.refreshState();

      var vm = this;
      vm.showMenu = $window.outerWidth >= 768;

      vm.getUserIndex = function () {
        return UserService.getUserIndex();
      };

      vm.getUserRole = function () {
        var role = UserService.getUserRole();
        return role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
      };

      vm.logout = function () {
        return UserService.logout();
      };

      vm.isLoggedIn = function () {
        return UserService.isLoggedIn();
      };

      vm.isStarosta = function () {
        return UserService.isStarosta();
      };

      vm.isAdmin = function () {
        return UserService.isAdmin();
      };

      vm.isActive = function (viewLocation) {
        return viewLocation === $location.path();
      };

      vm.toggleMenu = function () {
        vm.showMenu = !vm.showMenu;
      };
    }
  }

})();
