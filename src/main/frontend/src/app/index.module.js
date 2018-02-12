(function() {
  'use strict';

  angular
    .module('emm', ['emm.config', 'ngCookies', 'ngTouch', 'ngSanitize', 'ngMessages', 'ngAria', 'ngDialog', 'restangular',
      'ngRoute', 'ui.calendar', 'ui.bootstrap', 'toastr', 'angular-jwt', 'ngFileSaver']);

})();
