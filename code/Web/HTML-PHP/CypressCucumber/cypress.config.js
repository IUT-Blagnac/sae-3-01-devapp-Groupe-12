const { defineConfig } = require("cypress");
const preprocessor = require("@badeball/cypress-cucumber-preprocessor");
const browserify = require("@badeball/cypress-cucumber-preprocessor/browserify");

async function setupNodeEvents(on, config) {
  // This is required for the preprocessor to be able to generate JSON reports after each run, and more,
await preprocessor.addCucumberPreprocessorPlugin(on, config);
on("file:preprocessor", browserify.default(config));
return config;
}

module.exports = defineConfig({
  e2e: {
    setupNodeEvents,
    baseUrl:'http://193.54.227.208/~saephp12/',
    specPattern: "cypress/e2e/bdd-cucumber/features/*.feature",
    watchForFileChanges:false,
    chromeWebSecurity: false,
    experimentalModifyObstructiveThirdPartyCode: true,
    defaultCommandTimeout: 10000
 },
});