Feature: Login to Application
    As a valide user
    I want to log in into Application

    Scenario: Valid login 
    Given I open login page 
    When I submit login
    Then I should see homepage
