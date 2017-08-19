'use strict';

/**
 * CarController
 * @constructor
 */
App.controller('AdminEditProdController', ['ProdAdminService', 'CatService', 'FileUpload','$state', '$stateParams', function(ProdAdminService, CatService, FileUpload, $state, $stateParams, $http) {
	var self = this; 
	self.prod = {};
	self.imageSelected = false;
	self.index = -1;
	if($stateParams && $stateParams.prd){		
		//ToDO : fetch prod by productNo
		self.prod = $stateParams.prd;
		self.selectedPcat = $stateParams.prd.cat.parent;
		self.selectedCat = $stateParams.prd.cat;
		if($stateParams.idx){
			console.log('EditProd: $stateParams.idx=' + $stateParams.idx);
			self.index = $stateParams.idx;
		}
		//alert('self.selectedPcat='+self.selectedPcat + ' self.selectedCat=' + self.selectedCat);
	}
	
	//self.isAdmin = true;
	self.fetchCategory = function() {
    	self.pcats = CatService.getService('fetchAll').query(    
		    function(result, responseHeaders){
		        console.log('Total cats received', result.length);
		    }, function(httpResponse){
		        console.log('Error while fetching category tree');
		    });
    };
	
    self.saveProd = function() {
   	 	self.resetError();
   	 	self.prod.tags = [];
   	 	self.prod.cat = self.selectedCat;
        self.selectedPcat.cats = null; 
        self.prod.cat.parent = self.selectedPcat;
                        
        //alert('saving Prod with image! ')
        if(self.myFile) {
        	//alert('Save prod with image');
        	//alert('File name: ' + self.myFile.name);
        	ProdAdminService.saveProdWithImage(self.prod, self.myFile, self.index);
        }else{
        	//alert('Save prod');
        	ProdAdminService.saveProduct(self.prod, self.index); 
        }
    };
   
    self.markImgForDelete = function (img) {
    	//alert("Calling markImgForDelete for :  " + img)

    	if(self.imageSelected){
        	//alert("Mark image for deletion:  " + img)

    		var index = self.prod.images.indexOf(img);
    		self.prod.images.splice(index, 1); 
    		self.imageSelected = false;
    	}
    }
    
    self.updateProd = function() {
    	self.saveProd();    	
    	$state.go('prodList', {prd:self.prod, idx:self.index});
    };
    
    self.resetProdForm = function() {
        self.resetError();
        self.prod = {};
        self.selectedCat = null;
        self.selectedPcat = null;
        self.editMode = false;
    };
    

    self.addNewProd = function() {
        self.saveProd();
    	$state.go('prodList', {prd:self.prod, idx:-1});
    };

    
    self.setProdCat = function(cat){
    	self.prod.cat = cat;
    	self.prod.cat.parent = self.selectedPcat
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