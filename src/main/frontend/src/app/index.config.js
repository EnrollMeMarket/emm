(function() {
  'use strict';

  angular
    .module('emm')
    .config(config);

  /** @ngInject */
  function config($logProvider, $httpProvider, toastrConfig, jwtOptionsProvider, environment) {
    // Enable log
    $logProvider.debugEnabled(true);

    // Set options third-party lib
    toastrConfig.allowHtml = true;
    toastrConfig.timeOut = 3000;
    toastrConfig.positionClass = 'toast-top-right';
    toastrConfig.preventDuplicates = true;
    toastrConfig.progressBar = true;

    var isApiCall = function(url) {
      return url.substr(0, environment.apiUrl.length) == environment.apiUrl;
    };

    jwtOptionsProvider.config({
      whiteListedDomains: ['localhost', 'rossum.knbit.edu.pl'],
      tokenGetter: function(UserService, options) {
        if(isApiCall(options.url)) return UserService.getAccessToken();
        return null;
      },
      authPrefix: ''
    });

    $httpProvider.interceptors.push('jwtInterceptor');
  }

})();
