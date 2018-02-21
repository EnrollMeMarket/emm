require_relative 'spec_helper'
require_relative '../lib/selectors'

include IntroductionGuide, HelpPage, LoginPage, NavigationBar

feature 'Introduction guide' do
  it 'has got all crucial elements' do
    find(STUDENT_RADIO_BUTTON).click
    find(DEBUG_LOGIN_BUTTON).click
    find(INTRODUCTION_LINK).click

    expect(find(INTRODUCTION_TEXT).text).to eq('Introduction')

    (1..7).each do |page_number|
      expect(page_title(page_number)).to eq(PAGE_TITLES[page_number])
      if page_number == 1
        expect(page).not_to have_css(PREV_BUTTON)
      else
        expect(page).to have_css(PREV_BUTTON)
      end

      if page_number == 7
        expect(page).not_to have_css(NEXT_BUTTON)
      else
        find(NEXT_BUTTON).click
      end
    end

    find(LOGOUT_BUTTON).click
  end
end
