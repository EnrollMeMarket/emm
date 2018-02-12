/* global malarkey:false, moment:false */
(function () {
  'use strict';

  angular
    .module('emm')
    .constant('malarkey', malarkey)
    .constant('moment', moment)
    .constant('oauthPath', 'your oauthPath')

    //roles
    .constant('studentRole', 'STUDENT')
    .constant('starostaRole', 'STAROSTA')
    .constant('adminRole', 'ADMIN')

    //profiles
    .constant('devProfile', 'dev')
    .constant('integProfile', 'integ')
    .constant('prodProfile', 'prod')
})();
