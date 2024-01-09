const { defineConfig } = require("cypress");

module.exports = defineConfig({
  video: false,
  defaultCommandTimeout: 5000,
  pageLoadTimeout: 10000,
  ignoreTestFiles: ["*.js","*.md"],

  e2e: {
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },
  
});
