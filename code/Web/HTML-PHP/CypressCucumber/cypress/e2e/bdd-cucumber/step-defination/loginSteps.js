import { Given, When, Then} from '@badeball/cypress-cucumber-preprocessor'

Given('I open login page', () => {
  cy.visit('http://193.54.227.208/~saephp12/FormConnexion.php');
});

When('I submit login',() =>{
    //fill username
    cy.get('input[name="username"]').type('LeFlicDu31')
    //fill password
    cy.get('input[name="password"]').type('flic')
    //submit form
    cy.get('input[name="connexion"]').click()
})

//devrait etre dans homepageSteps.js
Then('I should see homepage',() =>{
    cy.get('.menu-item').should('be.visible')
})