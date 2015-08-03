'use strict';

angular.module('mytestApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


