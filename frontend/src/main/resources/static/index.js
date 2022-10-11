(function () {
    'use strict';

    angular
        .module('home-organizer', ['ngRoute', 'ngStorage'])
        .config(config)
        .run(run);

    function config($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'pages/home/home.html',
                controller: 'homeController'
            })
            .when('/dictionaries', {
                templateUrl: 'pages/dictionary-page/dictionary-page.html',
                controller: 'dictionaryPageController'
            })
            .when('/dictionary/:dictId', {
                templateUrl: 'pages/dictionary/dictionary.html',
                controller: 'dictionaryPageController'
            })
            .otherwise({
                redirectTo: '/'
            });
    }

    function run() {
    }
})();

angular.module('home-organizer').constant('API_SERVER', 'http://localhost:8081/ho/api/v1');

angular.module('home-organizer').controller('indexController', function ($scope, $http, $location, $localStorage) {


});
