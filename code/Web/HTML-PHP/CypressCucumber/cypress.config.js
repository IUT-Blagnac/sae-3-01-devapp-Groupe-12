const { defineConfig } = require("cypress");
const browserify = require('@cypress/browserify-preprocessor');
const cucumber = require('cypress-cucumber-preprocessor').default; // Add this line

module.exports = defineConfig({
  video: false,
  defaultCommandTimeout: 5000,
  pageLoadTimeout: 10000,
  e2e: {
    setupNodeEvents(on, config) {
      const options = {
        ...browserify.defaultOptions,
        typescript: require.resolve('typescript'),
        
      };
      
      on('file:preprocessor', cucumber(options));
      
    },
    specPattern: 'cypress/e2e/login.feature',
  },
});