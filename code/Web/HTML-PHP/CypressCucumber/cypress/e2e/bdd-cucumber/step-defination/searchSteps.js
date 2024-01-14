import { Given, When, Then} from '@badeball/cypress-cucumber-preprocessor'

Given('I type in the searchbar', () => {
    cy.visit('http://193.54.227.208/~saephp12/index.php');
    cy.get('.search-input').type('Clarinette');
  });

When('I submit the search',() =>{
    cy.get('.search-button').click()
})

//devrait etre dans homepageSteps.js
Then('I should see the item',() =>{
    cy.get('h3').contains('Clarinette')
})