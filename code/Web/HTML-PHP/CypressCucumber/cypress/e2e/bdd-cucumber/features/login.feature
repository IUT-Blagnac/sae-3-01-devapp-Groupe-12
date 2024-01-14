Feature: Login
    Scenario: Login user with correct id and password

    Given I open login page
    When I submit login
    Then I should see homepage
