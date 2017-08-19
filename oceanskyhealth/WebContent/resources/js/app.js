'use strict';

//var App = {};

var App = angular.module('thisApp', ['ui.router', 'ngMaterial', 'ngFileUpload', 'App.filters', 'App.services', 'App.directives']);
App.constant('context', '/oceanskyhealth');

// Declare app level module which depends on filters, and services
App.config(['$stateProvider', '$urlRouterProvider','$resourceProvider',  function ($stateProvider, $urlRouterProvider, $resourceProvider) {
	/* 
	$httpProvider.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
	 $httpProvider.defaults.transformRequest.unshift(function (data, headersGetter) {
	     var key, result = [], response;
	     if(typeof data == "string") { //$http support
	            response = data;
	     } else {
	            for (key in data) {
	                if (data.hasOwnProperty(key)) {
	                    result.push(encodeURIComponent(key) + "=" + encodeURIComponent(data[key]));
	                }
	            }
	            response = result.join("&");
	        }
	        return response;
	});   
	*/
	$stateProvider
		.state('prodList', {
	        url: '/products',
	        params: {
	            prd: null,
	            idx: null
	        },
	        templateUrl: 'admin/products/prodlist',
	        controller: 'AdminProdController as aplc', 
	    })    
	    .state('prodDetails', {
	        url: '/prodDetails',
	        params: {
	            prd: null,
	            idx: null
	        },
	        templateUrl: 'admin/products/proddetails',
	        controller: 'AdminProdDetailController as apcDetails', 
	    })    
	    .state('prodNew', {
	        url: '/newproducts',
	        templateUrl: 'admin/products/newprod',
	        controller: 'AdminEditProdController as apclNew', 
	    })    
	    .state('prodEdit', {
	        url: '/prodEdit/:idx',
	        params: {
	            prd: null,
	            idx: null
	        },
	        templateUrl: 'admin/products/editprod',
	        controller: 'AdminEditProdController as apclEdit', 
	    })    
	    .state('catTree', {	    	
	    url: '/categories',
        templateUrl: 'admin/categories/catlist',
        controller: 'CatController as catCtrl',
        //controllerAs: 'catCtrl',
    });

    $urlRouterProvider.otherwise('/products');
    $resourceProvider.defaults.stripTrailingSlashes = false;

}]);

