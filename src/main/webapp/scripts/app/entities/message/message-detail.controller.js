'use strict';

angular.module('mytestApp')
    .controller('MessageDetailController', function ($scope, $rootScope, $stateParams, entity, Message, User) {
        $scope.message = entity;
        $scope.load = function (id) {
            Message.get({id: id}, function(result) {
                $scope.message = result;
            });
        };
        $rootScope.$on('mytestApp:messageUpdate', function(event, result) {
            $scope.message = result;
        });
    });
