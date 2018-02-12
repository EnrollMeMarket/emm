(function() {
  'use strict';

  angular
    .module('emm')
    .factory("ApiService", function($http, environment, UserService) {

      // This service should be used for API calls that require a token
      // It also handles refreshing expired tokens, processing the failed API call again

      var functions = {
        sendRequest: function(method, path, successCallback, errorCallback, params) {
          var args = {
            method: method,
            url: path,
            headers: {'Content-Type': 'application/json'}
          };
          if(method == 'GET') args.params = (typeof params !== 'undefined') ?  params : {};
          if(method == 'POST' || method == 'DELETE') args.data = (typeof params !== 'undefined') ?  params : {};
          $http(args).then(successCallback, errorCallback);
        },

        sendApiCall: function(method, path, successCallback, errorCallback, params) {
          functions.sendRequest(method, path, successCallback,
            function(response) {
              if(response.status == 403) {
                functions.refreshToken(function() {functions.sendRequest(method, path, successCallback, errorCallback, params)});
              } else errorCallback(response);
            }, params);
        },

        get: function(path, successCallback, errorCallback, params) {
          functions.sendApiCall('GET', environment.apiUrl + path, successCallback, errorCallback, params);
        },

        post: function(path, successCallback, errorCallback, params) {
          functions.sendApiCall('POST', environment.apiUrl + path, successCallback, errorCallback, params);
        },

        delete: function(path, successCallback, errorCallback, params) {
          functions.sendApiCall('DELETE', environment.apiUrl + path, successCallback, errorCallback, params);
        },

        refreshToken: function(cb) {
          $http({
            method: 'POST',
            url: environment.apiUrl + 'token/refresh',
            data: {
              refreshToken: UserService.getRefreshToken()
            }
          }).then(
            function(response) {
              UserService.setAccessToken(response.data['accessToken']);
              UserService.setRefreshToken(response.data['refreshToken']);
              cb();
            },
            function() {}
          );
        }
      };

      return functions;
    });
})();
