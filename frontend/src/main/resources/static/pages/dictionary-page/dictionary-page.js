angular.module('home-organizer').controller('dictionaryPageController', function ($scope, $http, API_SERVER) {
    $scope.getDictionaries = function () {
        $http.get(API_SERVER)
            .then(function (response) {
                $scope.dictionaries = response.data.content;
            })
    }

    $scope.getDictionaries()
})