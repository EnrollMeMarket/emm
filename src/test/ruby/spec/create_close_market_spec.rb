require_relative 'spec_helper'
require_relative '../lib/constants'
require_relative '../shared_examples/shared_create_close_market'

include NavigationBar

feature 'Create and close market' do
  include_examples('Create and close market', true)
  include_examples('Create and close market', false)

  after(:each) { find(LOGOUT_BUTTON).click }
end
