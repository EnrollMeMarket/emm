def expect_tabs_not_logged
  expect(find(EMM_TAB).text.strip).to eq('Enroll Me Market')
  expect(page).not_to have_css(CALENDAR_TAB)
  expect(page).not_to have_css(MARKETS_TAB)
  expect(page).not_to have_css(EXCHANGES_SUMMARY_TAB)
  expect(page).not_to have_css(HELP_TAB)
end

def expect_tabs_logged(is_student)
  expect(find(EMM_TAB).text.strip).to eq('Enroll Me Market')
  expect(find(CALENDAR_TAB).text).to eq('Calendar')

  if is_student
    expect(page).not_to have_css(MARKETS_TAB)
  else
    expect(find(MARKETS_TAB).text).to eq('Markets')
  end

  expect(find(EXCHANGES_SUMMARY_TAB).text).to eq('Exchanges Summary')
  expect(find(HELP_TAB).text).to eq('Help')
end

def page_title(page_number)
  find("div[ng-show='helpIntro.page==#{page_number}'] h2").text
end

def test_files
  ["#{SPEC_ROOT}/test_data/test_plan.txt", "#{SPEC_ROOT}/test_data/test_terms_descriptions.txt"]
end

def wait_and_click(selector)
  sleep(0.5) until (element = find_all(selector).first)
  element.click
end
