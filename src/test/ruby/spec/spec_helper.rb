require 'capybara'
require 'capybara/rspec'
require 'selenium-webdriver'

require_relative '../lib/helpers'

Capybara.register_driver(:chrome) { |app| Capybara::Selenium::Driver.new(app, browser: :chrome) }
Capybara.default_driver = :chrome

RSpec.configure do |config|
  config.expect_with(:rspec) do |expectations|
    expectations.syntax = :expect
    expectations.include_chain_clauses_in_custom_matcher_descriptions = true
  end
  config.mock_with(:rspec) { |mocks| mocks.verify_partial_doubles = true }
  config.shared_context_metadata_behavior = :apply_to_host_groups

  config.before(:each) { visit 'http://localhost:8080/' }
end

SPEC_ROOT = File.expand_path('../..', __FILE__)
