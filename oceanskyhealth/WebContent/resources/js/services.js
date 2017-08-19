'use strict';

/* Services */

var AppServices = angular.module('App.services', ['ngResource']);

AppServices.value('version', '0.1');

AppServices.service('CatService', ['$resource','context', function($resource, context,  $scope) {
	var self = this; 
	self.pcats = [];
	
	self.getAllCats = function(){		
		self.pcats = self.getService('fetchAll').query(    
				function(data) {
				    // success handler
				}, function(error) {
				    // error handler
				});
		
		return self.pcats;
	}
	
	self.saveCat = function(cat){
		self.getService('saveCat').save(cat, function() {			
		});    	
	};
	
	self.savePcat = function(name){
		self.getService('savePcat').save(name, function() {
			self.pcats.push(pcat);
        });    	
	};
		
	self.getService = function(serviceType){
		return $resource(context + '/admin/categories/'+serviceType);
	};		
}]);

AppServices.service('ProdAdminService', ['$resource', '$http','context', function($resource, $http, context) {	
	this.products = [];
	
	this.addProduct = function(prod) {		
		this.setProduct(prod, -1);
	};
	
	this.saveProduct = function(prod, index) {
		var prod = this.getService('saveProduct').save(prod);
		if(index && index >= 0 ) {
			this.products[index] = prod;
		}else{
			this.products.push(prod);
		}
	};

	this.getProducts = function(){
		if(this.products.length == 0){
			this.products=this.getService('fetchAll').query(    
				    function(result, responseHeaders){
				        console.log('Total cats received', result.length);
				    }, function(httpResponse){
				        console.log('Error while fetching category tree');
				    });
		}
	    return this.products;
	};
		
	this.getService = function(serviceType){
		return $resource(context + '/admin/products/'+serviceType);
	};	
	
	this.saveWithImage = $resource(context +'/admin/products/saveProductWithImage', {}, {
        save: { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }
    });
			
	this.saveProdWithImage = function (prod, file, index) {
		var fd = new FormData();
		fd.append('prod', new Blob([angular.toJson(prod)], {
            type: "application/json"
        }));
        fd.append('file', file);
       
        var prod = $http.post(context +'/admin/products/saveProductWithImage', fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        })
        .success(function(){
        })
        .error(function(){
        });
        /*
        var prod = $resource(context +'/admin/products/saveProductWithImage', fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        }).save(params, function() {
        	
        });
        
        this.saveWithImage.save(fd, function() {
        	
        })
          */ 
        if(!angular.isUndefined(prod) && prod != null && index && index >= 0 ) {
			this.products[index] = prod;
		}else{
			this.products.push(prod);
		}
	};
	
}]);

AppServices.service('FileUpload', ['$http', 'context', function ($http, context) {
    this.uploadFileToUrl = function(file, uploadUrl){
        var fd = new FormData();
        fd.append('file', file);
        $http.post(context +'/admin/products/'+uploadUrl, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        })
        .success(function(){
        })
        .error(function(){
        });
    }
}]);

AppServices.service('MathService', function() {
    this.add = function(a, b) { return a + b };
    
    this.subtract = function(a, b) { return a - b };
    
    this.multiply = function(a, b) { return a * b };
    
    this.divide = function(a, b) { return a / b };
});

AppServices.service('CalculatorService', function(MathService){
    
    this.square = function(a) { return MathService.multiply(a,a); };
    this.cube = function(a) { return MathService.multiply(a, MathService.multiply(a,a)); };

});