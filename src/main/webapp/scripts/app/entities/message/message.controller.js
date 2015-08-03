'use strict';

angular.module('mytestApp')
    .controller('MessageController', function ($scope, Message, ParseLinks) {
        $scope.messages = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Message.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.messages = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Message.get({id: id}, function(result) {
                $scope.message = result;
                $('#deleteMessageConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Message.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteMessageConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.message = {email: null, messageText: null, name: null, startDate: null, endDate: null, id: null};
        };
    });
