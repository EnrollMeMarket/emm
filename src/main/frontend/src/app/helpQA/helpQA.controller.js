(function() {
  'use strict';

  angular
    .module('emm')
    .controller('HelpQAController', HelpQAController);

  /** @ngInject */
  function HelpQAController() {

    var vm = this;

    vm.qa = [
      {
        'q': 'What does \'Available Now\' label mean?',
        'a': 'This is to indicate that someone is waiting for the term you currently have, and offers this one in exchange.<br><br>' +
        'Clicking it will change your timetable immediately, with a guarantee of success - be careful though, as this action cannot be undone!'
      },
      {
        'q': 'I clicked a term with \'Available Now\' label, but my timetable hasn\'t changed. Should I be worried?',
        'a': 'Certainly not - it seems somebody else had taken this term before you. ' +
        'Remember that the market might be used by many people at the same time, so the situation is likely to change within minutes - or even seconds.<br><br>' +
        'The term will be marked as wanted by you though, so you still have a chance to get it when possible.'
      },
      {
        'q': 'Why can\'t I see some terms in the chosen chourse?',
        'a': 'Only the terms that don\'t interfere with your current timetable are displayed.'
      },
      {
        'q': 'Can I take part in two markets at once?',
        'a': 'Unfortunately not, but we\'re planning to introduce this feature as soon as possible.'
      },
      {
        'q': 'My timetable in Enroll-Me was not updated after a swap. Why?',
        'a': 'Don\'t worry, all you need is a little patience. You will see all the changes in Enroll-Me after both stages of the market have been completed.'
      },
      {
        'q': 'Can I undo an exchange?',
        'a': 'Not if it has already been completed (for example if you used Available Now option). Your action might have changed another user\'s timetable as well, so we can\'t undo it so easily.<br><br>' +
        'You should be very careful when you choose your terms. If we warn you there\'s no way back, we really mean it!'
      },
      {
        'q': 'Why does the system tell me I\'m not assigned to any market?',
        'a': 'You should ask your year\'s representative as soon as possible - they will have to check this manually.'
      },
      {
        'q': 'I\'ve discovered a flaw in site\'s security. What should I do?',
        'a': 'We kindly ask you to inform the Enroll-Me Market team as soon as possible. Please do not take advantage ' +
        'of any security issues - we all want the application to serve the students safely and correctly.'
      },
      {
        'q': 'Why llama?',
        'a': 'Why not?'
      }
    ];

  }
})();
