(function () {
  'use strict';

  angular
    .module('emm')
    .factory('ProfileService', function (environment, devProfile, prodProfile) {

      return {
        isDevProfile: function () {
          return environment.profile === devProfile;
        },

        isProdProfile: function () {
          return environment.profile === prodProfile;
        },

        isDebugEnabled: function() {
          return environment.isDebug;
        },

        isTokenRequired: function() {
          if (typeof environment.loginToken === "string" && environment.loginToken.length > 0) {
            return true;
          }

          return false;
        },

        getToken: function() {
          return environment.loginToken;
        }
      }

    });
})();


