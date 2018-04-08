require_relative 'spec_helper'
require_relative '../lib/constants'

include LoginPage, NavigationBar

feature 'Login page' do
  APP_NAME = 'Enroll Me Market'.freeze

  it 'has got all crucial elements' do
    expect(find(EMM_TAB).text.strip).to eq(APP_NAME)

    expect(find(APP_NAME_TEXT).text).to eq(APP_NAME)
    expect(page).to have_css(INDEX_INPUT)

    expect(find(EXTERNAL_LOGIN_BUTTON).text.strip).to eq('Login with external service')
    expect(find(DEBUG_LOGIN_BUTTON).text.strip).to eq('Debug login')

    expect(page).to have_css(STUDENT_RADIO_BUTTON)
    expect(page).to have_css(FOREMAN_RADIO_BUTTON)
    expect(page).to have_css(ADMIN_RADIO_BUTTON)
  end
end
