import { Given, When, Then} from 'cypress-cucumber-preprocessor/steps'

Given('I open login page',()=> {
    cy.visit('http://193.54.227.208/~saephp12/FormConnexion.php')
})

When('I submit login',() =>{
    //fill username
    cy.get('#username').type('username')
    //fill password
    cy.get('#password').type('password')
    //submit form
    cy.get('input[name="connexion"]').click()
})

//devrait etre dans homepageSteps.js
Then('I should see homepage',() =>{
    cy.get('.menu-item').should('be.visible')
})