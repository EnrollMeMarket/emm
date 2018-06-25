# End-to-End tests

### Prerequisites
Install:
* Ruby 2.4.1
* Appium 1.7.1
* ChromeDriver 2.36

Run:
* `cd src/test/ruby`
* `gem install bundler`
* `bundle install`

Run EnrollMeMarket on `http://localhost:8080/`. See `emm/readme.md` for instructions.

### Run all tests
`bundle exec rspec spec/`

### Run specific test
`bundle exec rspec spec/login_page_spec.rb`
