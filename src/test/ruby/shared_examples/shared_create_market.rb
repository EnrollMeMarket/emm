require_relative '../spec/spec_helper'
require_relative '../lib/selectors'

include LoginPage, MarketsTab

shared_examples 'Create market' do |is_foreman|
  it "As #{is_foreman ? 'foreman' : 'admin'}" do
    test_market_name = Time.now.to_i.to_s

    find(is_foreman ? FOREMAN_RADIO_BUTTON : ADMIN_RADIO_BUTTON).click
    find(DEBUG_LOGIN_BUTTON).click

    find(MARKETS_TAB).click
    button = find(NEW_MARKET_BUTTON)
    expect(button.text).to eq('New Market')
    button.click

    find(MARKET_NAME_INPUT).set(test_market_name)
    find(CHOOSE_FILES_INPUT).set(test_files)

    2.times { find(MARKET_NEXT_BUTTON).click }
    find(SELECT_ALL_CHECKBOX).click
    find(MARKET_NEXT_BUTTON).click
    find(START_NOW_BUTTON).click
    find(MARKET_CREATED_OK_BUTTON).click

    expect(find_all(CREATED_MARKETS_NAMES).first.text).to eq(test_market_name)
  end
end
