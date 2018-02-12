'use strict';

var path = require('path'),
  gulp = require('gulp'),
  conf = require('./conf'),
  gulpNgConfig = require('gulp-ng-config');

gulp.task('config', function () {
  gulp.src(path.join(conf.paths.src, '../src/app/config.json'))
    .pipe(gulpNgConfig('emm.config', {
      environment: 'dev'
    }))
    .pipe(gulp.dest(path.join(conf.paths.src, '../src/app/')))
});

gulp.task('config:integ', function () {
  gulp.src(path.join(conf.paths.src, '../src/app/config.json'))
    .pipe(gulpNgConfig('emm.config', {
      environment: 'integ'
    }))
    .pipe(gulp.dest(path.join(conf.paths.src, '../src/app/')))
});

gulp.task('config:dev', function () {
  gulp.src(path.join(conf.paths.src, '../src/app/config.json'))
    .pipe(gulpNgConfig('emm.config', {
      environment: 'dev'
    }))
    .pipe(gulp.dest(path.join(conf.paths.src, '../src/app/')))
});

gulp.task('config:build', function () {
  gulp.src(path.join(conf.paths.src, '../src/app/config.json'))
    .pipe(gulpNgConfig('emm.config', {
      environment: 'prod'
    }))
    .pipe(gulp.dest(path.join(conf.paths.src, '../src/app/')))
});
