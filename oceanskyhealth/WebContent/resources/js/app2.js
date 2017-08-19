'use strict';

//var App = {};

var App = angular.module('thisApp2', ['ui.router', 'ngFileUpload', 'App.filters', 'App.services', 'App.directives']);
App.constant('context', '/oceanskyhealth');

// Declare app level module which depends on filters, and services
App.config(['$stateProvider', '$urlRouterProvider','$resourceProvider',  function ($stateProvider, $urlRouterProvider, $resourceProvider) {
        
	$stateProvider
		.state('products', {
	        url: '/products',
	        templateUrl: 'products/layout',
	        controller: 'ProdController as prodCtrl', 
	    })    
	    .state('categories', {	    	
	    url: '/categories',
        templateUrl: 'categories/layout',
        controller: 'CatController as catCtrl',
        //controllerAs: 'catCtrl',
    });

    $urlRouterProvider.otherwise('/products');
    $resourceProvider.defaults.stripTrailingSlashes = false;

}]);

