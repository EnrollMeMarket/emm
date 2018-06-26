require_relative 'spec_helper'
require_relative '../lib/constants'

include Calendar, LoginPage, MarketsTab, NavigationBar

feature 'Empty calendar for student without open market' do
  TEST_INDEX = '654321'.freeze

  it 'Correct warning is displayed' do
    find(INDEX_INPUT).set(TEST_INDEX)
    find(DEBUG_LOGIN_BUTTON).click
    find(CALENDAR_TAB).click
    expect(find(OOPS_WARNING_HEADER).text).to eq('Oops!')
    expect(find(OOPS_WARNING_TEXT).text).to eq(OOPS_WARNING_EXPECTED_TEXT)
    find(OOPS_WARNING_CLOSE_BUTTON).click
  end

  after(:each) { find(LOGOUT_BUTTON).click }
end
