'use strict';

var path = require('path');
var gulp = require('gulp');
var conf = require('./conf');

var browserSync = require('browser-sync');
var browserSyncSpa = require('browser-sync-spa');
var nodemon = require('gulp-nodemon');

var util = require('util');

var proxyMiddleware = require('http-proxy-middleware');
var modRewrite = require('connect-modrewrite');

function browserSyncInit(baseDir, browser) {
  browser = browser === undefined ? 'default' : browser;

  var routes = null;
  if(baseDir === conf.paths.src || (util.isArray(baseDir) && baseDir.indexOf(conf.paths.src) !== -1)) {
    routes = {
      '/bower_components': 'bower_components'
    };
  }

  var server = {
    baseDir: baseDir,
    middleware: [
      proxyMiddleware('/api/', {target: 'http://localhost:8080', changeOrigin: true, pathRewrite: {'^/api' : ''}}),
      modRewrite([
        '!(\\.\\w+)$ /index.html [L]',
      ])
    ],
    routes: routes
  };

  browserSync.instance = browserSync.init({
    startPath: '/',
    open: true,
    server: server,
    browser: browser
  });
}

browserSync.use(browserSyncSpa({
  selector: '[ng-app]'
}));

gulp.task('serve', ['config', 'watch'], function () {
  browserSyncInit([path.join(conf.paths.tmp, '/serve'), conf.paths.src]);
});

gulp.task('serve:integ', ['config:integ', 'build'], function () {
  browserSyncInit(conf.paths.dist);
});

gulp.task('serve:dist', ['config:build', 'build'], function () {
  browserSyncInit(conf.paths.dist);
});

gulp.task('serve:e2e', ['config', 'inject'], function () {
  browserSyncInit([conf.paths.tmp + '/serve', conf.paths.src], []);
});

gulp.task('serve:e2e-integ', ['config:integ', 'build'], function () {
  browserSyncInit(conf.paths.dist, []);
});

gulp.task('serve:e2e-dist', ['config:build', 'build'], function () {
  browserSyncInit(conf.paths.dist, []);
});
