module.exports = {
    // Other configurations...
    transformIgnorePatterns: [
      "/node_modules/(?!(axios)/)"
    ],
    transform: {
      "^.+\\.(js|jsx|ts|tsx)$": "babel-jest",
    },
  };
  