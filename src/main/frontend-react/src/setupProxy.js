const { createProxyMiddleware } = require('http-proxy-middleware');
//
module.exports = function(app) {
  // app.use(
  //   '/ws',
  //   createProxyMiddleware({
  //     target: 'http://localhost:8089/ws',
  //     changeOrigin: true,
  //     ws: true
  //   })
  // );
};
