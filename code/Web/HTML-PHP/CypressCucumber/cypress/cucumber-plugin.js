const cucumber = require("cypress-cucumber-preprocessor").default;
module.exports = (on, config) => {
//on is used to hook into various events Cypress emits
   //config is the resolved Cypress config
   on("file:preprocessor", cucumber());
   return Object.assign({}, config, {
       fixturesFolder: "cypress/fixtures",
       integrationFolder: "cypress/tests/BDD",
       screenshotsFolder: "cypress/screenshots",
       videosFolder: "cypress/videos",
       supportFile: "cypress/support/index.js",
   });
}; 