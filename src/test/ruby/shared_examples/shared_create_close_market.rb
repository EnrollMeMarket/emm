require_relative '../spec/spec_helper'
require_relative '../lib/constants'

include LoginPage, MarketsTab

shared_examples 'Create and close market' do |is_foreman|
  it "As #{is_foreman ? 'foreman' : 'admin'}" do
    test_market_name = Time.now.to_i.to_s

    find(is_foreman ? FOREMAN_RADIO_BUTTON : ADMIN_RADIO_BUTTON).click
    find(DEBUG_LOGIN_BUTTON).click

    find(MARKETS_TAB).click
    new_market_button = find(NEW_MARKET_BUTTON)
    expect(new_market_button.text).to eq('New Market')
    new_market_button.click

    find(MARKET_NAME_INPUT).set(test_market_name)
    find(CHOOSE_FILES_INPUT).set(test_files)

    2.times { find(MARKET_NEXT_BUTTON).click }
    find(SELECT_ALL_CHECKBOX).click
    find(MARKET_NEXT_BUTTON).click
    find(START_NOW_BUTTON).click
    wait_and_click(MARKET_CREATED_OK_BUTTON)

    created_market = find_all(CREATED_MARKETS_NAMES).first
    expect(created_market.text).to eq(test_market_name)
    find_all(CREATED_MARKETS_DETAILS_BUTTONS).first.click
    find(CLOSE_MARKET_BUTTON).click
    wait_and_click(FINISH_MARKET_YES_BUTTON)
  end
end
