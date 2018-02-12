(function () {
  'use strict';

  angular
    .module('emm')
    .controller('CalendarViewController', CalendarViewController);

  /** @ngInject */
  function CalendarViewController(uiCalendarConfig, moment, $location, $window, NotificationService, ApiService, UserService, ProfileService) {

    UserService.refreshState();

    var vm = this;
    var weekdays = {"Mon": 1, "Tue": 2, "Wed": 3, "Thu": 4, "Fri": 5};
    var i, j;

    var afterLoadTimetable = function (response) {
      angular.element("#calendar").fullCalendar('removeEvents');
      vm.myTerms = response.data;
      if (vm.myTerms.length == 0) {
        NotificationService.error('Oops!',
          'You haven\'t got any terms assigned to any existing market. <br>' +
          'If you think this is an error, contact your year\'s representative.');

        vm.isRequestProcessing = false;
        $location.path('/');
      }

      for (i = 0; i < vm.myTerms.length; i++) {
        var tmp = vm.myTerms[i];
        var backgroundColor = (tmp['classId'] == vm.classId) ? 'blue' : (tmp['to_swap'] && !tmp['lecture'] ? 'cornFlowerBlue' : 'gray');

        vm.events.push({
          id: vm.events.length,
          classId: tmp['classId'],
          title: vm.makeTitle(tmp),
          fullTitle: vm.makeFullTitle(tmp),
          start: vm.makeDate(tmp['weekday'], tmp['begTime']),
          end: vm.makeDate(tmp['weekday'], tmp['endTime']),
          week: tmp['week'],
          wasChosen: false,
          isChosen: false,
          isMine: true,
          isAvailableNow: false,
          backgroundColor: backgroundColor,
          borderColor: 'white',
          className: tmp['to_swap'] && !tmp['lecture'] ? ['clickable'] : ['normal']
        });

        vm.myEvents = angular.copy(vm.events);
      }
      angular.element("#calendar").fullCalendar('addEventSource', vm.events);
      vm.refreshHoverHandler();
      hideLoadingScreen();
      vm.isRequestProcessing = false;
    };

    vm.calendar = {};
    vm.userId = UserService.getUserIndex();

    vm.myEvents = [];
    vm.myTerms = [];
    vm.ucc = uiCalendarConfig;
    vm.zoomEnabled = true;

    vm.addedSwaps = [];
    vm.removedSwaps = [];

    vm.isRequestProcessing = false;

    vm.makeTitle = function (term) {
      var title = term['title'] + "<br>" + term['host'];
      if (term['available']) title += '<br><br><span class="available-now">&nbsp;AVAILABLE NOW!&nbsp;</span>';
      return title;
    };

    vm.makeFullTitle = function (term) {
      var week = term['week'] == "all" ? "" : term['week'];
      return term['weekday'] + " " + term['begTime'].substring(0, term['begTime'].length - 3) +
        "-" + term['endTime'].substring(0, term['endTime'].length - 3) + " " + week + " " + term['host'];
    };

    vm.makeDate = function (weekday, time) {
      return '2001-01-0' + weekdays[weekday] + 'T' + time
    };

    vm.findInMyEvents = function (classId) {
      for (i = 0; i < vm.myEvents.length; i++) {
        if (vm.myEvents[i].classId == classId) return vm.myEvents[i];
      }
    };

    vm.exchangeNow = function (calEvent) {
      showLoadingScreen();
      var event = vm.events[calEvent.id];
      var classId = event.classId;
      NotificationService.yesNoDialog(
        "Exchange term",
        "This is your lucky day! You will receive chosen term immediately.<br>" +
        "Remember, this cannot be undone - you won't be able to get your previous term back!<br><br>" +
        "<div style=\"text-align: center\">" +
        "<span class=\"label label-primary\">" + vm.findInMyEvents(vm.classId).fullTitle + "</span><br>" +
        "<span class=\"glyphicon glyphicon-arrow-down\"></span><br>" +
        "<span class=\"label label-primary\">" + event.fullTitle + "</span></div><br>" +
        "Are you sure you want to continue?",
        "Yes!",
        "Maybe not",
        function () {
          vm.isRequestProcessing = true;
          ApiService.post(
            '/swap/many',
            function (response) {
              if (response.data.doneSwaps.length > 0) {
                vm.cancelSwapsFromClass(vm.classId);
                vm.loadTimetable(afterLoadTimetable);
                vm.classId = 0;
                NotificationService.success('Yay!', "The exchange was successful. Enjoy your new term!");
              } else {
                vm.changeCourse(vm.classId);
                NotificationService.error('Oh no!', "It seems someone was faster than you. The exchange is no longer available.<br>Don't lose your hope though! The term has been marked as wanted. You can change it if you wish to.");
              }
              hideLoadingScreen();
              vm.isRequestProcessing = false;
            },
            function () {
              hideLoadingScreen();
              NotificationService.error();
              vm.isRequestProcessing = false;
            },

              [
                {
                  id: vm.userId,
                  give: vm.classId.toString(),
                  take: classId.toString()
                }
              ]

          );
        },
        function () {
          hideLoadingScreen();

          vm.isRequestProcessing = false;
        }
      );
    };

    vm.cancelSwapsFromClass = function (classId) {
      for (j = 0; j < vm.addedSwaps.length; j++) {
        if (vm.addedSwaps[j].give == classId) {
          vm.addedSwaps.splice(j, 1);
          j--;
        }
      }
      for (j = 0; j < vm.removedSwaps.length; j++) {
        if (vm.removedSwaps[j].take == classId) {
          vm.removedSwaps.splice(j, 1);
          j--;
        }
      }
    };

    vm.findSwapIndexInArray = function (array, take) {
      for (var i = 0; i < array.length; i++) {
        if (array[i].take == take) return i;
      }
      return -1;
    };

    vm.arrayContainsClass = function (array, classId) {
      for (i = 0; i < array.length; i++) {
        if (array[i]['classId'] == classId) return true;
      }
      return false;
    };

    vm.addOrRemoveSwap = function (calEvent) {
      var wasChosen = vm.events[calEvent.id].wasChosen;
      var isChosen = vm.events[calEvent.id].isChosen;
      var take = vm.events[calEvent.id].classId.toString();
      var swap = {
        id: vm.userId,
        give: vm.classId.toString(),
        take: take
      };

      if (isChosen) {
        if (wasChosen) vm.removedSwaps.push(swap);
        else vm.addedSwaps.splice(vm.findSwapIndexInArray(vm.addedSwaps, take), 1);
      } else {
        if (!wasChosen) vm.addedSwaps.push(swap);
        else vm.removedSwaps.splice(vm.findSwapIndexInArray(vm.removedSwaps, take), 1);
      }

      angular.element("#calendar").fullCalendar('removeEvents');
      vm.events[calEvent.id].backgroundColor = vm.events[calEvent.id].backgroundColor == 'red' ? 'green' : 'red';
      vm.events[calEvent.id].borderColor = 'white';
      vm.events[calEvent.id].isChosen = !isChosen;
      angular.element("#calendar").fullCalendar('addEventSource', vm.events);
      vm.refreshHoverHandler();
    };

    vm.changeCourse = function (classId) {

      showLoadingScreen();

      if (classId == vm.classId) {
        vm.classId = null;
        vm.events = angular.copy(vm.myEvents);
        angular.element("#calendar").fullCalendar('removeEvents');
        angular.element("#calendar").fullCalendar('addEventSource', vm.events);
        vm.refreshHoverHandler();
        hideLoadingScreen();
        return;
      }

      vm.loadTimetable(function (response) {
        if (vm.arrayContainsClass(response.data, classId)) {
          vm.classId = classId;
          vm.isRequestProcessing = true;
          ApiService.get(
            '/student/' + vm.userId + '/timetable/' + classId,
            function successCallback(response) {
              angular.element("#calendar").fullCalendar('removeEvents');
              vm.swapTerms = response.data;
              vm.events = angular.copy(vm.myEvents);

              for (i = 0; i < vm.events.length; i++) {
                if (vm.events[i].classId == classId) vm.events[i].backgroundColor = 'blue';
              }

              for (i = 0; i < vm.swapTerms.length; i++) {
                var tmp = vm.swapTerms[i];

                var isAdded = false;
                for (j = 0; j < vm.addedSwaps.length; j++) {
                  if (vm.addedSwaps[j].take == tmp['classId']) isAdded = true;
                }
                var isRemoved = false;
                for (j = 0; j < vm.removedSwaps.length; j++) {
                  if (vm.removedSwaps[j].take == tmp['classId']) isRemoved = true;
                }

                var wasChosen = tmp['chosen'];
                var isChosen = (wasChosen && !isRemoved) || (!wasChosen && isAdded);
                var backgroundColor = tmp['available'] ? 'orange' : (isChosen ? 'green' : 'red');

                vm.events.push({
                  id: vm.events.length,
                  classId: tmp['classId'],
                  title: vm.makeTitle(tmp),
                  fullTitle: vm.makeFullTitle(tmp),
                  start: vm.makeDate(tmp['weekday'], tmp['begTime']),
                  end: vm.makeDate(tmp['weekday'], tmp['endTime']),
                  week: tmp['week'],
                  wasChosen: wasChosen,
                  isChosen: isChosen,
                  isMine: false,
                  isAvailableNow: tmp['available'],
                  backgroundColor: backgroundColor,
                  borderColor: 'white',
                  className: ['clickable']
                });
              }

              angular.element("#calendar").fullCalendar('addEventSource', vm.events);
              vm.refreshHoverHandler();
              hideLoadingScreen();
              vm.isRequestProcessing = false;
            },
            function errorCallback() {
              hideLoadingScreen();
              NotificationService.error();
              vm.isRequestProcessing = false;
            }
          );
        } else {
          afterLoadTimetable(response);
          hideLoadingScreen();
          NotificationService.success("Watch out!",
            "It seems your timetable has changed since you last viewed it " +
            "(probably one of your previous exchanges was completed).<br><br>" +
            "Take a look at the updated timetable before selecting the subject.");
        }
      });
    };

    vm.loadTimetable = function (successCallback) {
      showLoadingScreen();
      vm.events = [];
      vm.classId = 0;

      vm.isRequestProcessing = true;
      ApiService.get(
        '/student/' + vm.userId + '/timetable/',
        successCallback,
        function (response) {
          hideLoadingScreen();
          if (response.status == 404) {
            NotificationService.error('Oops!',
              'You\'re not assigned to any existing market. <br>' +
              'If you think this is an error, contact your year\'s representative.');
            $location.path('/');
          } else {
            NotificationService.error();
          }

          vm.isRequestProcessing = false;
        }
      );
    };

    vm.refreshHoverHandler = function () {
      if (!vm.zoomEnabled) return;
      angular.element(".fc-time-grid-event").hover(
        function () {
          var m = angular.element("#zoom-container");
          var b = angular.element(this);
          var el = b.clone();
          el.addClass("zoomed-term");
          m.html(el);
        }, function () {
          angular.element(".zoomed-term").remove();
        }
      );
    };

    vm.saveChanges = function () {

      showLoadingScreen();

      //the POST and DELETE calls have to be synchronous to handle refresh token correctly
      var successCallback = function () {
        NotificationService.success('Hooray!', 'All your preferences have been saved.');
        vm.addedSwaps = [];
        vm.removedSwaps = [];
        vm.loadTimetable(function (response) {
          afterLoadTimetable(response);
          hideLoadingScreen();
        });
      };

      var errorCallback = function () {
        hideLoadingScreen();
        NotificationService.error();
      };

      var sendRemoved = function () {
        if (vm.removedSwaps.length > 0) {
          vm.isRequestProcessing = true;
          ApiService.delete(
            '/swap/many',
            function () {
              successCallback();
              vm.isRequestProcessing = false;
            },
            function (response) {
              errorCallback(response);
              vm.isRequestProcessing = false;
            },
            vm.removedSwaps
          );
        } else {
          successCallback();
          vm.isRequestProcessing = false;
        }
      };

      var sendAdded = function () {
        if (vm.addedSwaps.length > 0) {
          vm.isRequestProcessing = true;
          ApiService.post(
            '/swap/many',
            function () {
              sendRemoved();
              vm.isRequestProcessing = false;
            },
            function (response) {
              errorCallback(response);
              vm.isRequestProcessing = false;
            },
            vm.addedSwaps
          );
        } else sendRemoved();
      };

      sendAdded();
    };

    vm.allChangesSaved = function () {
      return vm.addedSwaps.length == 0 && vm.removedSwaps.length == 0;
    };

    vm.isDevProfile = function () {
      return ProfileService.isDevProfile();
    };

    vm.isDebugEnabled = function () {
      return ProfileService.isDebugEnabled();
    };

    vm.uiConfig = {
      calendar: {
        defaultView: $window.outerWidth >= 768 ? 'agendaWeek' : 'agendaDay',
        defaultDate: '2001-01-01',    //Monday, as a reference for the other days
        minTime: '08:00:00',
        maxTime: '22:00:00',
        editable: false,
        weekends: false,
        nowIndicator: false,
        slotEventOverlap: false,
        columnFormat: 'ddd',
        header: {
          left: 'prev next',
          center: null,
          right: 'agendaWeek agendaDay'
        },

        eventClick: function (calEvent) {
          angular.element(".zoomed-term").remove();
          if (vm.events[calEvent.id].className.indexOf('clickable') == -1) return;
          if (vm.events[calEvent.id].isMine) {
            vm.changeCourse(vm.events[calEvent.id].classId, vm.events[calEvent.id].host);
          } else {
            if (vm.events[calEvent.id].isAvailableNow) vm.exchangeNow(calEvent);
            else vm.addOrRemoveSwap(calEvent);
          }
        },

        eventRender: function (event, element) {
          var timeformat = event.start.format('HH:mm') + ' - ' + event.end.format('HH:mm');
          if (event.week != 'all') timeformat += (' ' + event.week);
          element.find('.fc-time').html(timeformat);
          element.find('div.fc-title').html(element.find('div.fc-title').text());
        },

        viewRender: function (view) {
          if (moment('2001-01-02').isAfter(view.start, 'day')) {
            angular.element(".fc-prev-button").prop('disabled', true);
            angular.element(".fc-prev-button").addClass('fc-state-disabled');
          } else {
            angular.element(".fc-prev-button").prop('disabled', false);
            angular.element(".fc-prev-button").removeClass('fc-state-disabled');
          }
          if (moment(view.end).isAfter('2001-01-06', 'day')) {
            angular.element(".fc-next-button").prop('disabled', true);
            angular.element(".fc-next-button").addClass('fc-state-disabled');
          } else {
            angular.element(".fc-next-button").prop('disabled', false);
            angular.element(".fc-next-button").removeClass('fc-state-disabled');
          }
        },

        eventAfterAllRender: function () {
          vm.refreshHoverHandler();
        }
      }
    };

    var loadingScreen = "<div class=\"loading-screen\">" +
      "<span class=\"loading-screen-content\">" +
      "<h1><span class=\"glyphicon glyphicon-refresh\"></span></h1><br>" +
      "<h2>Please wait...</h2>" +
      "</span>" +
      "</div>";

    var showLoadingScreen = function () {
      var existingLoadingScreen = angular.element(".loading-screen");
      if (existingLoadingScreen.length > 0) existingLoadingScreen.show();
      else angular.element(".fc-view").append(angular.element(loadingScreen));
    };

    var hideLoadingScreen = function () {
      angular.element(".loading-screen").hide();
    };

    vm.loadTimetable(afterLoadTimetable);

    angular.element(document).bind('mousemove', function (e) {
      angular.element("#zoom-container").css({
        left: e.pageX + 20,
        top: e.pageY
      });
    });

    function confirmLeave(e) {
      if (vm.allChangesSaved()) return;
      if (!e) e = $window.event;
      e.cancelBubble = true;
      e.returnValue = 'Are you sure you want to leave without saving your preferences?';

      if (e.stopPropagation) {
        e.stopPropagation();
        e.preventDefault();
      }
    }

    $window.onbeforeunload = confirmLeave;

  }
})();
