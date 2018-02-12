/**
 *  Welcome to your gulpfile!
 *  The gulp tasks are splitted in several files in the gulp directory
 *  because putting all here was really too long
 */

'use strict';

var gulp = require('gulp');
var klawSync = require('klaw-sync');

/**
 *  This will load all necessary files in the gulp directory
 *  in order to load all gulp tasks
 */
klawSync('./gulp', { nodir: true, ignore: ['.*'] }).map(function(file) {
   require(file.path);
});

/**
 *  Default task clean temporaries directories and launch the
 *  main optimization build task
 */
gulp.task('default', ['clean'], function () {
  gulp.start('build');
});
