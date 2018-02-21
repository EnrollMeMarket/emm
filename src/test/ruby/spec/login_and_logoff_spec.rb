require_relative 'spec_helper'
require_relative '../lib/selectors'

include LoginPage, NavigationBar

feature 'Login and logoff' do
  TEST_INDEX = '123456'.freeze

  before(:each) { expect_tabs_not_logged }

  # There is a bug raised
  xit 'as student' do
    find(STUDENT_RADIO_BUTTON).click
    find(DEBUG_LOGIN_BUTTON).click
    expect(find(USER_ROLE_LABEL).text).to include('Student')
    expect_tabs_logged(true)
  end

  it 'as year representative' do
    find(FOREMAN_RADIO_BUTTON).click
    find(DEBUG_LOGIN_BUTTON).click
    expect(find(USER_ROLE_LABEL).text).to include('Starosta')
    expect_tabs_logged(false)
  end

  it 'as admin' do
    find(ADMIN_RADIO_BUTTON).click
    find(DEBUG_LOGIN_BUTTON).click
    expect(find(USER_ROLE_LABEL).text).to include('Admin')
    expect_tabs_logged(false)
  end

  it "login as student wih #{TEST_INDEX} index" do
    find(INDEX_INPUT).set(TEST_INDEX)
    find(DEBUG_LOGIN_BUTTON).click
    expect(find(USER_ROLE_LABEL).text).to include('Student')
    expect(find(LOGGED_IN_AS_TEXT).text).to include(TEST_INDEX)
    expect_tabs_logged(true)
  end

  after(:each) do
    find(LOGOUT_BUTTON).click
    expect_tabs_not_logged
  end
end
