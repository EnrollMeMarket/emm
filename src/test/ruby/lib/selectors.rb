module IntroductionGuide
  INTRODUCTION_LINK = "a[href='helpIntro']".freeze

  INTRODUCTION_TEXT = 'div.jumbotron h2.text-center'.freeze
  INTRO_NEXT_BUTTON = "button[ng-click='helpIntro.page=helpIntro.page+1']".freeze
  INTRO_PREV_BUTTON = "button[ng-click='helpIntro.page=helpIntro.page-1']".freeze
  PAGE_TITLES = [
    'Introduction guide',
    'Welcome to Enroll-Me Market!',
    'Your timetable',
    'Introducing preferences',
    'Introducing preferences',
    'Available Now',
    'Exchanges Summary',
    "That's all!"
  ].freeze
end

module HelpTab
  HELP_HEADER_TEXT = 'div.jumbotron h2'.freeze
  HELP_WELCOME_TEXT = 'div.well.text-center'.freeze

  QUESTIONS = 'span.ng-binding.ng-scope'.freeze
  EXPECTED_QUESTIONS = [
    "What does 'Available Now' label mean?",
    "I clicked a term with 'Available Now' label, but my timetable hasn't changed. Should I be worried?",
    "Why can't I see some terms in the chosen chourse?",
    'Can I take part in two markets at once?',
    'My timetable in Enroll-Me was not updated after a swap. Why?',
    'Can I undo an exchange?',
    "Why does the system tell me I'm not assigned to any market?",
    "I've discovered a flaw in site's security. What should I do?",
    'Why llama?'
  ].freeze

  ANSWER = "span[ng-bind-html='qa.a']".freeze
  EXPECTED_ANSWERS = [
    'This is to indicate that someone is waiting for the term you currently have, and offers this one in exchange. Clicking it will change your timetable immediately, with a guarantee of success - be careful though, as this action cannot be undone!',
    'Certainly not - it seems somebody else had taken this term before you. Remember that the market might be used by many people at the same time, so the situation is likely to change within minutes - or even seconds. The term will be marked as wanted by you though, so you still have a chance to get it when possible.',
    "Only the terms that don't interfere with your current timetable are displayed.",
    "Unfortunately not, but we're planning to introduce this feature as soon as possible.",
    "Don't worry, all you need is a little patience. You will see all the changes in Enroll-Me after both stages of the market have been completed.",
    "Not if it has already been completed (for example if you used Available Now option). Your action might have changed another user's timetable as well, so we can't undo it so easily. You should be very careful when you choose your terms. If we warn you there's no way back, we really mean it!",
    "You should ask your year's representative as soon as possible - they will have to check this manually.",
    'We kindly ask you to inform the Enroll-Me Market team as soon as possible. Please do not take advantage of any security issues - we all want the application to serve the students safely and correctly.',
    'Why not?'
  ].freeze
end

module LoginPage
  APP_NAME_TEXT = 'div.jumbotron.text-center h1'.freeze
  INDEX_INPUT = "input[name='debugIndex']".freeze

  EXTERNAL_LOGIN_BUTTON = "button[ng-click='main.oauthLogin()']".freeze
  DEBUG_LOGIN_BUTTON = "button[ng-click='main.loginDebug()']".freeze

  STUDENT_RADIO_BUTTON = "input[value='STUDENT']".freeze
  FOREMAN_RADIO_BUTTON = "input[value='STAROSTA']".freeze
  ADMIN_RADIO_BUTTON = "input[value='ADMIN']".freeze
end

module MarketsTab
  NEW_MARKET_BUTTON = "button[ng-click='marketList.goToCreateMarket()']".freeze
  MARKET_NAME_INPUT = "input[name='marketName']".freeze
  CHOOSE_FILES_INPUT = 'input#fileToUpload'.freeze
  MARKET_NEXT_BUTTON = "button[ng-click='createMarket.nextPage()']".freeze
  SELECT_ALL_CHECKBOX = "input[data-ng-model='createMarket.selectAll']".freeze
  START_NOW_BUTTON = "button[ng-click='createMarket.startNowFunction()']".freeze
  MARKET_CREATED_OK_BUTTON = "button[ng-click='notification.close()']".freeze
  CREATED_MARKETS_NAMES = 'div.col-xs-3.col-md-3.col-lg-3.ng-binding'.freeze
end

module NavigationBar
  EMM_TAB = 'a.navbar-brand:first-child'.freeze
  CALENDAR_TAB = "a[href='/calendarView']".freeze
  MARKETS_TAB = "a[href='/markets']".freeze
  EXCHANGES_SUMMARY_TAB = "a[href='/exchangesSummary']".freeze
  HELP_TAB = "a[href='/helpQA']".freeze

  LOGGED_IN_AS_TEXT = 'span.acme-navbar-text.ng-binding'.freeze
  USER_ROLE_LABEL = 'span.acme-navbar-text.ng-binding'.freeze
  LOGOUT_BUTTON = "span[ng-click='vm.logout()']".freeze
end
