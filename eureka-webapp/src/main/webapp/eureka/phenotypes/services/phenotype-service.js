(function() {
    'use strict';

    angular
        .module('phenotypes')
        .factory('PhenotypeService', PhenotypeService);

    PhenotypeService.$inject = ['$http', '$q'];

    function PhenotypeService($http, $q) {

        return ({
            getSummarizedUserElements: getSummarizedUserElements
        });

        function getSummarizedUserElements() {
            return $http.get('/eureka-services/api/protected/dataelement?summarize=true')
                .then(handleSuccess, handleError);
        }

        function handleSuccess(response) {
            return response.data;
        }

        function handleError(response) {
            if (!angular.isObject(response.data) && !response.data) {
                return ($q.reject('An unknown error occurred.'));
            }
            return ($q.reject(response.data));
        }

    }

}());