(function() {
  'use strict';

  angular
    .module('emm')
    .controller('HelpIntroController', HelpIntroController);

  /** @ngInject */
  function HelpIntroController() {

    var vm = this;

    vm.page = 1;

  }
})();
