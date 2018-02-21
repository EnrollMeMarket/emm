require_relative 'spec_helper'
require_relative '../lib/selectors'
require_relative '../shared_examples/shared_create_market'

include NavigationBar

feature 'Create market' do
  include_examples('Create market', true)
  include_examples('Create market', false)

  after(:each) { find(LOGOUT_BUTTON).click }
end
