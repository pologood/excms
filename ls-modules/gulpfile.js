"use strict";

//加载模块
var gulp = require('gulp'),
    uglify = require('gulp-uglify'),
    concat = require('gulp-concat'),
    rename = require('gulp-rename'),
    gulpif = require('gulp-if'),
    pkg = require('./package'),
    plugins = require('gulp-load-plugins')();

console.info(plugins);

//获取参数
// 开发环境 DEV , 生产环境 PRO
var argv = require('minimist')(process.argv.slice(2), {
    default: {
        env: process.env.NODE_ENV || 'dev'
    }
});

const date = {
    day: new Date().getDate(),
    month: ('January February March April May June July August September October November December').split(' ')[new Date().getMonth()],
    year: new Date().getFullYear(),
};

const license = `${`
/**
 * core ${pkg.version}
 * ${pkg.description}
 * ${pkg.homepage}
 *
 * Copyright 2017-${date.year} ${pkg.author}
 *
 * Released on: ${date.month} ${date.day}, ${date.year}
 */
`.trim()}\n\n`;

gulp.task('ugjs', function (done) {

    var sys_path = './ls-sys-assets/src/main/resources/assets/js/';
    var static_path = './ls-front-assets/src/main/resources/assets/js/';

    // sys
    gulp.src([sys_path + 'core.js'])
        .pipe(rename({suffix: '-min'}))
        .pipe(gulpif(argv.env === 'build', uglify()))
        .pipe(gulp.dest(sys_path));

    // static
    gulp.src([static_path + 'core.js'])
        .pipe(rename({suffix: '-min'}))
        .pipe(gulpif(argv.env === 'build', uglify({
            compress: {
                sequences: true,
                unsafe: false,
                typeofs: false
            },
            output: {
                // comments: ""
                //ast: true,
                //code: true,
                // beautify: true
                // ascii_only: true
            },
            ie8: true
        })))
        .pipe(plugins.header(license))
        .pipe(gulp.dest(static_path));

    return gulp.src([
        static_path + 'jquery-min.js',
        static_path + 'easing-min.js',
        static_path + 'core-min.js'
    ]).pipe(rename({suffix: '-min'}))
        .pipe(concat('common.min.js'))
        .pipe(gulp.dest(static_path))

});