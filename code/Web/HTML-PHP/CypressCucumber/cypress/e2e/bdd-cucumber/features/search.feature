Feature: Search
    Scenario: Search for an item in the searchbar

    Given I type in the searchbar
    When I submit the search
    Then I should see the item
