'use strict';

/**
 * CatController
 * @constructor , 'CatService'
 */
App.service('AppService', ['$http', function($http) {
	var self = this;
	self.fetchCategory = function() {
        return $http.get('categories/pcats.json');
    };
    
        
    self.addNewCat = function(name) {           
        var cat = {};
        cat.name = name;
        var pcat = {};
        pcat.name = self.selectedPcat.name;
        cat.parent = pcat;
        //alert("add new cat ... " + cat); 
        $http.post('categories/saveCat/', cat).success(function() {
        	self.fetchCategory();
        }).error(function() {
        	self.setError('Could not add/update Category' + name);
        });
        self.selectedPcat = {};
        self.createCat = {};
        self.fetchCategory();
    };
    
    self.addNewPcat = function(name) {           
        var pcat = {};
        pcat.name = name;
        //alert("add new pcat ... " + name); 
        $http.post('categories/savePcat/', pcat).success(function() {
        	self.fetchCategory();            
        }).error(function() {
        	self.setError('Could not add/update Parent Category' + name);
        });
        self.createPcat = {};
    };
    
    self.removeCat = function(name) {
        //alert("delete cat " + name);        
        $http.delete('categories/removeCat/'+name).success(function() {
        	self.fetchCategory();            
        }).error(function() {
        	self.setError('Could not delete cat ' +name);
        });
    };
    
    self.removePcat = function(name) {
        //alert("delete pcat " + name);        
        $http.delete('categories/removePcat/'+name).success(function() {
        	self.fetchCategory();            
        }).error(function() {
        	self.setError('Could not delete parent cat ' +name);
        });
    };
    
    self.resetError = function() {
    	self.error = false;
    	self.errorMessage = '';
    };

    self.setError = function(message) {
    	self.error = true;
    	self.errorMessage = message;
    };
    
    self.fetchCategory();
}]);