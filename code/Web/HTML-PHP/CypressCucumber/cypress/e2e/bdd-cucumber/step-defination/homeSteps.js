import { Given, When, Then} from '@badeball/cypress-cucumber-preprocessor'

Given('I open panier page', () => {
  cy.visit('http://193.54.227.208/~saephp12/Panier.php');
});

When('I click the home icon',() =>{
    cy.get('.home-link').click()
})