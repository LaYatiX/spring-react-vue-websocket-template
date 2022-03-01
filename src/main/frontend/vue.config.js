module.exports = {
  devServer: {
    port: 4200,
    watchOptions: {
      ignored: [/node_modules/],
    },
    proxy: {
      '^/api': {
        target: 'http://localhost:9000/',
      },
    },
    disableHostCheck: true,
  },

  lintOnSave: process.env.NODE_ENV !== 'production',
  transpileDependencies: ['vuetify'],
};
