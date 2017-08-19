'use strict';

/**
 * CarController
 * @constructor
 */
App.controller('AdminProdController', ['ProdAdminService', 'CatService', 'FileUpload','$state', '$stateParams', function(ProdAdminService, CatService, FileUpload, $state, $stateParams, $http) {
	var self = this; 
	self.prod = {};

	self.selectedPcat = {};
	self.selectedCat = {};
	
	if($stateParams && $stateParams.prd){
		self.prod = $stateParams.prd;	
		//alert('$stateParams.prd=' + $stateParams.prd);
		if($stateParams.idx && $stateParams.idx >=0 ){
			//alert('$stateParams.idx=' + $stateParams.idx);
			//ProdAdminService.setProduct($stateParams.prd, $stateParams.idx);			
		}
	}
	self.products = ProdAdminService.getProducts();
	
	//self.isAdmin = true;
	self.fetchCategory = function() {
    	self.pcats = CatService.getService('fetchAll').query(    
		    function(result, responseHeaders){
		        console.log('Total cats received', result.length);
		    }, function(httpResponse){
		        console.log('Error while fetching category tree');
		    });
    };
	
    self.fetchCategory();
    /*
	self.fetchProdList = function() {
		self.products = ProdAdminService.getService('fetchAll').query(
			function(result, responseHeaders){}, 
		    function(httpResponse){
		    });
		self.fetchCategory();
	}
	*/
    
	self.loadProdByCat = function(cat){
		//alert('Load prod by cat ' + self.selectedCat.name + ' parent cat: ' + self.selectedPcat.name);
		self.products = ProdAdminService.getService('fetchByType/' + self.selectedPcat.name +'/'+ self.selectedCat.name).query(
			function(result, responseHeaders){}, 
		    function(httpResponse){
		       //console.log('Error while fetching category tree');
		    });
	}
	
	self.viewProdDetails = function(prod, index) {
		$state.go('prodDetails', {prd:prod, idx:index});
	}
        
    self.removeProd = function(prodNo) {
        //alert("delete prod ... "); 
    	ProdAdminService.getService('removeProduct/'+ prodNo).delete(
    		function(result, responseHeaders){
				if(!result.successful){
		    		//alert('cat remove - prams! result: ' + result);
					self.setError(result.messageKey);
				}else{
			    	self.fetchProdList();
				}
		        //console.log('Total cats received', result.length);
			    }, function() {
		    		
		    	});
    	/*
        $http.delete('products/removeProduct/'+ prodNo).success(function() {
            self.fetchProdList();
        }).error(function() {
            self.setError('Could not delete product');
        });
        */
        self.prod = {};
    };

    self.uploadFile = function(){
    	//alert('Uploading file ...')
        var file = self.myFile;
        console.log('file is ' );
        console.dir(file);
        var uploadUrl = "images";
        FileUpload.uploadFileToUrl(file, uploadUrl);
    };
    
    self.editProd = function(prod, index) {
        //alert('Edit prod with id: ' + prod.productNo)
        //$state.go('prodEdit', {prd:angular.toJson(prod)});
    	self.editIndex = index;
    	//alert("Editing element at index : " + index);
        $state.go('prodEdit', {prd:prod, idx:index});
    };

    self.setError = function(message) {
        self.error = true;
        self.errorMessage = message;
    };
   
	//self.fetchProdList();
}]);