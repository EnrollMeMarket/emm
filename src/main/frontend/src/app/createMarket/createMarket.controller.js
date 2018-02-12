(function() {
  'use strict';

  angular
    .module('emm')
    .directive('customOnChange', function() {
      return {
        restrict: 'A',
        link: function (scope, element, attrs) {
          var onChangeHandler = scope.$eval(attrs.customOnChange);
          element.bind('change', onChangeHandler);
        }
      };
    })
    .controller('CreateMarketController', CreateMarketController);

  /** @ngInject */
  function CreateMarketController($timeout, NotificationService, ApiService, $location, UserService) {

    var vm = this;

    vm.lang = {
      "subpageHeader": "Create Market",

      "fileUploadHeader": "File Upload",
      "timeSettingsHeader": "Time Settings",
      "subjectsHeader": "Subjects",
      "summaryHeader": "Summary",

      "marketNameLabel": "Market Name:",
      "marketNamePlaceholder": "Please enter a market name",
      "marketExists": "A market with this name already exists",
      "marketNeedsAName": "This field cannot be empty",
      "wrongAmountOfFiles": "You need to specify exactly two files",
      "filesNotTxt": "The specified files need to be text files",
      "filesNotInCorrectFormat": "The specified files are not in a correct format",
      "selectedTimePassed": "The selected time has already passed",
      "datesNotChronological": "A market can't finish before it starts",
      "enableAutoOpen": "Enable automatic opening",
      "enableAutoClose": "Enable automatic closure",
      "nextButtonText": "Next",
      "prevButtonText": "Previous",
      "doneButtonText": "Finish",
      "startNowButtonText": "Start now",
      "selectAllText": "Select all",
      "subjectChoiceText": "Please select the subjects you wish to enable:",
      "noSubjectSelected": "You need to select at least one subject",

      "summaryMarketName": "Market name:",
      "summaryAutostart": "Automatic start:",
      "summaryAutofinish": "Automatic finish:",

      "summaryDisabled": "disabled",

      "connectionFailureTitle": 'Connection failure',
      "connectionFailureText": "Failed to reach the server side. Please try again later.",
      "pleaseWaitTitle": 'Please wait',
      "pleaseWaitText": "Not all server side data has been loaded yet. Please wait a second before proceeding.",
      "marketCreatedTitle": 'Market created',
      "marketCreatedText": "You have successfully created a new market!",
      "marketCreationFailureTitle": 'Creation failure',
      "marketCreationFailureText": "An error occured while trying to create a new market."

    };

    var marketListUrl = "/markets";


    vm.sampleMarketNames = [];

    vm.isRequestProcessing = false;

    var areNamesLoaded = false;

    var getMarketNames = function(){
      vm.isRequestProcessing = true;
      ApiService.get(
        '/market/names',
        function(response) {
          vm.sampleMarketNames = response.data;
          //$log.info("Existing names fetch successful.");
          //$log.info(angular.fromJson(response));
          areNamesLoaded = true;
          vm.isRequestProcessing = false;
        },
        function() {
          //$log.error("Existing names fetch failed.");
          //$log.error(angular.fromJson(response));
          NotificationService.error(vm.lang.connectionFailureTitle, vm.lang.connectionFailureText,
            function(){
              $timeout(function() {
                $location.path(marketListUrl);
              }, 0);
            });
          vm.isRequestProcessing = false;
        }
      );
    };

    $timeout(getMarketNames(), 1);


    //Page 1 stuff
    vm.enableAutostart = false;

    vm.enableAutofinish = false;
    var d1 = new Date();
    d1.setHours( 12 );
    d1.setMinutes( 0 );
    d1.setSeconds( 0 );
    vm.startDate = d1;

    var d2 = new Date();
    d2.setHours(20);
    d2.setMinutes(0);
    d2.setSeconds(0);
    vm.endDate = d2;

    vm.marketName = "";


    //Page 2 stuff

    //Datepicker stuff

    vm.datePickerOptions = {
      showWeeks: false,
      minDate: new Date(),
      startingDay: 1

    };


    //Page 3 stuff

    vm.subjectNames = [];

    vm.selectAll = false;

    vm.updateAllSubjects = function(){
      for(var i=0; i<vm.subjectNames.length; i++){
        vm.subjectNames[i].enabled = vm.selectAll;
      }
    };

    vm.deselectSelectAll = function(element){
      if(element.enabled === false){
        vm.selectAll = false;
      }
    };

      //Page 4 stuff



    //Validation

    vm.nameExists = false;
    vm.nameEmpty = false;

    vm.checkForNameErrors = function() {
      vm.nameExists = vm.sampleMarketNames.indexOf(vm.marketName) > -1;

      vm.nameEmpty = vm.marketName.length == 0;

      return vm.nameExists || vm.nameEmpty;
    };


    vm.datesNotChronological = false;
    vm.startAlreadyPassed = false;
    vm.endAlreadyPassed = false;

    vm.areDatesChronological = function(){
      if(vm.enableAutostart && vm.enableAutofinish){
        if(vm.startDate >= vm.endDate) {
          vm.datesNotChronological=true;
          return false;
        }
      }
      vm.datesNotChronological=false;
      return true;
    };

    vm.isStartInTheFuture = function(){
      var now = new Date();
      if(vm.enableAutostart && (now > vm.startDate)){
        vm.startAlreadyPassed = true;
        return true;
      }
      vm.startAlreadyPassed = false;
      return false;
    };

    vm.isEndInTheFuture = function(){
      var now = new Date();
      if(vm.enableAutofinish && (now > vm.endDate)){
        vm.endAlreadyPassed = true;
        return true;
      }
      vm.endAlreadyPassed = false;
      return false;
    };

    vm.validateDates = function(){
      var result = !vm.areDatesChronological();
      result = vm.isStartInTheFuture() || result;
      result = vm.isEndInTheFuture() || result;
      return result;
    };


    vm.noSubjectsSelected = false;

    vm.checkIfSubjectSelected = function() {
      var isEnabled = false;

      for(var i=0; i<vm.subjectNames.length; i++){
        isEnabled = isEnabled || vm.subjectNames[i].enabled;
      }

      vm.noSubjectsSelected = !isEnabled;
      return vm.noSubjectsSelected;
    };

    var checkForFileErrors = function(){
      if(vm.files.length !== 2) {
        vm.wrongAmountOfFiles = true;
      }
      var result = vm.wrongAmountOfFiles;
      result = result || vm.filesNotInCorrectFormat;
      result = result || vm.filesNotTxt;

      return result;
    };

    //Navigation

    vm.canNext = [
      function(){
        if(!areNamesLoaded){
          NotificationService.error(vm.lang.pleaseWaitTitle, vm.lang.pleaseWaitText);
          return false;
        }
        var result = vm.checkForNameErrors();
        result = checkForFileErrors() || result;

        return !result;
      },
      function(){
        var result = vm.validateDates();

        return !result;
      },
      function(){
        var result = vm.checkIfSubjectSelected();

        return !result;
      },
      function(){
        var result = false;

        return !result;
      }
    ];

    vm.selectedPage = 0;

    vm.nextPage = function(){
      if(vm.canNext[vm.selectedPage]()) {
        vm.selectedPage++;
      }
    };

    vm.prevPage = function(){
      vm.selectedPage--;
    };


    // File Management, probably to be rearranged a bit

    vm.files = [];
    vm.planFileContents = "";
    vm.termsFileContents = "";

    vm.filesToRead = 2;
    vm.wrongAmountOfFiles = false;
    vm.filesNotTxt = false;
    vm.filesNotInCorrectFormat = false;

    var checkFileFormat = function(termsFile){
      var checkResult = true;
      var semicolons = termsFile.split(';').length-1;
      var Ns = termsFile.split('\\n').length-1;

      if(!(semicolons>0) || !(Ns>0) || ((semicolons) !== 8*(Ns))){
        checkResult = false;
      }

      return checkResult;
    };

    vm.setFiles = function(event){
      vm.files = event.target.files;

      $timeout(function () {  //assume not guilty
        vm.wrongAmountOfFiles = false;
        vm.filesNotTxt = false;
        vm.filesNotInCorrectFormat = false;
      }, 0);

      var assignFileContents = function(){
        var fileCheckResult;

        if(result1.indexOf(';') > -1){
          fileCheckResult = checkFileFormat(result1);
          if(fileCheckResult){
            vm.termsFileContents = result1;
            vm.planFileContents = result2;
          }
        } else {
          fileCheckResult = checkFileFormat(result2);
          if(fileCheckResult) {
            vm.termsFileContents = result2;
            vm.planFileContents = result1;
          }
        }

        return fileCheckResult;
      };

      var onBothLoaded = function(){
        if(assignFileContents()){

          var subjectNamesSet = new Set();
          var splitTermsFileContents = vm.termsFileContents.replace(/\\n/g, ";").split(';');
          for(var i=1; i<splitTermsFileContents.length; i+=9){
            subjectNamesSet.add(splitTermsFileContents[i]);
          }
          var temporarySubjectObjectsArray = [];

          subjectNamesSet.forEach(function(value){
            temporarySubjectObjectsArray.push({'name': value, 'enabled': false});
          });

          vm.subjectNames = temporarySubjectObjectsArray;


        } else {
          $timeout(function () {
            vm.filesNotInCorrectFormat = true;
          }, 0);
        }

      };

      var areFilesTxt = function(fileList){
        var result = true;
        for(var idx=0; idx<fileList.length; idx++){
          result = result && (fileList[idx].type === "text/plain");
        }
        return result;
      };

      if(vm.files.length === 2){
        vm.filesToRead = 2;

        if(areFilesTxt(vm.files)) {

          var reader1 = new FileReader();
          var reader2 = new FileReader();
          var result1;
          var result2;

          reader1.onload = function () {
            vm.filesToRead -= 1;
            result1 = reader1.result;
            if (vm.filesToRead === 0) {
              onBothLoaded();
            }
          };
          reader2.onload = function () {
            vm.filesToRead -= 1;
            result2 = reader2.result;
            if (vm.filesToRead === 0) {
              onBothLoaded();
            }
          };

          reader1.readAsText(vm.files[0]);
          reader2.readAsText(vm.files[1]);
        } else {
          $timeout(function () {
            vm.filesNotTxt = true;
          }, 0);

        }

      } else {
        $timeout(function () {
          vm.wrongAmountOfFiles = true;
        }, 0);
      }

    };

    // End buttons actions

    vm.startNowFunction = function(){
      var startTime = 0;
      var finishTime = vm.enableAutofinish ? vm.endDate.getTime(): -1;
      createMarketFunction(startTime, finishTime);
    };

    vm.finishFunction = function () {
      var startTime = vm.enableAutostart ? vm.startDate.getTime(): -1;
      var finishTime = vm.enableAutofinish ? vm.endDate.getTime(): -1;
      createMarketFunction(startTime, finishTime);
    };

    var createMarketFunction = function(startTime, finishTime) {
      if (UserService.isStarosta()) {
        var enabledSubjects = [];
        for (var obj in vm.subjectNames) {
          if (vm.subjectNames[obj].enabled) {
            enabledSubjects.push(vm.subjectNames[obj].name);
          }
        }

        var newMarketData = {
          "name": vm.marketName,
          "creator": "?", //God knows what it's gonna be
          "beg": startTime,
          "end": finishTime,
          "subjects": enabledSubjects,
          "termFile": vm.termsFileContents.replace(/\\n/g, "\n"),
          "planFile": vm.planFileContents
        };

        vm.isRequestProcessing = true;
        ApiService.post(
          '/market',
          function() {
            //$log.info("Market creation successful.");
            //$log.info(angular.fromJson(response));
            NotificationService.success(vm.lang.marketCreatedTitle,
              vm.lang.marketCreatedText,
              function () {
                $timeout(function () {
                  $location.path(marketListUrl);
                }, 0);
              });

            vm.isRequestProcessing = false;
          },
          function() {
            //$log.error("Market creation failed.");
            //$log.error(angular.fromJson(response));
            NotificationService.error(vm.lang.marketCreationFailureTitle, vm.lang.marketCreationFailureText);
            vm.isRequestProcessing = false;
          },
          newMarketData
        );
    } else {
      alert('You have no permission!');
    }
  }
}
})();
