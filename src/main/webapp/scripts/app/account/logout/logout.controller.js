'use strict';

angular.module('mytestApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
