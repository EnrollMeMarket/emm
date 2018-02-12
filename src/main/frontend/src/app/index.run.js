(function() {
  'use strict';

  angular
    .module('emm')
    .run(runBlock);

  /** @ngInject */
  function runBlock($rootScope, $location, UserService) {

    var loginCallback = $rootScope.$on( "$stateChangeStart", function(e, toState) {

      var isLoggedIn = UserService.isLoggedIn();

      if (toState.restricted && !isLoggedIn) {
        $location.path("/");
      }

    });
    $rootScope.$on('$destroy', loginCallback)
  }

})();
