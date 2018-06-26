require_relative 'spec_helper'
require_relative '../lib/constants'

include HelpTab, LoginPage, NavigationBar

feature 'Help tab' do
  it 'has got all crucial elements' do
    find(STUDENT_RADIO_BUTTON).click
    find(DEBUG_LOGIN_BUTTON).click
    find(HELP_TAB).click

    expect(find(HELP_HEADER_TEXT).text).to eq('Q&A')
    expect(find(HELP_WELCOME_TEXT).text.strip).to eq('New here? Take a look at a brief introduction!')

    questions = find_all(QUESTIONS)
    expect(questions.length).to eq(9)
    expect(questions.map(&:text)).to eq(EXPECTED_QUESTIONS)

    answers = []
    questions.each do |question|
      question.click
      answers.push(find(ANSWER).text)
    end
    expect(answers).to eq(EXPECTED_ANSWERS)

    find(LOGOUT_BUTTON).click
  end
end
