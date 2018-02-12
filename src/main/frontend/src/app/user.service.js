(function () {
  'use strict';

  angular
    .module('emm')
    .factory("UserService", function ($cookies, $location, $window, oauthPath, adminRole, starostaRole, ProfileService) {

      var currentUser = null;
      var accessToken = null;
      var refreshToken = null;

      return {
        getUserIndex: function () {
          return currentUser ? currentUser.index : '';
        },

        getUserRole: function () {
          return currentUser ? currentUser.userRole : '';
        },

        refreshState: function () {
          if (!this.isLoggedIn() && $cookies.get('loggedIn') == 'true') {
            if (ProfileService.isDebugEnabled()) {
              currentUser = {
                index: $cookies.get('index'),
                userRole: $cookies.get('indexRole')
              };
              accessToken = $cookies.get('accessToken');
              refreshToken = $cookies.get('refreshToken')
            } else {
              this.oauthLogin();
            }
          }
        },

        getAccessToken: function () {
          return accessToken;
        },

        getRefreshToken: function () {
          return refreshToken;
        },

        setAccessToken: function (token) {
          accessToken = token;
        },

        setRefreshToken: function (token) {
          refreshToken = token;
        },

        isLoggedIn: function () {
          return (currentUser != null && currentUser != {});
        },

        isAdmin: function () {
          return this.isLoggedIn() && currentUser.userRole === adminRole;
        },

        isStarosta: function () {
          return this.isLoggedIn() && (currentUser.userRole === adminRole || currentUser.userRole === starostaRole);
        },

        login: function (user, accessTokenValue, refreshTokenValue) {
          currentUser = user;
          accessToken = accessTokenValue;
          refreshToken = refreshTokenValue;
          $cookies.put('loggedIn', 'true');
          $cookies.put('index', (ProfileService.isDebugEnabled() ? user.index : 'false'));

          if (ProfileService.isDebugEnabled()) {
            $cookies.put('indexRole', user.userRole);
            $cookies.put('accessToken', accessToken);
            $cookies.put('refreshToken', refreshToken);
          }
        },

        oauthLogin: function () {
          $window.location.href = oauthPath;
        },

        logout: function () {
          $cookies.remove('loggedIn');

          if (ProfileService.isDebugEnabled()) {
            $cookies.remove('index');
            $cookies.remove('indexRole');
          }

          currentUser = null;
          $location.path("/");
          return null;
        }
      }
    });
})();
